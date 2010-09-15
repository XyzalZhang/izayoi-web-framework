/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF
 *
 * ANY KIND, either express or implied. See the License for the specific language governing rights and
 *
 * limitations under the License.
 *
 * The Original Code is the IZAYOI web framework.
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.core.interpret;

import org.withinsea.izayoi.commons.servlet.ParamUtils;
import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.core.bean.BeanFactory;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeContainer;
import org.withinsea.izayoi.core.exception.IzayoiException;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-7-6
 * Time: 3:38:46
 */
public class JSP implements CompilableInterpreter {


    @Resource
    CodeContainer codeContainer;

    @Resource
    BeanFactory jspCloneFactory;


    @Override
    @SuppressWarnings("unchecked")
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {
        return (T) compile(code, importedClasses).interpret(bindings);
    }

    @Override
    public CompiledInterpreter compile(Code code, String... importedClasses) throws IzayoiException {
        return new CompiledJSP(code.getPath().getPath());
    }


    protected static final String HELPER_ATTR = JSP.class.getCanonicalName() + ".interpretHelper";

    public static boolean interpret(HttpServletRequest request, Object protoObj) {
        InterpretHelper interpretHelper = (InterpretHelper) request.getAttribute(HELPER_ATTR);
        try {
            interpretHelper.interpret(protoObj);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    protected class CompiledJSP implements CompiledInterpreter {

        protected String path;
        protected boolean tricked = false;

        public CompiledJSP(String path) {
            this.path = path;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T interpret(Bindings bindings) throws IzayoiException {

            if (path == null
                    || !(bindings.get("request") instanceof HttpServletRequest)
                    || !(bindings.get("response") instanceof HttpServletResponse)) {
                return null;
            }

            HttpServletRequest request = (HttpServletRequest) bindings.get("request");
            HttpServletResponse response = (HttpServletResponse) bindings.get("response");

            Object originalInterpretHelper = request.getAttribute(HELPER_ATTR);
            InterpretHelper interpretHelper = new InterpretHelper(bindings);
            request.setAttribute(HELPER_ATTR, interpretHelper);
            try {
                boolean interpreted = false;
                synchronized (this) {
                    if (!this.tricked) {
                        this.tricked = true;
                        String code = codeContainer.get(path).getCode();
                        String trickedCode = Tricker.trickJsp(code);
                        codeContainer.update(path, trickedCode, true);
                        try {
                            ServletFilterUtils.forwardOrInclude(request, response, path);
                        } finally {
                            codeContainer.update(path, code, true);
                        }
                        interpreted = true;
                    }
                }
                synchronized (this) {

                }
                if (!interpreted) {
                    ServletFilterUtils.forwardOrInclude(request, response, path);
                }
            } catch (Exception e) {
                throw new IzayoiException(e);
            } finally {
                if (originalInterpretHelper == null) {
                    request.removeAttribute(HELPER_ATTR);
                } else {
                    request.setAttribute(HELPER_ATTR, originalInterpretHelper);
                }
            }

            if (interpretHelper.ex != null) {
                throw new IzayoiException(interpretHelper.ex);
            } else {
                return (T) interpretHelper.result;
            }
        }
    }

    protected class InterpretHelper {

        protected Bindings bindings;
        protected Object result = null;
        protected Exception ex = null;

        public InterpretHelper(Bindings bindings) {
            this.bindings = bindings;
        }

        public void interpret(Object protoObj) throws NoSuchMethodException {
            try {
                Object jspobj = jspCloneFactory.create(protoObj.getClass());
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
                        Object v = bindings.get(getName(protoField));
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

    protected static class Tricker {

        public static String trickJsp(String code) {
            String prefix = (checkPageDirective(code, "pageEncoding") ? "" : "<%@ page pageEncoding=\"UTF-8\" %>") +
                    "<% if (" + JSP.class.getCanonicalName() + ".interpret(request, this)) return; %>";
            return prefix + code;
        }

        protected static boolean checkPageDirective(String code, String attrname) {
            int i = -1;
            while ((i = code.indexOf("<%@", i + 1)) >= 0) {
                int end = code.indexOf("%>", i + 1);
                int attr = code.indexOf(attrname, i + 1);
                int afterStart = code.indexOf("<%", attr + attrname.length());
                int afterEnd = code.indexOf("%>", attr + attrname.length());
                if (attr >= 0 && afterEnd >= 0 && end == afterEnd && (afterStart < 0 || afterStart > afterEnd)) {
                    return true;
                }
            }
            return false;
        }
    }
}
