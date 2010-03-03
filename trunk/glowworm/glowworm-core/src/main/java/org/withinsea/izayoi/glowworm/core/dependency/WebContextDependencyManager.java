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

package org.withinsea.izayoi.glowworm.core.dependency;

import org.withinsea.izayoi.commons.servlet.HttpContextMap;
import org.withinsea.izayoi.commons.servlet.HttpParameterMap;
import org.withinsea.izayoi.commons.util.Varstack;

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
        Object obj = lookupRequest(request, name);
        if (obj == null) obj = lookupCDI(name);
        if (obj == null) obj = lookupJndi(name.replace("_", "/"));
        return obj;
    }

    protected static final String VARSTACK_ATTR = WebContextDependencyManager.class.getCanonicalName() + ".VARSTACK";

    @SuppressWarnings("unchecked")
    protected static <T> T lookupRequest(HttpServletRequest request, String name) {
        if (request.getAttribute(VARSTACK_ATTR) == null) {
            HttpContextMap contextMap = new HttpContextMap(request);
            HttpParameterMap paramMap = new HttpParameterMap(request);
            Varstack varstack = new Varstack();
            {
                varstack.push(contextMap);
                varstack.push(paramMap);
                varstack.push();
                {
                    varstack.put("params", paramMap);
                    varstack.put("application", request.getSession().getServletContext());
                    varstack.put("session", request.getSession());
                    varstack.put("request", request);
                }
            }
            request.setAttribute(VARSTACK_ATTR, varstack);
        }
        Varstack varstack = (Varstack) request.getAttribute(VARSTACK_ATTR);
        return (T) varstack.get(name);
    }

    @SuppressWarnings("unchecked")
    protected static <T> T lookupCDI(String name) {
        try {
            BeanManager beanManager = lookupJndi("java:comp/BeanManager");
            if (beanManager == null) {
                return null;
            }
            List<Bean<?>> beans = new ArrayList<Bean<?>>(beanManager.getBeans(name));
            if (beans.isEmpty()) {
                return null;
            } else {
                Bean<?> bean = beans.get(0);
                return (T) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean));
            }
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> T lookupJndi(String name) {
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
                    return (T) ctx.lookup(jndiName);
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
