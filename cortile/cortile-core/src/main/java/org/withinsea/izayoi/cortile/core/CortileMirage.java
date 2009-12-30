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

import org.withinsea.izayoi.cortile.core.compile.CompileManager;
import org.withinsea.izayoi.cortile.core.conf.Config;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:04:20
 */
public class CortileMirage implements Filter {

    public static final String TRUE_PATH_ATTR_NAME = CortileMirage.class.getCanonicalName() + ".TRUE_PATH_ATTR_NAME";

    protected String mirageSuffix;
    protected CompileManager manager;

    protected void init(ServletContext servletContext, String configPath) throws ServletException {
        try {
            mirageSuffix = Config.getConfig(servletContext, configPath).getProperty("cortile.suffix.mirage");
            manager = Config.getCompileManager(servletContext, configPath);
        } catch (CortileException e) {
            throw new ServletException(e);
        }
    }

    protected void doDispatch(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException {

        try {

            ServletContext servletContext = req.getSession().getServletContext();
            String truePath = (String) req.getAttribute(TRUE_PATH_ATTR_NAME);

            if (truePath != null) {
                chain.doFilter(req, resp);
                resp.setCharacterEncoding(manager.getEncoding());
                resp.setContentType(servletContext.getMimeType(truePath) + "; charset=" + manager.getEncoding());
                return;
            }

            String path = req.getServletPath();
            if (path.endsWith("/")) {
                chain.doFilter(req, resp);
            }

            String type = path.replaceAll(".*/", "").replaceAll(".*\\.", "");
            String main = path.substring(0, path.length() - type.length() - 1);

            if (manager.getSupportedTypes().contains(type)) {
                String templatePath = main + mirageSuffix + "." + type;
                if (manager.exist(templatePath)) {
                    req.setAttribute(TRUE_PATH_ATTR_NAME, path);
                    req.getRequestDispatcher(manager.update(templatePath, type)).forward(req, resp);
                    return;
                }
            }

            for (String supportedType : manager.getSupportedTypes()) {
                String typeSuffix = supportedType.equals("") ? "" : "-" + supportedType;
                String templatePath = main + mirageSuffix + typeSuffix + "." + type;
                if (manager.exist(templatePath)) {
                    req.setAttribute(TRUE_PATH_ATTR_NAME, path);
                    req.getRequestDispatcher(manager.update(templatePath, supportedType)).forward(req, resp);
                    return;
                }
            }

            chain.doFilter(req, resp);

        } catch (IOException e) {
            throw new ServletException(e);
        } catch (CortileException e) {
            throw new ServletException(e);
        }
    }

    // as filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            doDispatch(req, resp, chain);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
