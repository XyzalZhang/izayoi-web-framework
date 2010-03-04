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

package org.withinsea.izayoi.core.conf;

import org.picocontainer.MutablePicoContainer;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;
import org.withinsea.izayoi.core.interpreter.Interpreter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 16:05:31
 */
public class IzayoiConfig {

    protected static final String CONTAINERS_ATTR = IzayoiConfig.class.getCanonicalName() + ".CONTAINERS";

    protected final ServletContext servletContext;
    protected final String configPath;

    public IzayoiConfig(ServletContext servletContext) {
        this(servletContext, null);
    }

    public IzayoiConfig(ServletContext servletContext, String configPath) {
        this.servletContext = servletContext;
        this.configPath = configPath;
    }

    @SuppressWarnings("unchecked")
    public static IzayoiConfig retrieval(ServletContext servletContext, String retrievalKey) {
        try {
            Map<String, IzayoiComponentContainer> containers = (Map<String, IzayoiComponentContainer>) servletContext.getAttribute(CONTAINERS_ATTR);
            return (IzayoiConfig) containers.get(retrievalKey).getComponent("config");
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public final <T> T getComponent(String name) {
        return (T) getContainer().getComponent(name);
    }

    @SuppressWarnings("unchecked")
    public final <T> T getComponent(Class<T> claz) {
        return getContainer().getComponent(claz);
    }

    @SuppressWarnings("unchecked")
    protected final IzayoiComponentContainer getContainer() {
        Map<String, IzayoiComponentContainer> containers = (Map<String, IzayoiComponentContainer>) servletContext.getAttribute(CONTAINERS_ATTR);
        if (containers == null) {
            containers = new HashMap<String, IzayoiComponentContainer>();
            servletContext.setAttribute(CONTAINERS_ATTR, containers);
        }
        String containerName = getRetrievalKey();
        IzayoiComponentContainer container = containers.get(containerName);
        if (container == null) {
            try {
                container = createContainer();
            } catch (Exception e) {
                throw new IzayoiRuntimeException(e);
            }
            containers.put(containerName, container);
        }
        return container;
    }

    protected final String getRetrievalKey() {
        return getClass().getCanonicalName() + "@" + (configPath == null ? "DEFAULT" : configPath);
    }

    @SuppressWarnings("unchecked")
    protected final IzayoiComponentContainer createContainer() throws Exception {

        Properties conf = new Properties();
        {
            Deque<Class<?>> classes = new ArrayDeque<Class<?>>();
            for (Class<?> claz = getClass(); ;) {
                classes.push(claz);
                if (claz == IzayoiConfig.class) {
                    break;
                }
                claz = claz.getSuperclass();
            }
            for (Class<?> claz : classes) {
                InputStream is = claz.getResourceAsStream(getNamespace(claz) + ".properties");
                if (is != null) {
                    conf.load(is);
                }
            }
            for (Class<?> claz : classes) {
                InputStream is = servletContext.getResourceAsStream("/WEB-INF/" + getNamespace(claz) + ".properties");
                if (is != null) {
                    conf.load(is);
                }
            }
            if (configPath != null) {
                InputStream is = servletContext.getResourceAsStream(configPath);
                if (is != null) {
                    conf.load(is);
                }
            }
        }

        IzayoiComponentContainer container = new IzayoiComponentContainer();
        {

            container.addComponent("config", this);
            container.addComponent("retrievalKey", getRetrievalKey());

            for (String propname : conf.stringPropertyNames()) {
                container.addComponent(propname, conf.getProperty(propname));
            }

            initComponents(container, servletContext, conf);
        }
        return container;
    }

    protected void initComponents(MutablePicoContainer container, ServletContext servletContext, Properties conf) throws Exception {

        container.addComponent("servletContext", servletContext);
        container.addComponent("conf", conf);
        container.addComponent("webroot", new File(servletContext.getRealPath("/").replace("%20", " ")));

        container.addComponent("codeManager", Class.forName(conf.getProperty("class.codeManager")));
        container.addComponent("dependencyManager", Class.forName(conf.getProperty("class.dependencyManager").trim()));

        Map<String, Interpreter> interpreters = new LinkedHashMap<String, Interpreter>();
        {
            for (String name : conf.stringPropertyNames()) {
                if (name.startsWith("class.interpreter.")) {
                    String type = name.substring("class.interpreter.".length());
                    try {
                        Class<?> claz = Class.forName(conf.getProperty(name));
                        if (Interpreter.class.isAssignableFrom(claz)) {
                            interpreters.put(type, (Interpreter) container.getComponent(claz));
                        }
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
            }
        }
        container.addComponent("interpreters", interpreters);
    }

    protected static String getNamespace(Class<?> claz) {
        return claz.getSimpleName().replaceAll("Config$", "").toLowerCase();
    }
}