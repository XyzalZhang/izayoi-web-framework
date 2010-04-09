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
import org.withinsea.izayoi.core.code.PathUtils;
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
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:01:47
 */
public class Cortile extends HttpServlet implements Filter, Configurable {

    // dispatcher

    public static class Dispatcher {

        protected ServletContext servletContext;
        protected CodeManager codeManager;
        protected CompileManager compileManager;
        protected String templateSuffix;
        protected String encoding;

        protected String findTemplatePath(String requestPath) throws CortileException {

            if (codeManager.exist(requestPath) && !codeManager.get(requestPath).isFolder()) {
                return requestPath;
            }

            String folder = PathUtils.getFolderPath(requestPath);
            String name = PathUtils.getName(requestPath);
            String templateNameRegex = Pattern.quote(name + templateSuffix + ".") + "(\\w+)";
            List<String> templateNames = codeManager.listNames(folder, templateNameRegex);
            if (!templateNames.isEmpty()) {
                if (templateNames.size() > 1) {
                    throw new CortileException("Request on " + requestPath + " has ambiguous mirage templates.");
                }
                String templatePath = folder + "/" + templateNames.get(0);
                if (codeManager.exist(templatePath) && !codeManager.get(templatePath).isFolder()) {
                    return templatePath;
                }
            }

            return null;
        }

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws CortileException {

            requestPath = (requestPath == null) ? req.getServletPath() : requestPath;

            String templatePath = findTemplatePath(requestPath);
            try {
                if (templatePath != null) {
                    String entrancePath = compileManager.update(templatePath, null, false);
                    if (entrancePath.equals(requestPath)) {
                        chain.doFilter(req, resp);
                    } else {
                        req.getRequestDispatcher(entrancePath).forward(req, resp);
                        resp.setCharacterEncoding(encoding);
                        resp.setContentType(servletContext.getMimeType(requestPath) + "; charset=" + encoding);
                    }
                } else if (chain != null) {
                    chain.doFilter(req, resp);
                } else {
                    resp.sendError(404, requestPath);
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

    // api

    protected Configurator configurator = new CortileConfigurator();
    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath) throws CortileException {
        dispatcher = ComponentContainer.get(servletContext, configPath, configurator).getComponent(Dispatcher.class);
    }

    public String getTemplateSuffix() {
        return dispatcher.templateSuffix;
    }

    public String findTemplatePath(String requestPath) {
        try {
            return dispatcher.findTemplatePath(requestPath);
        } catch (CortileException e) {
            return null;
        }
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws CortileException {
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
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        try {
            doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, null, chain);
        } catch (CortileException e) {
            throw new ServletException(e);
        }
    }
}
