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

package org.withinsea.izayoi.cortile;

import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.core.context.BeanContextManager;
import org.withinsea.izayoi.core.context.Request;
import org.withinsea.izayoi.cortile.core.conf.CortileConfigurator;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.core.compile.CompileManager;

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

        protected BeanContextManager beanContextManager;
        protected CompileManager compileManager;
        protected CodeManager codeManager;
        protected String encoding;
        protected String bypass;

        public void doDispatch(HttpServletRequest request, HttpServletResponse response, String requestPath, FilterChain chain) throws ServletException, IOException {

            if (requestPath == null)
                requestPath = (String) request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
            if (requestPath == null) requestPath = request.getServletPath();

            if (ServletFilterUtils.matchUrlPattern(requestPath, bypass)) {
                chain.doFilter(request, response);
                return;
            }

            beanContextManager.getContext(new Request(request, response, chain));

            String templatePath = compileManager.findTemplatePath(requestPath);
            if (templatePath == null) {
                if (chain != null) {
                    chain.doFilter(request, response);
                } else {
                    response.sendError(404, requestPath);
                }
                return;
            }

            try {

                String entrancePath = compileManager.update(templatePath, false);

                if (!entrancePath.equals(requestPath)) {

                    response.setCharacterEncoding(encoding);

                    Path parsedPath = new Path(requestPath);
                    String mimeType = codeManager.getMimeType(parsedPath.getMainType());
                    if (mimeType == null) mimeType = codeManager.getMimeType(parsedPath.getType());
                    if (mimeType != null) {
                        response.setContentType(mimeType + "; charset=" + encoding);
                    }

                    try {
                        request.getRequestDispatcher(entrancePath).forward(request, response);
                    } catch (Exception e) {
                        throw new CortileException(e);
                    }

                    if (mimeType != null) {
                        response.setContentType(mimeType + "; charset=" + encoding);
                    }

                } else if (chain != null) {

                    chain.doFilter(request, response);

                } else {

                    response.sendError(404, requestPath);
                }

            } catch (CortileException e) {
                throw new ServletException(e);
            }
        }

        public void setBypass(String bypass) {
            this.bypass = bypass;
        }

        public void setCodeManager(CodeManager codeManager) {
            this.codeManager = codeManager;
        }

        public void setCompileManager(CompileManager compileManager) {
            this.compileManager = compileManager;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public void setBeanContextManager(BeanContextManager beanContextManager) {
            this.beanContextManager = beanContextManager;
        }
    }

    // api

    protected Configurator configurator = new CortileConfigurator();
    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath, Map<String, String> confOverrides) {
        IzayoiContainer container = IzayoiContainer.get(servletContext, configPath, configurator, confOverrides);
        dispatcher = container.getComponent(Dispatcher.class);
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