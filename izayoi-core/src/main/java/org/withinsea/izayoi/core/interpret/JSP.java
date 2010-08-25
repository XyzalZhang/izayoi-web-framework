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
import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

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


    public static boolean interpret(Object jspobj, HttpServletRequest request) {
        InterpretHelper interpretHelper = (InterpretHelper) request.getAttribute(HELPER_ATTR);
        try {
            interpretHelper.interpret(jspobj);
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


    protected static final String HELPER_ATTR = JSP.class.getCanonicalName() + ".interpretHelper";

    protected static final String INTERPRET_PREFIX =
            "<% if (" + JSP.class.getCanonicalName() + ".interpret(this, request)) return; %>";

    protected class CompiledJSP implements CompiledInterpreter {

        protected String path;
        protected boolean prefixed = false;

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
                    if (!this.prefixed) {
                        this.prefixed = true;
                        String code = codeContainer.get(path).getCode();
                        String prefix = INTERPRET_PREFIX;
                        if (!checkPageDirective(code, "pageEncoding"))
                            prefix += "<%@ page pageEncoding=\"UTF-8\" %>";
                        codeContainer.update(path, prefix + code, true);
                        ServletFilterUtils.forwardOrInclude(request, response, path);
//                        request.getRequestDispatcher(path).forward(request, response);
                        codeContainer.update(path, code, true);
                        interpreted = true;
                    }
                }
                synchronized (this) {

                }
                if (!interpreted) {
                    ServletFilterUtils.forwardOrInclude(request, response, path);
//                    request.getRequestDispatcher(path).forward(request, response);
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

        protected boolean checkPageDirective(String code, String attrname) {
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

    protected class InterpretHelper {

        protected Bindings bindings;
        protected Object result = null;
        protected Exception ex = null;

        public InterpretHelper(Bindings bindings) {
            this.bindings = bindings;
        }

        public void interpret(Object prototype) throws NoSuchMethodException {
            try {
                Object jspobj = jspCloneFactory.create(prototype.getClass());
                bind(jspobj, prototype);
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
        protected void bind(Object obj, Object prototype) throws Exception {
            for (Field f : obj.getClass().getDeclaredFields()) {
                int mod = f.getModifiers();
                if (!Modifier.isPrivate(mod) && !Modifier.isStatic(mod) &&
                        (f.get(obj) == null || f.get(obj).equals(f.get(prototype)))) {
                    f.setAccessible(true);
                    Object v = bindings.get(f.getName());
                    if (v != null) {
                        f.set(obj, ParamUtils.cast(v, f.getType()));
                    }
                }
            }
        }
    }
}
