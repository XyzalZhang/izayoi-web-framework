package org.withinsea.izayoi.core.bindings.scope;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-22
 * Time: 12:07:16
 */
public class DefaultContextScope implements Scope {

    @Override
    public <T> void setBean(HttpServletRequest request, HttpServletResponse response, String name, T object) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(HttpServletRequest request, HttpServletResponse response, String name) {
        Object obj = lookupCDI(request, name);
        if (obj == null) obj = lookupJndi(name.replace("_", "/"));
        return (T) obj;
    }

    protected Object lookupCDI(HttpServletRequest request, String name) {
        try {
            Class.forName("javax.enterprise.inject.spi.Bean");
        } catch (ClassNotFoundException e) {
            return null;
        }
        return CDIHelper.lookupCDI(request, name);
    }

    protected Object lookupJndi(String... names) {
        return JNDIHelper.lookupJndi(names);
    }

    // lazy load CDI api for jee5-

    protected static class CDIHelper {

        public static Object lookupCDI(HttpServletRequest request, String name) {
            @SuppressWarnings("unchecked")
            BeanManager beanManager = (BeanManager) request.getSession().getServletContext().getAttribute(BeanManager.class.getName());
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
