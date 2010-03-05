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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 15:13:00
 */
public class ComponentContainer extends DefaultPicoContainer {

    // constructor

    protected static final String CONTAINERS_ATTR = ComponentContainer.class.getCanonicalName() + ".CONTAINERS";

    public static ComponentContainer get(Class<? extends Configurator> configuratorClaz, ServletContext servletContext, String configPath) {
        try {
            return get(configuratorClaz.newInstance(), servletContext, configPath);
        } catch (InstantiationException e) {
            throw new IzayoiRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new IzayoiRuntimeException(e);
        }
    }

    public static ComponentContainer get(Configurator configurator, ServletContext servletContext, String configPath) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, ComponentContainer> containers = (Map<String, ComponentContainer>) servletContext.getAttribute(CONTAINERS_ATTR);
            if (containers == null) {
                containers = new HashMap<String, ComponentContainer>();
                servletContext.setAttribute(CONTAINERS_ATTR, containers);
            }
            String retrievalKey = getRetrievalKey(configurator.getClass(), configPath);
            ComponentContainer container = containers.get(retrievalKey);
            if (container == null) {
                Properties conf = new Properties();
                {
                    configurator.loadConf(conf, servletContext, configPath);
                }
                container = new ComponentContainer();
                {
                    container.addComponent("servletContext", servletContext);
                    container.addComponent("componentContainerRetrievalKey", retrievalKey);
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

    @SuppressWarnings("unchecked")
    public static ComponentContainer retrieval(ServletContext servletContext, String retrievalKey) {
        try {
            String[] splitKey = retrievalKey.split("@");
            return get((Class<? extends Configurator>) Class.forName(splitKey[0]), servletContext, splitKey[1]);
        } catch (Exception ex) {
            return null;
        }
    }

    protected static String getRetrievalKey(Class<? extends Configurator> configClass, String configPath) {
        return configClass.getCanonicalName() + "@" + (configPath == null ? "DEFAULT" : configPath);
    }

    // container

    protected ComponentContainer() {
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