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

import org.withinsea.izayoi.core.conf.IzayoiContainer;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-15
 * Time: 4:41:01
 */
public class DefaultGlobalScope extends AbstractScope<Scope> {

    protected IzayoiContainer izayoiContainer;
    protected ServletContext servletContext;

    @Override
    protected Object getConstant(String name) {
        return name.equals("izayoiContainer") ? izayoiContainer
                : name.equals("servletContext") ? servletContext
                : null;
    }

    @Override
    protected Object getAttribute(String name) {
        Object obj = lookupCDI(name);
        if (obj == null) obj = lookupJndi(name.replace("_", "/"));
        if (obj == null) try {
            obj = izayoiContainer.getComponent(name);
        } catch (Exception e) {
            // do nothing
        }
        return obj;
    }

    @Override
    protected void setAttribute(String name, Object obj) {
        throw new UnsupportedOperationException();
    }

    protected Object lookupCDI(String name) {
        try {
            Class.forName("javax.enterprise.inject.spi.Bean");
        } catch (ClassNotFoundException e) {
            return null;
        }
        return CDIHelper.lookupCDI(servletContext, name);
    }

    protected Object lookupJndi(String... names) {
        return JNDIHelper.lookupJndi(names);
    }

    public void setIzayoiContainer(IzayoiContainer izayoiContainer) {
        this.izayoiContainer = izayoiContainer;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    // lazy load CDI api for jee5-

    protected static class CDIHelper {

        public static Object lookupCDI(ServletContext servletContext, String name) {
            @SuppressWarnings("unchecked")
            BeanManager beanManager = (BeanManager) servletContext.getAttribute(BeanManager.class.getName());
            if (beanManager == null)
                beanManager = (BeanManager) JNDIHelper.lookupJndi("java:comp/BeanManager", "java:app/BeanManager");
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
            javax.naming.Context provide() throws NamingException;
        }

        protected static Collection<JNDIContextProvider> PROVIDERS = new LinkedHashSet<JNDIContextProvider>(); static {
            PROVIDERS.add(new JNDIContextProvider() {
                @Override
                public javax.naming.Context provide() throws NamingException {
                    return new InitialContext();
                }
            });
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

        public static void registerJNDIContextProvider(JNDIContextProvider provider) {
            PROVIDERS.add(provider);
        }

        public static void registerPrefix(String prefix) {
            PREFIXES.add(prefix);
        }

        public static void registerNamespace(String namespace) {
            NAMESPACES.add(namespace);
        }

        public static Object lookupJndi(String... names) {
            for (String name : names) {
                for (String ejbName : new String[]{name, name + "Local", name + "Remote"}) { // ejb
                    for (JNDIContextProvider provider : PROVIDERS) {
                        javax.naming.Context ctx = null;
                        try {
                            ctx = provider.provide();
                            for (String prefix : PREFIXES) {
                                for (String namespace : NAMESPACES) {
                                    try {
                                        return ctx.lookup(prefix + namespace + ejbName);
                                    } catch (NamingException e) {
                                        // do nothing
                                    }
                                }
                            }
                        } catch (NamingException e) {
                            // do nothing
                        }
                    }
                }
            }
            return null;
        }
    }
}