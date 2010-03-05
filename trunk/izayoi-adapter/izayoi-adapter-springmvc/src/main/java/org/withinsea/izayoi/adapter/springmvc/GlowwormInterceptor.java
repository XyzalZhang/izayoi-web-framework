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

package org.withinsea.izayoi.adapter.springmvc;

import org.picocontainer.MutablePicoContainer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.withinsea.izayoi.commons.servlet.FlagChain;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.glowworm.core.GlowwormLight;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfigurator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-30
 * Time: 3:31:19
 */
public class GlowwormInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {

    protected ApplicationContext applicationContext;
    protected String configPath;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Configurator configurator = new GlowwormConfigurator() {

            @Override
            protected void loadDefaultConf(Properties conf, ServletContext servletContext) throws Exception {
                super.loadDefaultConf(conf, servletContext);
                conf.setProperty("class.dependencyManager", "org.withinsea.izayoi.adapter.springmvc.SpringWebContextDependencyManager");
            }

            @Override
            public void initComponents(MutablePicoContainer container, Properties conf) throws Exception {
                container.addComponent("applicationContext", applicationContext);
                super.initComponents(container, conf);
            }
        };

        GlowwormLight light = new GlowwormLight();
        light.setConfigurator(configurator);
        light.init(request.getServletContext(), configPath);

        FlagChain chain = new FlagChain();
        light.doDispatch(request, response, chain);
        return chain.isInvoked();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}