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

package org.withinsea.izayoi.glowworm.core;

import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.core.conf.ComponentContainer;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.core.scope.context.ContextScope;
import org.withinsea.izayoi.core.scope.custom.Application;
import org.withinsea.izayoi.core.scope.custom.Request;
import org.withinsea.izayoi.core.scope.custom.Session;
import org.withinsea.izayoi.core.scope.custom.Singleton;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfigurator;
import org.withinsea.izayoi.glowworm.core.decorate.DecorateManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-12
 * Time: 23:49:57
 */
public class Glowworm implements Filter, Configurable {

    // dispatcher

    public static class Dispatcher {

        protected DecorateManager decorateManager;
        protected ContextScope contextScope;
        protected String outputFolder;
        protected String outputSuffix;
        protected String bypass;

        public void doDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

            String requestPath = (String) request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
            if (requestPath == null) requestPath = request.getServletPath();

            if (ServletFilterUtils.matchUrlPattern(requestPath, bypass)) {
                chain.doFilter(request, response);
                return;
            }

            if (requestPath.startsWith(outputFolder)) {
                requestPath = requestPath.substring(outputFolder.length());
                int i = requestPath.lastIndexOf("." + outputSuffix);
                if (i >= 0) {
                    requestPath = requestPath.substring(0, i);
                }
            }

            if (decorateManager.isDecorator(requestPath)) {
                response.sendError(404);
                return;
            }

            try {

                Map<String, Scope> scopes = new LinkedHashMap<String, Scope>();
                {
                    scopes.put("singleton", new Singleton(contextScope));
                    scopes.put("application", new Application(contextScope, request.getSession().getServletContext()));
                    scopes.put("session", new Session(contextScope, request.getSession()));
                    scopes.put("request", new Request(contextScope, request, response, chain));
                }
                for (Map.Entry<String, Scope> scopeE : scopes.entrySet()) {
                    for (String decoratorPath : decorateManager.findScopedDecoratorPaths(scopeE.getKey(), scopeE.getValue())) {
                        if (!decorateManager.invoke(decoratorPath, scopeE.getValue())) {
                            break;
                        }
                    }
                }

                Request scope = new Request(contextScope, request, response, chain);
                for (String decoratorPath : decorateManager.findRequestDecoratorPaths(requestPath)) {
                    if (!decorateManager.invoke(decoratorPath, scope)) {
                        return;
                    }
                }

                chain.doFilter(request, response);

            } catch (IzayoiException e) {
                throw new SecurityException(e);
            }
        }

        public void setBypass(String bypass) {
            this.bypass = bypass;
        }

        public void setDecorateManager(DecorateManager decorateManager) {
            this.decorateManager = decorateManager;
        }

        public void setOutputFolder(String outputFolder) {
            this.outputFolder = outputFolder;
        }

        public void setOutputSuffix(String outputSuffix) {
            this.outputSuffix = outputSuffix;
        }

        public void setContextScope(ContextScope contextScope) {
            this.contextScope = contextScope;
        }
    }

    // api

    protected Configurator configurator = new GlowwormConfigurator();
    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath, Map<String, String> confOverrides) {
        ComponentContainer container = ComponentContainer.get(servletContext, configPath, configurator, confOverrides);
        dispatcher = container.getComponent(Dispatcher.class);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, final FilterChain chain) throws ServletException, IOException {
        dispatcher.doDispatch(req, resp, chain);
    }

    @Override
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    // as filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Map<String, String> confOverrides = ServletFilterUtils.getParamsMap(filterConfig);
        confOverrides.remove("config-path");
        init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"), confOverrides);
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, chain);
    }

    @Override
    public void destroy() {
    }
}