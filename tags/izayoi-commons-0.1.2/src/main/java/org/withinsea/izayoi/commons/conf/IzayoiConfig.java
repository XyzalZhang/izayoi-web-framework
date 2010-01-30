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

package org.withinsea.izayoi.commons.conf;

import org.picocontainer.MutablePicoContainer;
import org.withinsea.izayoi.commons.exception.IzayoiRuntimeException;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 16:05:31
 */
public class IzayoiConfig {

    protected static final String CONTAINERS_ATTR_NAME = IzayoiConfig.class.getCanonicalName() + ".CONTAINERS";

    protected final ServletContext servletContext;
    protected final String configPath;

    public IzayoiConfig(ServletContext servletContext) {
        this(servletContext, null);
    }

    public IzayoiConfig(ServletContext servletContext, String configPath) {
        this.servletContext = servletContext;
        this.configPath = (configPath != null) ? configPath : "/WEB-INF/" + getNamespace(getClass()) + ".properties";
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
        Map<String, IzayoiComponentContainer> containers = (Map<String, IzayoiComponentContainer>) servletContext.getAttribute(CONTAINERS_ATTR_NAME);
        if (containers == null) {
            containers = new HashMap<String, IzayoiComponentContainer>();
            servletContext.setAttribute(CONTAINERS_ATTR_NAME, containers);
        }
        String containerName = getClass().getCanonicalName() + "@" + configPath;
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
                    Properties props = new Properties();
                    conf.load(is);
                }
            }

            InputStream is = servletContext.getResourceAsStream(configPath);
            if (is != null) {
                conf.load(is);
            }
        }

        IzayoiComponentContainer container = new IzayoiComponentContainer();
        {
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
    }

    protected static String getNamespace(Class<?> claz) {
        return claz.getSimpleName().replaceAll("Config$", "").toLowerCase();
    }
}