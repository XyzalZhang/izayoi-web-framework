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
 * The Original Code is the @PROJECT_NAME
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
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.bean.BeanFactory;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeContainer;
import org.withinsea.izayoi.core.exception.IzayoiException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-7-6
 * Time: 3:38:46
 */
public class JSP_ComplexTest implements CompilableInterpreter {


    @Resource
    CodeContainer codeContainer;

    @Resource
    BeanFactory jspCloneFactory;


    protected static final String HELPER_ATTR = JSP_ComplexTest.class.getCanonicalName() + ".interpretHelper";

    public static boolean interpret(HttpServletRequest request, Object protoObj) {
        return interpret(request, protoObj, null);
    }

    public static boolean interpret(HttpServletRequest request, Object protoObj, Class<?> innerClaz) {
        InterpretHelper interpretHelper = (InterpretHelper) request.getAttribute(HELPER_ATTR);
        try {
            interpretHelper.interpret(protoObj, innerClaz);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {
        return (T) compile(code, importedClasses).interpret(bindings);
    }

    @Override
    public CompiledInterpreter compile(Code code, String... importedClasses) throws IzayoiException {
        return new CompiledJSP(code.getPath().getPath());
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

        public void interpret(Object prototype, Class<?> innerClaz) throws NoSuchMethodException {
            try {
                Object jspobj = jspCloneFactory.create(prototype.getClass());
                if (innerClaz == null) {
                    bind(jspobj, prototype);
                } else {
                    Object inner = jspCloneFactory.create(innerClaz);
                    bind(jspobj, prototype, inner);
                }
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
                    if (now == null || now == proto) {
                        Object v = bindings.get(getName(protoField));
                        if (v != null) {
                            protoField.set(obj, ParamUtils.cast(v, protoField.getType()));
                        }
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        protected void bind(Object obj, Object protoObj, Object innerObj) throws Exception {
            Class<?> protoClaz = protoObj.getClass();
            Class<?> innerClaz = innerObj.getClass();
            for (Field innerField : innerClaz.getDeclaredFields()) {
                int mod = innerField.getModifiers();
                if (!Modifier.isStatic(mod)) {
                    Field protoField = protoClaz.getDeclaredField(innerField.getName());
                    protoField.setAccessible(true);
                    innerField.setAccessible(true);
                    Object proto = protoField.get(protoObj);
                    Object inner = innerField.get(innerObj);
                    if (inner == null || inner == proto) {
                        Object v = bindings.get(getName(innerField));
                        if (v != null) {
                            protoField.set(obj, ParamUtils.cast(v, protoField.getType()));
                        }
                    } else {
                        protoField.set(obj, inner);
                    }
                }
            }
        }

        protected String getName(Field f) {
            Resource r = f.getAnnotation(Resource.class);
            Inject i = f.getAnnotation(Inject.class);
            Named n = f.getAnnotation(Named.class);
            if (r != null && !r.name().equals("")) {
                return r.name();
            } else if (i != null && n != null) {
                return n.value();
            } else {
                return f.getName();
            }
        }
    }


    protected static class Tricker {

        protected static final String INNER_CLASS_NAME = "__JspScript";

        protected static List<String> ANNOTATION_NAMES = Arrays.asList(
                "Resource", "Resources", "EJB", "InjectionComplete", "WebServiceRef", "Inject", "Named");

        public static String trickJsp(String code) {

            final StringBuffer declarations = new StringBuffer();
            String clearCode = StringUtils.replaceAll(code, "<%!([\\s\\S]*?)%>", new StringUtils.Replace() {
                @Override
                public String replace(String... groups) {
                    String declaration = groups[1];
                    declarations.append(declaration);
                    return Tricker.removeAnnotations(groups[0]);
                }
            });

            if (clearCode.equals(code)) {
                String prefix = (Tricker.checkPageDirective(code, "pageEncoding") ? "" : "<%@ page pageEncoding=\"UTF-8\" %>") +
                        "<% if (" + JSP_ComplexTest.class.getCanonicalName() + ".interpret(request, this)) return; %>";
                return prefix + code;
            } else {
                String prefix = (Tricker.checkPageDirective(code, "pageEncoding") ? "" : "<%@ page pageEncoding=\"UTF-8\" %>") +
                        "<% if (" + JSP_ComplexTest.class.getCanonicalName() + ".interpret(request, this, " + INNER_CLASS_NAME + ".class)) return; %>";
                String innerClass = "<%! public static class " + INNER_CLASS_NAME + " { " + declarations.toString() + " } %>";
                return prefix + clearCode + innerClass;
            }
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

        public static String removeAnnotations(String code) {
            StringBuffer ori = new StringBuffer(code);
            StringBuffer covered = new StringBuffer(code);
            coverString(covered);
            int len = code.length();
            LinkedList<Integer[]> stack = new LinkedList<Integer[]>();
            for (String n : ANNOTATION_NAMES) {
                int s, e = -1;
                while (true) {
                    s = covered.indexOf("@" + n, e + 1);
                    if (s < 0) break;
                    e = s + n.length() + 1;
                    while (e < len && covered.charAt(e) == ' ' || covered.charAt(e) == '\t'
                            || covered.charAt(e) == '\n' || covered.charAt(e) == '\r') {
                        e++;
                    }
                    if (e >= len) break;
                    if (covered.charAt(e) == '(') {
                        e = covered.indexOf(")", e + 1);
                    }
                    if (e < 0) break;
                    stack.push(new Integer[]{s, e + 1});
                }
            }
            for (Integer[] range : stack) {
                int s = range[0], e = range[1];
                ori.replace(s, e, ori.substring(s, e).replaceAll(".", ""));
            }
            return ori.toString();
        }

        protected static void coverString(StringBuffer buf) {
            int s, e = -1;
            while (true) {
                s = buf.indexOf("\"", e + 1);
                if (s < 0) break;
                e = s;
                while (true) {
                    e = buf.indexOf("\"", e + 1);
                    if (e < 0) break;
                    int iSlash;
                    for (iSlash = e - 1; (iSlash > s) && (buf.charAt(iSlash) == '\\'); iSlash--) {
                    }
                    if ((e - iSlash + 1) % 2 == 0) break;
                }
                if (e < 0) break;
                for (int i = s; i <= e; i++) {
                    buf.setCharAt(i, '*');
                }
            }
        }
    }
}