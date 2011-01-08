package org.withinsea.izayoi.cloister.web.feature.jspscript;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.feature.postscript.ScriptEngine;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.common.util.IOUtils;

import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-4
 * Time: 上午8:36
 */
public class JspScriptEngine implements ScriptEngine {

    public static final RuntimeContext RUNTIME_CONTEXT = new RuntimeContext();

    protected ServletContext servletContext;
    protected String encoding;

    public JspScriptEngine(ServletContext servletContext, String encoding) {
        this.servletContext = servletContext;
        this.encoding = encoding;
    }

    @Override
    public CompiledScript compile(Environment.Codefile postscript) throws CloisterException {
        return new CompiledJsp(postscript);
    }

    protected String trickJsp(String code) {

        String pageDirectiveName = "pageEncoding";

        boolean hasEncoding = false;
        int i = -1;
        while ((i = code.indexOf("<%@", i + 1)) >= 0) {
            int end = code.indexOf("%>", i + 1);
            int attr = code.indexOf(pageDirectiveName, i + 1);
            int afterStart = code.indexOf("<%", attr + pageDirectiveName.length());
            int afterEnd = code.indexOf("%>", attr + pageDirectiveName.length());
            if (attr >= 0 && afterEnd >= 0 && end == afterEnd && (afterStart < 0 || afterStart > afterEnd)) {
                hasEncoding = true;
                break;
            }
        }

        String prefix = (hasEncoding ? "" : "<%@ page pageEncoding=\"" + encoding + "\" %>") +
                "<% if (" + JspScriptEngine.class.getCanonicalName() + ".execute(request, this)) return; %>";
        return prefix + code;
    }

    protected String getServletPath(Environment.Codefile jspfile) {
        return jspfile.getPath();
    }

    protected void updateJsp(Environment.Codefile jspfile, String code) throws IOException {
        String realpath = servletContext.getRealPath(getServletPath(jspfile).replace("%20", " "));
        IOUtils.write(code, new File(realpath), encoding);
    }

    protected Object createJspBean(Class<?> jspclass) throws Exception {
        return jspclass.newInstance();
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public static class RuntimeContext {

        protected static ThreadLocal<HttpServletRequest> HTTP_REQ = new ThreadLocal<HttpServletRequest>();
        protected static ThreadLocal<HttpServletResponse> HTTP_RESP = new ThreadLocal<HttpServletResponse>();

        public static boolean isEmpty() {
            return (HTTP_REQ.get() == null || HTTP_RESP.get() == null);
        }

        public static void set(HttpServletRequest httpReq, HttpServletResponse httpResp) {
            HTTP_REQ.set(httpReq);
            HTTP_RESP.set(httpResp);
        }

        public static void clear() {
            HTTP_REQ.remove();
            HTTP_RESP.remove();
        }
    }

    protected class CompiledJsp implements CompiledScript {

        protected Environment.Codefile jspfile;
        protected boolean tricked = false;

        public CompiledJsp(Environment.Codefile jspfile) {
            this.jspfile = jspfile;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T run(Map<String, Object> context, String entrance) throws CloisterException {
            Object result = run(context);
            return (T) result;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T run(Map<String, Object> context) throws CloisterException {

            HttpServletRequest httpReq = (HttpServletRequest) context.get("request");
            HttpServletResponse httpResp = (HttpServletResponse) context.get("response");

            if (httpReq == null || httpResp == null) {
                httpReq = RuntimeContext.HTTP_REQ.get();
                httpResp = RuntimeContext.HTTP_RESP.get();
                if (httpReq == null || httpResp == null) {
                    throw new CloisterException("JspScriptEngine require HttpServletRequest for runtime context");
                }
            }

            String path = getServletPath(jspfile);

            Object originalInterpretHelper = httpReq.getAttribute(ATTR_HELPER);
            ExecuteHelper executeHelper = new ExecuteHelper(context);
            httpReq.setAttribute(ATTR_HELPER, executeHelper);
            try {
                boolean interpreted = false;
                synchronized (this) {
                    if (!this.tricked) {
                        this.tricked = true;
                        String code = IOUtils.toString(jspfile.getInputStream(), encoding);
                        String trickedCode = trickJsp(code);
                        updateJsp(jspfile, trickedCode);
                        try {
                            httpReq.getRequestDispatcher(path).include(httpReq, httpResp);
                        } finally {
                            updateJsp(jspfile, code);
                        }
                        interpreted = true;
                    }
                }
                synchronized (this) {

                }
                if (!interpreted) {
                    httpReq.getRequestDispatcher(path).include(httpReq, httpResp);
                }
            } catch (Exception e) {
                throw new CloisterException(e);
            } finally {
                if (originalInterpretHelper == null) {
                    httpReq.removeAttribute(ATTR_HELPER);
                } else {
                    httpReq.setAttribute(ATTR_HELPER, originalInterpretHelper);
                }
            }

            if (executeHelper.ex != null) {
                throw new CloisterException(executeHelper.ex);
            } else {
                return (T) executeHelper.result;
            }
        }
    }


    public static final String ATTR_HELPER = JspScriptEngine.class.getCanonicalName() + ".ATTR_HELPER";

    public static boolean execute(HttpServletRequest request, Object protoObj) {
        ExecuteHelper executeHelper = (ExecuteHelper) request.getAttribute(ATTR_HELPER);
        try {
            executeHelper.interpret(protoObj);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    protected class ExecuteHelper {

        protected Map<String, Object> context;
        protected Object result = null;
        protected Exception ex = null;

        public ExecuteHelper(Map<String, Object> context) {
            this.context = context;
        }

        public void interpret(Object protoObj) throws NoSuchMethodException {
            try {
                Object jspobj = createJspBean(protoObj.getClass());
                bind(jspobj, protoObj);
                Method m = jspobj.getClass().getDeclaredMethod("execute");
                m.setAccessible(true);
                if (m.getReturnType().equals(Void.class)) {
                    m.invoke(jspobj);
                } else {
                    result = m.invoke(jspobj);
                }
            } catch (NoSuchMethodException e) {
                throw e;
            } catch (Exception e) {
                ex = e;
            }
        }

        @SuppressWarnings("unchecked")
        protected void bind(Object obj, Object protoObj) throws Exception {
            Class<?> protoClaz = protoObj.getClass();
            for (Field protoField : protoClaz.getDeclaredFields()) {
                int mod = protoField.getModifiers();
                if (!Modifier.isStatic(mod)) {
                    protoField.setAccessible(true);
                    Object proto = protoField.get(protoObj);
                    Object now = protoField.get(obj);
                    if (!injected(proto, now)) {
                        Object v = context.get(getName(protoField));
                        if (v != null) {
                            protoField.set(obj, ParamUtils.cast(v, protoField.getType()));
                        }
                    }
                }
            }
        }

        protected boolean injected(Object proto, Object now) {
            if (now == null || now == proto || now.equals(proto)) return false;
            if (proto == null || now.getClass() != proto.getClass()) return true;
            Class<?> claz = now.getClass();
            return !((claz.isArray() && ((Object[]) now).length == 0 && ((Object[]) proto).length == 0)
                    || (now instanceof Collection && ((Collection) now).isEmpty() && ((Collection) proto).isEmpty())
                    || (now instanceof Map && ((Map) now).isEmpty() && ((Map) proto).isEmpty()));
        }

        protected String getName(Field f) {
            Named n = f.getAnnotation(Named.class);
            return (n != null) ? n.value() : f.getName();
        }
    }
}
