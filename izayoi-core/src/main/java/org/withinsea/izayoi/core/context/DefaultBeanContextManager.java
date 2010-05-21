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

package org.withinsea.izayoi.core.context;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-15
 * Time: 4:43:36
 */
public class DefaultBeanContextManager implements BeanContextManager {

    protected BeanContext globalContext;

    @Override
    public BeanContext getContext(Scope scope) {
        BeanContext beanContext = new ScopeContext(globalContext, scope);
        try {
            scope.setAttribute("beanContext", beanContext);
        } catch (UnsupportedOperationException e) {
            // do nothing
        }
        return beanContext;
    }

    public void setGlobalContext(BeanContext globalContext) {
        this.globalContext = globalContext;
    }

    /**
     * Created by Mo Chen <withinsea@gmail.com>
     * Date: 2010-5-15
     * Time: 4:50:32
     */
    protected static class ScopeContext implements BeanContext {

        protected final BeanContext baseContext;
        protected final Scope scope;

        public ScopeContext(Scope scope) {
            this(null, scope);
        }

        public ScopeContext(BeanContext baseContext, Scope scope) {
            this.baseContext = baseContext;
            this.scope = scope;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getBean(String name) {
            Object obj = scope.getConstant(name);
            if (obj == null) obj = scope.getAttribute(name);
            if (obj == null && baseContext != null) obj = baseContext.getBean(name);
            return (T) obj;
        }

        @Override
        public <T> void setBean(String name, T object) {
            scope.setAttribute(name, object);
        }
    }
}
