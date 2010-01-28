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

package org.withinsea.izayoi.cortile.jsp;

import org.mvel2.MVEL;
import org.withinsea.izayoi.commons.servlet.HttpContextMap;
import org.withinsea.izayoi.commons.servlet.HttpParameterMap;
import org.withinsea.izayoi.commons.util.Varstack;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 0:29:30
 */
public class MVEL2ELInterpreter implements ELInterpreter {

    @Override
    public String compileInit(String importsEl) {
        return Varstack.class.getCanonicalName() + " varstack=" +
                MVEL2ELInterpreter.class.getCanonicalName() + ".varstack(pageContext);" +
                MVEL2ELInterpreter.class.getCanonicalName() + ".imports(\"" + importsEl + "\", pageContext);";
    }

    @Override
    public String compileEL(String el) {
        return MVEL2ELInterpreter.class.getCanonicalName() + ".eval(\"" +
                el.replace("\n", "").replace("\r", "") + "\", pageContext)";
    }

    @SuppressWarnings("unused")
    public static Varstack varstack(PageContext context) {
        return ELContext.getContext(context).getVarstack();
    }

    @SuppressWarnings("unused")
    public static Object eval(String el, PageContext context) {
        try {
            ELContext elContext = ELContext.getContext(context);
            return MVEL.eval(elContext.getImportsEL() + el, elContext.getVarstack());
        } catch (Exception e) {
            // temporary silent exception stack trace
            // TODO: log this exception
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static void imports(String importsEl, PageContext context) {
        ELContext.getContext(context).setImportsEL(importsEl);
    }

    protected static class ELContext {

        protected static final String EL_CONTEXT_ATTR_NAME = MVEL2ELInterpreter.class.getCanonicalName() + ".EL_CONTEXT";

        protected Varstack varstack;
        protected String importsEl;

        public static ELContext getContext(PageContext context) {

            Object provided = context.getRequest().getAttribute(EL_CONTEXT_ATTR_NAME);
            if (provided != null && provided instanceof ELContext) {
                return (ELContext) provided;
            }

            ELContext elContext = new ELContext();
            {
                HttpParameterMap paramMap = new HttpParameterMap((HttpServletRequest) context.getRequest());
                HttpContextMap contextMap = new HttpContextMap(context);

                Varstack varstack = new Varstack();
                {
                    varstack.push(paramMap);
                    varstack.push(contextMap);
                    varstack.push();
                    {
                        varstack.put("params", paramMap);
                        varstack.put("application", context.getServletContext());
                        varstack.put("session", context.getSession());
                        varstack.put("request", context.getRequest());
                        varstack.put("pageContext", context);
                        varstack.put("varstack", varstack);
                    }
                    varstack.push();
                }
                elContext.setVarstack(varstack);

                String imports = "";
                elContext.setImportsEL(imports);
            }
            context.getRequest().setAttribute(EL_CONTEXT_ATTR_NAME, elContext);

            return elContext;
        }

        public String getImportsEL() {
            return importsEl;
        }

        public void setImportsEL(String importsEl) {
            this.importsEl = importsEl;
        }

        public Varstack getVarstack() {
            return varstack;
        }

        public void setVarstack(Varstack varstack) {
            this.varstack = varstack;
        }
    }
}