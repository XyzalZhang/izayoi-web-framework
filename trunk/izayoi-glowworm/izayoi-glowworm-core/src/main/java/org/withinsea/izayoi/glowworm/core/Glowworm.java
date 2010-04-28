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

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.conf.ComponentContainer;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfigurator;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.invoke.InvokeManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-12
 * Time: 23:49:57
 */
public class Glowworm implements Filter, Configurable {

    // dispatcher

    public static class Dispatcher {

        protected CodeManager codeManager;
        protected InvokeManager invokeManager;
        protected String appendantFolder;
        protected String outputFolder;
        protected String outputSuffix;
        protected String globalPrefix;

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException, IOException {

            String requestPath = (String) req.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH);
            if (requestPath == null) requestPath = req.getServletPath();

            if (invokeManager.isScript(requestPath)) {
                resp.sendError(404);
                return;
            }

            if (requestPath.startsWith(outputFolder)) {
                requestPath = requestPath.substring(outputFolder.length());
                int i = requestPath.lastIndexOf("." + outputSuffix);
                if (i >= 0) {
                    requestPath = requestPath.substring(0, i);
                }
            }
            Path parsedPath = new Path(requestPath);

            try {

                List<String> scriptPaths = new ArrayList<String>();

                String folder = appendantFolder;
                for (String folderItem : parsedPath.getFolder().equals("/") ? new String[]{""} : parsedPath.getFolder().split("/")) {
                    folder = folder + "/" + folderItem;
                    for (String scriptName : codeManager.listNames(folder, Pattern.quote(globalPrefix) + ".*" + "\\.[^\\.]+\\.[^\\.]+$")) {
                        scriptPaths.add(folder + "/" + scriptName);
                    }
                }
                for (String scriptName : codeManager.listNames(folder, Pattern.quote(parsedPath.getName()) + "\\.[^\\.]+\\.[^\\.]+$")) {
                    scriptPaths.add(folder + "/" + scriptName);
                }

                boolean toContinue = invokeManager.invoke(req, resp, scriptPaths);
                if (!toContinue) {
                    return;
                }

                chain.doFilter(req, resp);

            } catch (GlowwormException e) {
                throw new ServletException(e);
            }
        }

        public void setAppendantFolder(String appendantFolder) {
            this.appendantFolder = appendantFolder;
        }

        public void setCodeManager(CodeManager codeManager) {
            this.codeManager = codeManager;
        }

        public void setGlobalPrefix(String globalPrefix) {
            this.globalPrefix = globalPrefix;
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

    public void init(ServletContext servletContext, String configPath) {
        ComponentContainer container = ComponentContainer.get(servletContext, configPath, configurator);
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
        try {
            init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, chain);
    }

    @Override
    public void destroy() {
    }
}