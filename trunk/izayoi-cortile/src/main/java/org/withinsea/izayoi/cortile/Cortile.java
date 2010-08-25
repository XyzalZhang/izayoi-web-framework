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
import org.withinsea.izayoi.core.code.CodeContainer;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.core.conf.IzayoiContainerFactory;
import org.withinsea.izayoi.cortile.core.compile.CompileManager;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:01:47
 */
public class Cortile extends HttpServlet implements Filter {

    private static final long serialVersionUID = 5277775921503773954L;

    // dispatcher

    public static class Dispatcher {

        @Resource
        CompileManager compileManager;

        @Resource
        CodeContainer codeContainer;

        @Resource
        String encoding;

        @Resource
        List<String> bypass;

        public void doDispatch(HttpServletRequest request, HttpServletResponse response, String requestPath, FilterChain chain) throws ServletException, IOException {

            if (requestPath == null)
                requestPath = (String) request.getAttribute(ServletFilterUtils.INCLUDE_SERVLET_PATH);
            if (requestPath == null) requestPath = request.getServletPath();

            if (ServletFilterUtils.matchUrlPattern(requestPath, bypass)) {
                chain.doFilter(request, response);
                return;
            }

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
                    String mimeType = codeContainer.getMimeType(parsedPath.getMainType());
                    if (mimeType == null) mimeType = codeContainer.getMimeType(parsedPath.getType());
                    if (mimeType != null) {
                        response.setContentType(mimeType + "; charset=" + encoding);
                    }

                    try {
                        ServletFilterUtils.forwardOrInclude(request, response, entrancePath);
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
    }

    // api

    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, Map<String, String> overriddenProperties) {
        init(new IzayoiContainerFactory()
                .addModule("org.withinsea.izayoi.core")
                .addModule("org.withinsea.izayoi.cortile")
                .create(servletContext, overriddenProperties));
    }

    public void init(IzayoiContainer container) {
        dispatcher = container.get(Dispatcher.class);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws ServletException, IOException {
        dispatcher.doDispatch(req, resp, requestPath, chain);
    }

    // as servlet

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        init(config.getServletContext(), ServletFilterUtils.getParamsMap(config));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doDispatch(req, resp, req.getServletPath(), null);
    }

    // as filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        init(filterConfig.getServletContext(), ServletFilterUtils.getParamsMap(filterConfig));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, null, chain);
    }
}