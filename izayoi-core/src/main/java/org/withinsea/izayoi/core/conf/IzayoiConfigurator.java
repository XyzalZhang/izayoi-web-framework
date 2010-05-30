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

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 16:05:31
 */
public class IzayoiConfigurator implements Configurator {

    protected static final String DEFAULT_CONFIG_PATH = "/WEB-INF/izayoi.properties";

    @Override
    public void loadConf(Properties conf, ServletContext servletContext, String configPath) throws Exception {
        loadDefaultConf(conf, servletContext);
        loadCustomizedConf(conf, servletContext, configPath);
    }

    @Override
    public void initComponents(IzayoiContainer container, Properties conf) throws Exception {

        container.addComponent("mimeTypes", getPropertyMap(conf, "mimeType", true));
        container.addComponent("codeManager", getClass(conf, "codeManager"));

        container.addComponent("globalScope", getClass(conf, "globalScope"));
        container.addComponent("scopeManager", getClass(conf, "scopeManager"));

        container.addComponent("interpreters", getComponentMap(container, conf, "interpreter"));
        container.addComponent("defaultInterpreters", getComponentList(container, conf, "defaultInterpreters"));
        container.addComponent("interpretManager", getClass(conf, "interpretManager"));
    }

    protected Map<String, String> getPropertyMap(Properties conf, String typeName, boolean trim) throws Exception {
        String prefix = typeName + ".";
        Map<String, String> props = new LinkedHashMap<String, String>();
        for (String propName : conf.stringPropertyNames()) {
            if (propName.startsWith(prefix)) {
                String name = propName.substring(prefix.length());
                String value = conf.getProperty(propName);
                props.put(name, trim ? value.trim() : value);
            }
        }
        return props;
    }

    @SuppressWarnings("unchecked")
    protected <T> Map<String, T> getComponentMap(IzayoiContainer container, Properties conf, String typeName) throws Exception {
        String prefix = "class." + typeName + ".";
        Map<String, T> components = new LinkedHashMap<String, T>();
        for (String propName : conf.stringPropertyNames()) {
            if (propName.startsWith(prefix)) {
                String name = propName.substring(prefix.length());
                components.put(name, (T) container.getComponent(Class.forName(conf.getProperty(propName).trim())));
            }
        }
        return components;
    }

    @SuppressWarnings("unchecked")
    protected <T> List<T> getComponentList(IzayoiContainer container, Properties conf, String typeName) throws Exception {
        List<T> components = new ArrayList<T>();
        for (String className : conf.getProperty("class." + typeName).trim().split("[,; ]+")) {
            components.add((T) container.getComponent(Class.forName(className.trim())));
        }
        return components;
    }

    protected Class<?> getClass(Properties conf, String typeName) throws Exception {
        return Class.forName(conf.getProperty("class." + typeName).trim());
    }

    protected List<String> getList(Properties conf, String typeName) throws Exception {
        return Arrays.asList(conf.getProperty("list." + typeName).trim().split("[,; ]+"));
    }

    protected void loadDefaultConf(Properties conf, ServletContext servletContext) throws Exception {
        Deque<Class<?>> classes = new ArrayDeque<Class<?>>();
        for (Class<?> claz = getClass(); Configurator.class.isAssignableFrom(claz); claz = claz.getSuperclass()) {
            classes.push(claz);
        }
        for (Class<?> claz : classes) {
            String confname = getDefaultConfigName(claz);
            if (confname != null) {
                InputStream is = claz.getResourceAsStream(confname + ".properties");
                if (is != null) {
                    conf.load(is);
                }
            }
        }
    }

    protected void loadCustomizedConf(Properties conf, ServletContext servletContext, String configPath) throws Exception {
        configPath = (configPath != null) ? configPath : DEFAULT_CONFIG_PATH;
        InputStream is = servletContext.getResourceAsStream(configPath);
        if (is != null) {
            conf.load(is);
        }
    }

    protected String getDefaultConfigName(Class<?> claz) {
        if (claz.isAnonymousClass() || claz.getName() == null) {
            return null;
        } else {
            String name = claz.getCanonicalName().substring(claz.getPackage().getName().length() + 1).replaceAll("Configurator.*$", "");
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
    }
}