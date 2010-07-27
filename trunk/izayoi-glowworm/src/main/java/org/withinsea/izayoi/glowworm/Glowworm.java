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

package org.withinsea.izayoi.glowworm;

import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.scope.*;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfigurator;
import org.withinsea.izayoi.glowworm.core.invoke.InvokeManager;

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

        protected InvokeManager invokeManager;
        protected String outputFolder;
        protected String outputSuffix;
        protected String bypass;

        public void doDispatch(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

            String requestPath = (String) request.getAttribute(ServletFilterUtils.INCLUDE_SERVLET_PATH);
            if (requestPath == null) requestPath = request.getServletPath();

            if (requestPath.startsWith(outputFolder)) {
                requestPath = requestPath.substring(outputFolder.length());
                int i = requestPath.lastIndexOf("." + outputSuffix);
                if (i >= 0) {
                    requestPath = requestPath.substring(0, i);
                }
            }

            if (ServletFilterUtils.matchUrlPattern(requestPath, bypass)) {
                chain.doFilter(request, response);
                return;
            }

            if (invokeManager.isAppendant(requestPath)) {
                if (chain != null && (ServletFilterUtils.isIncluded(request) || ServletFilterUtils.isForwarded(request))) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(404, requestPath);
                }
                return;
            }

            try {

                Map<String, Scope> scopes = new LinkedHashMap<String, Scope>();
                {
                    scopes.put("singleton", new Singleton());
                    scopes.put("application", new Application(request.getSession().getServletContext()));
                    scopes.put("session", new Session(request.getSession()));
                    scopes.put("request", new Request(request, response, chain));
                }
                for (Map.Entry<String, Scope> scopeE : scopes.entrySet()) {
                    for (String appendantPath : invokeManager.findScopedAppendantPaths(scopeE.getKey(), scopeE.getValue())) {
                        if (!invokeManager.invoke(appendantPath, scopeE.getValue())) {
                            break;
                        }
                    }
                }

                Request scope = new Request(request, response, chain);
                for (String appendantPath : invokeManager.findRequestAppendantPaths(requestPath)) {
                    if (!invokeManager.invoke(appendantPath, scope)) {
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

        public void setInvokeManager(InvokeManager invokeManager) {
            this.invokeManager = invokeManager;
        }

        public void setOutputFolder(String outputFolder) {
            this.outputFolder = outputFolder;
        }

        public void setOutputSuffix(String outputSuffix) {
            this.outputSuffix = outputSuffix;
        }
    }

    // api

    protected Configurator configurator = new GlowwormConfigurator();
    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath, Map<String, String> confOverrides) {
        IzayoiContainer container = IzayoiContainer.get(servletContext, configPath, configurator, confOverrides);
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