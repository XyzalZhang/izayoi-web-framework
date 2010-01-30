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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;
import org.withinsea.izayoi.glowworm.core.GlowwormLight;
import org.withinsea.izayoi.glowworm.core.dependency.ContextDependencyManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormRuntimeException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-30
 * Time: 3:31:19
 */
public class GlowwormInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {

    protected final String configPath;

    protected final Map<ServletContext, GlowwormLight> lights = new LazyLinkedHashMap<ServletContext, GlowwormLight>() {
        @Override
        protected GlowwormLight createValue(ServletContext servletContext) {
            GlowwormLight light = new GlowwormLight() {
                @Override
                public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws GlowwormException {
                    dispatcher.doDispatch(new ApplicationContextServletDependency(req), req, resp, requestPath, chain);
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

    protected ApplicationContext applicationContext;

    public GlowwormInterceptor() {
        this(null);
    }

    public GlowwormInterceptor(String configPath) {
        this.configPath = configPath;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        FlagChain chain = new FlagChain();
        lights.get(request.getSession().getServletContext()).doDispatch(request, response, chain);
        return chain.isFlag();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected class ApplicationContextServletDependency extends ContextDependencyManager.DependencyImpl {

        public ApplicationContextServletDependency(HttpServletRequest request) {
            super(request);
        }

        @Override
        public Object getBean(String name) {
            Object obj = super.getBean(name);
            return (obj != null) ? obj : applicationContext.getBean(name);
        }
    }

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
}
