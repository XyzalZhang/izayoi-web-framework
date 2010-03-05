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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:04:20
 */
public class CortileMirage implements Filter, Configurable {

    protected Configurator configurator = new CortileConfigurator();

    @Override
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    public static class Dispatcher {

        public static final String TRUE_PATH_ATTR = CortileMirage.class.getCanonicalName() + ".TRUE_PATH";

        protected ServletContext servletContext;
        protected CodeManager codeManager;
        protected CompileManager compileManager;
        protected String mirageSuffix;
        protected String encoding;

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws CortileException {

            requestPath = (requestPath == null) ? req.getServletPath() : requestPath;

            try {

                String truePath = (String) req.getAttribute(TRUE_PATH_ATTR);
                if (truePath != null) {
                    chain.doFilter(req, resp);
                    resp.setCharacterEncoding(encoding);
                    resp.setContentType(servletContext.getMimeType(truePath) + "; charset=" + encoding);
                    return;
                }

                if (!codeManager.get(requestPath).isFolder()) {

                    String folder = PathUtils.getFolderPath(requestPath);
                    String main = PathUtils.getMainName(requestPath);
                    String ext = PathUtils.getExtName(requestPath);
                    String typedTemplateNameRegex = Pattern.quote(main + mirageSuffix + "-") + "(\\w+)" + Pattern.quote("." + ext);

                    String templatePath = folder + "/" + main + mirageSuffix + "." + ext;
                    List<String> typedTemplateNames = codeManager.listNames(folder, typedTemplateNameRegex);

                    if ((codeManager.exist(templatePath) && !typedTemplateNames.isEmpty()) || typedTemplateNames.size() > 1) {
                        throw new CortileException("Request on " + requestPath + " has ambiguous mirage templates.");
                    }

                    if (codeManager.exist(templatePath)) {
                        req.setAttribute(TRUE_PATH_ATTR, requestPath);
                        req.getRequestDispatcher(compileManager.update(templatePath, null, false)).forward(req, resp);
                        return;
                    } else if (!typedTemplateNames.isEmpty()) {
                        String type = Pattern.compile(typedTemplateNameRegex).matcher(typedTemplateNames.get(0)).group(1);
                        req.setAttribute(TRUE_PATH_ATTR, requestPath);
                        req.getRequestDispatcher(compileManager.update(folder + "/" + typedTemplateNames.get(0), type, false)).forward(req, resp);
                        return;
                    }
                }

                chain.doFilter(req, resp);

            } catch (Exception e) {
                throw new CortileException(e);
            }
        }

        public void setCompileManager(CompileManager compileManager) {
            this.compileManager = compileManager;
        }

        public void setMirageSuffix(String mirageSuffix) {
            this.mirageSuffix = mirageSuffix;
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
    }

    // api

    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath) throws CortileException {
        dispatcher = ComponentContainer.get(configurator, servletContext, configPath).getComponent(Dispatcher.class);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws CortileException {
        doDispatch(req, resp, null, chain);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws CortileException {
        dispatcher.doDispatch(req, resp, requestPath, chain);
    }

    // as filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"));
        } catch (Exception e) {
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

    @Override
    public void destroy() {
    }
}
