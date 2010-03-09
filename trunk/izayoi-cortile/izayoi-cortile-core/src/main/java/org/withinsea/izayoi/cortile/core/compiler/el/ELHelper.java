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

package org.withinsea.izayoi.cortile.core.compiler.el;

import org.withinsea.izayoi.core.bindings.BindingsManager;
import org.withinsea.izayoi.core.bindings.Varstack;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.core.interpreter.Interpreter;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-5
 * Time: 2:32:23
 */
public class ELHelper {

    protected static final String HELPER_ATTR = ELHelper.class.getCanonicalName() + ".HELPER";

    protected String elType;
    protected BindingsManager bindingsManager;
    protected InterpretManager interpretManager;
    protected Map<String, Interpreter> interpreters;

    public synchronized Helper getHelper(HttpServletRequest request) {
        Helper helper = (Helper) request.getAttribute(HELPER_ATTR);
        if (helper == null) {
            helper = new Helper(request);
            request.setAttribute(HELPER_ATTR, helper);
        }
        return helper;
    }

    public class Helper {

        protected final Set<String> importedClasses;
        protected final Deque<String> elTypeStack = new LinkedList<String>();
        protected final Varstack varstack = new Varstack();

        protected Helper(HttpServletRequest request) {
            this.importedClasses = new LinkedHashSet<String>();
            elTypeStack.push(elType);
            varstack.push(bindingsManager.getBindings(request));
            varstack.push();
        }

        public Object eval(String el, boolean forOutput) {
            String elType = elTypeStack.peek();
            Object ret;
            try {
                ret = interpretManager.interpret(el, varstack, elType, importedClasses.toArray(new String[importedClasses.size()]));
            } catch (Exception e) {
                ret = null; // silent exception stack trace
            }
            return (!forOutput) ? ret : (ret == null) ? "" : ret;
        }

        public void imports(String classes) {
            if (classes != null && !classes.trim().equals("")) {
                importedClasses.addAll(Arrays.asList(classes.replaceAll("\\s+", "").split(",")));
            }
        }

        public void bind(String key, Object value) {
            varstack.put(key, value);
        }

        public synchronized void scope(String elType, Map<String, Object> bindings) {
            if (bindings == null) {
                varstack.push();
            } else {
                varstack.push(bindings);
            }
            elTypeStack.push((elType != null) ? elType : elTypeStack.peek());
        }

        public synchronized void scopeEnd() {
            elTypeStack.pop();
            varstack.pop();
        }
    }

    public void setInterpreters(Map<String, Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    public void setDependencyManager(BindingsManager bindingsManager) {
        this.bindingsManager = bindingsManager;
    }

    public InterpretManager getInterpretManager() {
        return interpretManager;
    }

    public void setInterpretManager(InterpretManager interpretManager) {
        this.interpretManager = interpretManager;
    }

    public void setElType(String elType) {
        this.elType = elType;
    }
}
