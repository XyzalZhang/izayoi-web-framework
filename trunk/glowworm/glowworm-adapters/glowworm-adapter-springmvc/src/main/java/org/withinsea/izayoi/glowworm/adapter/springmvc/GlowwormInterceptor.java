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

package org.withinsea.izayoi.glowworm.adapter.springmvc;

import org.picocontainer.MutablePicoContainer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.withinsea.izayoi.glowworm.core.GlowwormLight;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfig;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormRuntimeException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-30
 * Time: 3:31:19
 */
public class GlowwormInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {

    // impl

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    // glowworm

    protected final String configPath;

    protected final Map<ServletContext, GlowwormLight> lights = new LinkedHashMap<ServletContext, GlowwormLight>() {

        public GlowwormLight get(ServletContext key) {
            if (!containsKey(key)) {
                synchronized (this) {
                    put(key, createValue(key));
                }
            }
            return super.get(key);
        }

        protected GlowwormLight createValue(ServletContext servletContext) {

            GlowwormLight light = new GlowwormLight() {
                @Override
                public void init(ServletContext servletContext, String configPath) throws GlowwormException {
                    dispatcher = new GlowwormConfig(servletContext, configPath) {
                        @Override
                        protected void initComponents(MutablePicoContainer container, ServletContext servletContext, Properties conf) throws Exception {
                            conf.setProperty("class.dependencyManager", "org.withinsea.izayoi.glowworm.adapter.springmvc.SpringWebContextDependencyManager");
                            super.initComponents(container, servletContext, conf);
                            container.addComponent("applicationContext", applicationContext);
                        }
                    }.getComponent(Dispatcher.class);
                }
            };

            try {
                light.init(servletContext, configPath);
            } catch (GlowwormException e) {
                throw new GlowwormRuntimeException(e);
            }

            return light;
        }
    };

    public GlowwormInterceptor() {
        this(null);
    }

    public GlowwormInterceptor(String configPath) {
        this.configPath = configPath;
    }

    // interceptor

    protected static class FlagChain implements FilterChain {

        protected boolean flag = false;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            flag = true;
        }

        public boolean isFlag() {
            return flag;
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        FlagChain chain = new FlagChain();
        lights.get(request.getSession().getServletContext()).doDispatch(request, response, chain);
        return chain.isFlag();
    }
}
