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

package org.withinsea.izayoi.core.dependency;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-30
 * Time: 16:05:28
 */
public class WebContextDependencyManager implements DependencyManager {

    @Override
    public Object getBean(HttpServletRequest request, String name) {
        Object obj = lookupConstants(request, name);
        if (obj == null) obj = lookupRequest(request, name);
        if (obj == null) obj = lookupCDI(name);
        if (obj == null) obj = lookupJndi(name.replace("_", "/"));
        return obj;
    }

    protected static Object lookupConstants(HttpServletRequest request, String name) {
        return name.equals("request") ? request
                : name.equals("session") ? request.getSession()
                : name.equals("application") ? request.getServletContext()
                : name.equals("servletContext") ? request.getServletContext()
                : null;
    }

    protected static Object lookupRequest(HttpServletRequest request, String name) {
        Object obj = request.getParameter(name);
        if (obj == null) obj = request.getAttribute(name);
        if (obj == null) obj = request.getSession().getAttribute(name);
        if (obj == null) obj = request.getServletContext().getAttribute(name);
        return obj;
    }

    protected static Object lookupCDI(String name) {
        try {
            @SuppressWarnings("unchecked")
            BeanManager beanManager = (BeanManager) lookupJndi("java:comp/BeanManager");
            if (beanManager == null) {
                return null;
            }
            List<Bean<?>> beans = new ArrayList<Bean<?>>(beanManager.getBeans(name));
            if (beans.isEmpty()) {
                return null;
            } else {
                Bean<?> bean = beans.get(0);
                return beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
            }
        } catch (Exception ex) {
            return null;
        }
    }

    protected static Object lookupJndi(String name) {
        try {
            Context ctx = new InitialContext();
            for (String jndiName : new String[]{
                    name,
                    "java:module/" + name,
                    "java:app/" + name,
                    "java:global/" + name,
                    "java:env/" + name,
                    "java:comp/" + name,
                    "java:ejb/" + name,
                    "java:jms/" + name
            }) {
                try {
                    return ctx.lookup(jndiName);
                } catch (NamingException e) {
                    // do nothing
                }
            }
        } catch (NamingException e) {
            return null;
        }
        return null;
    }
}