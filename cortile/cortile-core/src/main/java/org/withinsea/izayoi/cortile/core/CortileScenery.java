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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:01:47
 */
public class CortileScenery extends HttpServlet implements Filter {

    protected CompileManager manager;

    protected void init(ServletContext servletContext, String configPath) throws ServletException {
        try {
            manager = Config.getCompileManager(servletContext, configPath);
        } catch (CortileException e) {
            throw new ServletException(e);
        }
    }

    protected void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        try {

            ServletContext servletContext = req.getSession().getServletContext();

            String templatePath = req.getServletPath();
            String type = templatePath.replaceAll(".*/", "").replaceAll(".*\\.", "");

            if (templatePath.endsWith("/") || !manager.exist(templatePath)) {
                resp.sendError(404, req.getServletPath());
            } else {
                req.getRequestDispatcher(manager.update(templatePath, type)).forward(req, resp);
                resp.setCharacterEncoding(manager.getEncoding());
                resp.setContentType(servletContext.getMimeType(templatePath) + "; charset=" + manager.getEncoding());
            }

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    // as servlet

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        init(config.getServletContext(), config.getInitParameter("config-path"));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp);
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
            doDispatch(req, resp);
        } else {
            chain.doFilter(request, response);
        }
    }
}
