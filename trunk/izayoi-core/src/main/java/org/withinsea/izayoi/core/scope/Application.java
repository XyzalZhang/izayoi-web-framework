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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:16:18
 */
public class Application extends InheritedScope {

    protected final ServletContext servletContext;

    public Application(final ServletContext servletContext) {
        super(new Singleton(), null);
        this.servletContext = servletContext;
        this.declaredScope = new DeclaredScope();
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    protected static final Set<String> CONSTANT_NAMES = new LinkedHashSet<String>(Arrays.asList(
            "application", "servletContext"));

    public class DeclaredScope extends SimpleScope {

        @Override
        public Set<String> getContantNames() {
            return CONSTANT_NAMES;
        }

        @Override
        public Set<String> getAttributeNames() {
            Set<String> names = new LinkedHashSet<String>();
            Enumeration<String> enu = servletContext.getAttributeNames();
            while (enu.hasMoreElements()) {
                names.add(enu.nextElement());
            }
            return names;
        }

        @Override
        public boolean containsConstant(String name) {
            return CONSTANT_NAMES.contains(name);
        }

        @Override
        public boolean containsAttribute(String name) {
            Enumeration<String> enu = servletContext.getAttributeNames();
            while (enu.hasMoreElements()) {
                if (name.equals(enu.nextElement())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getConstant(String name) {
            Object obj = name.equals("application") ? servletContext
                    : name.equals("servletContext") ? servletContext
                    : null;
            return (T) obj;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAttribute(String name) {
            return (T) servletContext.getAttribute(name);
        }

        @Override
        public void setAttribute(String name, Object obj) {
            servletContext.setAttribute(name, obj);
        }
    }
}