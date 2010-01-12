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

package org.withinsea.izayoi.glowworm.core.conf;

import org.picocontainer.MutablePicoContainer;
import org.withinsea.izayoi.commons.conf.IzayoiConfig;
import org.withinsea.izayoi.commons.util.ClassUtils;
import org.withinsea.izayoi.glowworm.core.injector.Injector;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-11
 * Time: 14:36:57
 */
public class GlowwormConfig extends IzayoiConfig {

    public GlowwormConfig(ServletContext servletContext) {
        super(servletContext);
    }

    public GlowwormConfig(ServletContext servletContext, String configPath) {
        super(servletContext, configPath);
    }

    @Override
    protected void initComponents(MutablePicoContainer container, ServletContext servletContext, Properties conf) throws Exception {

        super.initComponents(container, servletContext, conf);

        Map<String, Injector> injectors = new LinkedHashMap<String, Injector>();
        {
            Map<String, Injector> unsortedInjectors = new HashMap<String, Injector>();
            for (String name : conf.stringPropertyNames()) {
                if (name.startsWith("class.injector.")) {
                    String type = name.substring("class.injector.".length());
                    try {
                        @SuppressWarnings("unchecked")
                        Class<?> claz = Class.forName(conf.getProperty(name));
                        if (ClassUtils.isExtendsFrom(claz, Injector.class)) {
                            if (container.getComponentAdapter(claz) == null) {
                                container.addComponent(claz);
                            }
                            unsortedInjectors.put(type, (Injector) container.getComponent(claz));
                        }

                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
            }
            for (String type : conf.getProperty("defaultTypes").split("[\\s;,]+")) {
                if (unsortedInjectors.containsKey(type)) {
                    injectors.put(type, unsortedInjectors.get(type));
                    unsortedInjectors.remove(type);
                }
            }
            injectors.putAll(unsortedInjectors);
        }
        container.addComponent("injectors", injectors);

        container.addComponent("injectManager", Class.forName(conf.getProperty("class.injectManager")));
    }
}
