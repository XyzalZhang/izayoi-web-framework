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

package org.withinsea.izayoi.cortile.core;

import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.core.conf.ComponentContainer;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.scope.context.ContextScope;
import org.withinsea.izayoi.core.scope.custom.Request;
import org.withinsea.izayoi.cortile.core.conf.CortileConfigurator;
import org.withinsea.izayoi.cortile.core.respond.RespondManager;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:01:47
 */
public class Cortile extends HttpServlet implements Filter, Configurable {

    // dispatcher

    public static class Dispatcher {

        protected RespondManager respondManager;
        protected ContextScope contextScope;
        protected String bypass;

        public void doDispatch(HttpServletRequest request, HttpServletResponse response, String requestPath, FilterChain chain) throws ServletException, IOException {

            if (requestPath == null)
                requestPath = (String) request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
            if (requestPath == null) requestPath = request.getServletPath();

            if (ServletFilterUtils.matchUrlPattern(requestPath, bypass)) {
                chain.doFilter(request, response);
                return;
            }

            if (respondManager.isResponder(requestPath)) {
                if (chain != null && (ServletFilterUtils.isIncluded(request) || ServletFilterUtils.isForwarded(request))) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(404, requestPath);
                }
                return;
            }

            Request scope = new Request(contextScope, request, response, chain);

            try {
                for (String responderPath : respondManager.findResponderPaths(requestPath)) {
                    if (respondManager.invoke(responderPath, scope)) {
                        return;
                    }
                }
            } catch (IzayoiException e) {
                throw new ServletException(e);
            }

            if (chain != null) {
                chain.doFilter(request, response);
            } else {
                response.sendError(404, requestPath);
            }
        }

        public void setBypass(String bypass) {
            this.bypass = bypass;
        }

        public void setRespondManager(RespondManager respondManager) {
            this.respondManager = respondManager;
        }

        public void setContextScope(ContextScope contextScope) {
            this.contextScope = contextScope;
        }
    }

    // api

    protected Configurator configurator = new CortileConfigurator();
    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath, Map<String, String> confOverrides) {
        ComponentContainer container = ComponentContainer.get(servletContext, configPath, configurator, confOverrides);
        dispatcher = container.getComponent(Dispatcher.class);
    }

    public boolean hasResponders(String requestPath) {
        return !dispatcher.respondManager.findResponderPaths(requestPath).isEmpty();
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws ServletException, IOException {
        dispatcher.doDispatch(req, resp, requestPath, chain);
    }

    @Override
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    // as servlet

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        Map<String, String> confOverrides = ServletFilterUtils.getParamsMap(config);
        confOverrides.remove("config-path");
        init(config.getServletContext(), config.getInitParameter("config-path"), confOverrides);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp, req.getServletPath(), null);
    }

    // as filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Map<String, String> confOverrides = ServletFilterUtils.getParamsMap(filterConfig);
        confOverrides.remove("config-path");
        init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"), confOverrides);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, null, chain);
    }
}
