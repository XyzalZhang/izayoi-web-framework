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

package org.withinsea.izayoi.core.bindings;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-9
 * Time: 5:50:25
 */
public class WebContextBindingsManager extends BindingsManagerImpl {

    @Override
    public Object getBean(HttpServletRequest request, HttpServletResponse response, String name) {
        Object obj = lookupConstants(request, response, name);
        if (obj == null) obj = lookupRequest(request, name);
        if (obj == null) obj = lookupCDI(request, name);
        if (obj == null) obj = lookupJndi(name.replace("_", "/"));
        return obj;
    }

    protected static Object lookupConstants(HttpServletRequest request, HttpServletResponse response, String name) {
        return name.equals("request") ? request
                : name.equals("response") ? response
                : name.equals("session") ? request.getSession()
                : name.equals("application") ? request.getSession().getServletContext()
                : name.equals("servletContext") ? request.getSession().getServletContext()
                : null;
    }

    protected static Object lookupRequest(HttpServletRequest request, String name) {
        Object obj = request.getParameter(name);
        if (obj == null) obj = request.getAttribute(name);
        if (obj == null) obj = request.getSession().getAttribute(name);
        if (obj == null) obj = request.getSession().getServletContext().getAttribute(name);
        return obj;
    }

    protected static Object lookupCDI(HttpServletRequest request, String name) {
        try {
            Class.forName("javax.enterprise.inject.spi.Bean");
        } catch (ClassNotFoundException e) {
            return null;
        }
        return CDIHelper.lookupCDI(request, name);
    }

    protected static Object lookupJndi(String... names) {
        Collection<Context> ctxs = JNDIHelper.getJNDIContexts();
        Collection<String> prefixes = JNDIHelper.getPrefixes();
        Collection<String> namespaces = JNDIHelper.getNamespaces();
        for (String name : names) {
            for (String ejbName : new String[]{name, name + "Local", name + "Remote"}) { // ejb
                for (Context ctx : ctxs) {
                    for (String prefix : prefixes) {
                        for (String namespace : namespaces) {
                            try {
                                return ctx.lookup(prefix + namespace + ejbName);
                            } catch (NamingException e) {
                                // do nothing
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // lazy load CDI api for jee5-

    protected static class CDIHelper {

        public static Object lookupCDI(HttpServletRequest request, String name) {
            @SuppressWarnings("unchecked")
            BeanManager beanManager = (BeanManager) request.getSession().getServletContext().getAttribute(BeanManager.class.getName());
            if (beanManager == null)
                beanManager = (BeanManager) lookupJndi("java:comp/BeanManager", "java:app/BeanManager");
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
        }
    }

    // 3rd jndi context providers

    protected static class JNDIHelper {

        public static interface JNDIContextProvider {
            Context provide() throws NamingException;
        }

        protected static Collection<String> PREFIXES = new LinkedHashSet<String>(Arrays.asList(
                "",
                "java:module/",
                "java:app/",
                "java:global/",
                "java:comp/"
        ));

        protected static Collection<String> NAMESPACES = new LinkedHashSet<String>(Arrays.asList(
                "",
                "env/",
                "ejb/",
                "jms/"
        ));

        protected static Collection<JNDIContextProvider> PROVIDERS = new LinkedHashSet<JNDIContextProvider>(); static {
            PROVIDERS.add(new JNDIContextProvider() {
                @Override
                public Context provide() throws NamingException {
                    return new InitialContext();
                }
            });
        }

        public static Collection<String> getPrefixes() {
            return PREFIXES;
        }

        public static Collection<String> getNamespaces() {
            return NAMESPACES;
        }

        public static Collection<Context> getJNDIContexts() {
            List<Context> ctxs = new ArrayList<Context>();
            for (JNDIContextProvider provider : PROVIDERS) {
                try {
                    ctxs.add(provider.provide());
                } catch (NamingException e) {
                    // do nothing
                }
            }
            return ctxs;
        }

        public static void registerPrefix(String prefix) {
            PREFIXES.add(prefix);
        }

        public static void registerNamespace(String namespace) {
            NAMESPACES.add(namespace);
        }

        public static void registerJNDIContextProvider(JNDIContextProvider provider) {
            PROVIDERS.add(provider);
        }
    }
}
