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
import org.withinsea.izayoi.core.interpreter.Interpreter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 16:05:31
 */
public class IzayoiConfigurator implements Configurator {

    public void loadConf(Properties conf, ServletContext servletContext, String configPath) throws Exception {
        loadDefaultConf(conf, servletContext);
        loadCustomizedConf(conf, servletContext, configPath);
    }

    public void initComponents(MutablePicoContainer container, Properties conf) throws Exception {

        container.addComponent("codeManager", Class.forName(conf.getProperty("class.codeManager")));
        container.addComponent("bindingsManager", Class.forName(conf.getProperty("class.dependencyManager").trim()));

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

    protected void loadDefaultConf(Properties conf, ServletContext servletContext) throws Exception {
        Deque<Class<?>> classes = new ArrayDeque<Class<?>>();
        for (Class<?> claz = getClass(); Configurator.class.isAssignableFrom(claz); claz = claz.getSuperclass()) {
            classes.push(claz);
        }
        for (Class<?> claz : classes) {
            InputStream is = claz.getResourceAsStream(getDefaultConfigName(claz) + ".properties");
            if (is != null) {
                conf.load(is);
            }
        }
        InputStream is = servletContext.getResourceAsStream("/WEB-INF/" + getDefaultConfigName(getClass()) + ".properties");
        if (is != null) {
            conf.load(is);
        }
    }

    protected void loadCustomizedConf(Properties conf, ServletContext servletContext, String configPath) throws Exception {
        if (configPath != null) {
            InputStream is = servletContext.getResourceAsStream(configPath);
            if (is != null) {
                conf.load(is);
            }
        }
    }

    protected String getDefaultConfigName(Class<?> claz) {
        String name = claz.getSimpleName().replaceAll("Configurator.*$", "");
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}