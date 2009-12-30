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
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.util.HttpContextMap;
import org.withinsea.izayoi.cortile.util.HttpParameterMap;
import org.withinsea.izayoi.cortile.util.Varstack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 0:29:30
 */
public class MVEL2ELInterpreter implements ELInterpreter {

    @Override
    public String compileEL(String el) {
        return MVEL2ELInterpreter.class.getCanonicalName() + ".eval(\"" + el + "\", pageContext)";
    }

    public static Object eval(String el, PageContext context) {
        try {
            ELContext elContext = ELContext.getContext(context);
            return MVEL.eval(elContext.getImports() + el, elContext.getVarstack());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void imports(String imports, PageContext context) {
        ELContext.getContext(context).setImports("import " + imports.replaceAll(",", "; import ") + ";");
    }

    public static Varstack varstack(PageContext context) {
        return ELContext.getContext(context).getVarstack();
    }

    protected static class ELContext {

        protected static final String EL_CONTEXT_ATTR_NAME = MVEL2ELInterpreter.class.getCanonicalName() + ".EL_CONTEXT";

        protected Varstack varstack;
        protected String imports;

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

                String imports = "import java.util.*;";
                elContext.setImports(imports);
            }
            context.getRequest().setAttribute(EL_CONTEXT_ATTR_NAME, elContext);

            return elContext;
        }

        public String getImports() {
            return imports;
        }

        public void setImports(String imports) {
            this.imports = imports;
        }

        public Varstack getVarstack() {
            return varstack;
        }

        public void setVarstack(Varstack varstack) {
            this.varstack = varstack;
        }
    }
}