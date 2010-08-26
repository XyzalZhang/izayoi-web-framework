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

package org.withinsea.izayoi.core.scope;

import javax.servlet.ServletContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:16:18
 */
public class Application extends InheritedScope<Singleton> {

    protected final ServletContext servletContext;

    public Application(javax.servlet.ServletContext servletContext) {
        super(new Singleton());
        this.servletContext = servletContext;
    }

    @Override
    public void setScopeAttribute(String name, Object obj) {
        servletContext.setAttribute(name, obj);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getScopeConstant(String name) {
        Object obj = name.equals("application") ? servletContext
                : name.equals("servletContext") ? servletContext
                : null;
        return (T) obj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getScopeAttribute(String name) {
        return (T) servletContext.getAttribute(name);
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}