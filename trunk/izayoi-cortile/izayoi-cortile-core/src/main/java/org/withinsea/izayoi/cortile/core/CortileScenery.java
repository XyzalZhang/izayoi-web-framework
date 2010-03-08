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

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.conf.ComponentContainer;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.cortile.core.compile.CompileManager;
import org.withinsea.izayoi.cortile.core.conf.CortileConfigurator;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:01:47
 */
public class CortileScenery extends HttpServlet implements Filter, Configurable {

    // config

    protected Configurator configurator = new CortileConfigurator();

    @Override
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    // api

    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath) throws CortileException {
        dispatcher = ComponentContainer.get(configurator, servletContext, configPath).getComponent(Dispatcher.class);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws CortileException {
        dispatcher.doDispatch(req, resp, null, chain);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws CortileException {
        dispatcher.doDispatch(req, resp, requestPath, chain);
    }

    // dispatcher

    public static class Dispatcher {

        protected ServletContext servletContext;
        protected CodeManager codeManager;
        protected CompileManager compileManager;
        protected String templateSuffix;
        protected String encoding;

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws CortileException {

            requestPath = (requestPath == null) ? req.getServletPath() : requestPath;

            try {

                if ((chain == null || requestPath.endsWith(templateSuffix))
                        && codeManager.exist(requestPath) && !codeManager.get(requestPath).isFolder()) {
                    req.getRequestDispatcher(compileManager.update(requestPath, null, false)).forward(req, resp);
                    resp.setCharacterEncoding(encoding);
                    resp.setContentType(servletContext.getMimeType(requestPath) + "; charset=" + encoding);
                } else if (chain != null) {
                    chain.doFilter(req, resp);
                } else {
                    resp.sendError(404, req.getServletPath());
                }

            } catch (Exception e) {
                throw new CortileException(e);
            }
        }

        public void setCompileManager(CompileManager compileManager) {
            this.compileManager = compileManager;
        }

        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public void setCodeManager(CodeManager codeManager) {
            this.codeManager = codeManager;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public void setTemplateSuffix(String templateSuffix) {
            this.templateSuffix = templateSuffix;
        }
    }

    // as servlet

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            init(config.getServletContext(), config.getInitParameter("config-path"));
        } catch (CortileException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp, req.getServletPath(), null);
        } catch (CortileException e) {
            throw new ServletException(e);
        }
    }

    // as filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"));
        } catch (CortileException e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            try {
                doDispatch(req, resp, chain);
            } catch (CortileException e) {
                throw new ServletException(e);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
