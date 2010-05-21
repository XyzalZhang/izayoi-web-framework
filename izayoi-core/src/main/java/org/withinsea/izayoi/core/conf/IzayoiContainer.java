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

import org.picocontainer.*;
import org.picocontainer.behaviors.Behaviors;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 15:13:00
 */
@SuppressWarnings("serial")
public class IzayoiContainer extends DefaultPicoContainer {

    // constructor

    protected static final String CONTAINERS_ATTR = IzayoiContainer.class.getCanonicalName() + ".CONTAINERS";

    public static IzayoiContainer retrieval(ServletContext servletContext, String retrievalKey) {
        @SuppressWarnings("unchecked")
        Map<String, IzayoiContainer> containers = (Map<String, IzayoiContainer>) servletContext.getAttribute(CONTAINERS_ATTR);
        if (containers == null) {
            containers = new HashMap<String, IzayoiContainer>();
            servletContext.setAttribute(CONTAINERS_ATTR, containers);
        }
        return containers.get(retrievalKey);
    }

    public static IzayoiContainer get(ServletContext servletContext, String configPath, Configurator configurator) {
        return get(servletContext, configPath, configurator, Collections.<String, String>emptyMap());
    }

    public static IzayoiContainer get(ServletContext servletContext, String configPath, Configurator configurator, Map<String, String> confOverrides) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, IzayoiContainer> containers = (Map<String, IzayoiContainer>) servletContext.getAttribute(CONTAINERS_ATTR);
            if (containers == null) {
                containers = new HashMap<String, IzayoiContainer>();
                servletContext.setAttribute(CONTAINERS_ATTR, containers);
            }
            String retrievalKey = getRetrievalKey(configurator.getClass(), configPath, confOverrides);
            IzayoiContainer container = retrieval(servletContext, retrievalKey);
            if (container == null) {
                Properties conf = new Properties();
                {
                    configurator.loadConf(conf, servletContext, configPath);
                    conf.putAll(confOverrides);
                }
                container = new IzayoiContainer();
                {
                    container.addComponent("servletContext", servletContext);
                    container.addComponent("izayoiContainer", container);
                    container.addComponent("izayoiContainerRetrievalKey", retrievalKey);
                    for (String propname : conf.stringPropertyNames()) {
                        container.addComponent(propname, conf.getProperty(propname));
                    }
                    configurator.initComponents(container, conf);
                }
                containers.put(retrievalKey, container);
            }
            return container;
        } catch (Exception e) {
            throw new IzayoiRuntimeException(e);
        }
    }

    protected static String getRetrievalKey(Class<? extends Configurator> configClass, String configPath, Map<String, String> confOverrides) {
        String retrievalKey = configClass.toString() + "@" + (configPath == null ? "DEFAULT" : configPath);
        for (Map.Entry<String, String> e : confOverrides.entrySet()) {
            retrievalKey += "&" + e.getKey() + "=" + e.getValue();
        }
        return retrievalKey.replaceAll("\\s", "_");
    }

    // container

    protected IzayoiContainer() {
        super(Behaviors.caching());
        change(Characteristics.SDI, Characteristics.USE_NAMES);
    }

    @Override
    public Object getComponent(Object componentKey) {
        return (componentKey instanceof String)
                ? getComponent((String) componentKey)
                : super.getComponent(componentKey);
    }

    @Override
    public <T> T getComponent(final Class<T> componentType) {
        addComponent(componentType);
        return super.getComponent(componentType);
    }

    @Override
    public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) {
        return (componentKey instanceof String)
                ? addComponent((String) componentKey, componentImplementationOrInstance, parameters)
                : super.addComponent(componentKey, componentImplementationOrInstance, parameters);
    }

    @Override
    public MutablePicoContainer addComponent(Object implOrInstance) {
        return (implOrInstance instanceof Class)
                ? addComponent((Class<?>) implOrInstance)
                : super.addComponent(implOrInstance);
    }

    @SuppressWarnings("unchecked")
    public <T> T getComponent(String componentKey) {
        return (T) super.getComponent(toInjectName(componentKey));
    }

    public MutablePicoContainer addComponent(Class<?> impl) {
        if (getComponentAdapter(impl) == null) {
            super.addComponent(impl);
        }
        return this;
    }

    public MutablePicoContainer addComponent(String componentKey, Object componentImplementationOrInstance, Parameter... parameters) {
        try {
            super.addComponent(toInjectName(componentKey), componentImplementationOrInstance, parameters);
        } catch (PicoCompositionException e) {
            if (!e.getMessage().startsWith("Unprocessed Characteristics")) {
                throw e;
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    protected static String toInjectName(String componentKey) {
        StringBuffer buf = new StringBuffer();
        String[] names = componentKey.trim().split("\\.");
        for (int i = names.length - 1; i >= 0; i--) {
            buf.append(names[i].substring(0, 1).toUpperCase()).append(names[i].substring(1));
        }
        String injectName = buf.toString();
        return injectName.substring(0, 1).toLowerCase() + injectName.substring(1);
    }
}