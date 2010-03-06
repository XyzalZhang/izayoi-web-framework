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

package org.withinsea.izayoi.cortile.core.compiler;

import org.withinsea.izayoi.commons.util.Varstack;
import org.withinsea.izayoi.core.dependency.DependencyManager;
import org.withinsea.izayoi.core.dependency.DependencyUtils;
import org.withinsea.izayoi.core.interpreter.ImportableInterpreter;
import org.withinsea.izayoi.core.interpreter.Interpreter;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-5
 * Time: 2:32:23
 */
public class ELHelper {

    protected static final String HELPER_ATTR = ELHelper.class.getCanonicalName() + ".HELPER";

    protected String elType;
    protected DependencyManager dependencyManager;
    protected Map<String, Interpreter> interpreters;

    public Helper getHelper(HttpServletRequest request) {
        Helper helper = (Helper) request.getAttribute(HELPER_ATTR);
        if (helper == null) {
            helper = new Helper(request);
            request.setAttribute(HELPER_ATTR, helper);
        }
        return helper;
    }

    public class Helper {

        protected HttpServletRequest request;
        protected Context context;

        protected Helper(HttpServletRequest request) {
            this.request = request;
            this.context = Context.newInstance(dependencyManager, request);
        }

        public Object eval(String el, boolean forOutput) {
            Interpreter interpreter = interpreters.get(interpreters.containsKey(elType) ? elType : "default");
            Object ret;
            try {
                if (interpreter instanceof ImportableInterpreter) {
                    String[] importedClasses = context.importedClasses.toArray(new String[context.importedClasses.size()]);
                    ret = ((ImportableInterpreter) interpreter).interpret(el, context.varstack, elType, importedClasses);
                } else {
                    ret = interpreter.interpret(el, context.varstack, elType);
                }
            } catch (Exception e) {
                ret = null; // silent exception stack trace
            }
            return (!forOutput) ? ret : (ret == null) ? "" : ret;
        }

        public void imports(String classes) {
            if (classes != null && !classes.trim().equals("")) {
                context.importedClasses.addAll(Arrays.asList(classes.replaceAll("\\s+", "").split(",")));
            }
        }

        public Varstack getVarstack() {
            return context.varstack;
        }
    }

    protected static class Context {

        protected Set<String> importedClasses;
        protected Varstack varstack;

        protected Context(Set<String> importedClasses, Varstack varstack) {
            this.importedClasses = importedClasses;
            this.varstack = varstack;
        }

        public static Context newInstance(DependencyManager dependencyManager, HttpServletRequest request) {
            Varstack varstack = new Varstack();
            {
                varstack.push(DependencyUtils.asMap(dependencyManager, request));
                varstack.push();
                varstack.put("varstack", varstack);
                varstack.push();
            }
            return new Context(new LinkedHashSet<String>(), varstack);
        }
    }

    public void setInterpreters(Map<String, Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    public void setDependencyManager(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    public void setElType(String elType) {
        this.elType = elType;
    }
}
