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

import org.withinsea.izayoi.commons.servlet.ParamsAdjustHttpServletRequestWrapper;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfig;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.inject.InjectManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-12
 * Time: 23:49:57
 */
public class GlowwormLight implements Filter {

    public static class Dispatcher {

        protected ServletContext servletContext;
        protected InjectManager injectManager;
        protected File webroot;
        protected String dataFolder;
        protected String dataSuffix;
        protected String globalName;

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws GlowwormException {

            requestPath = (requestPath == null) ? req.getServletPath() : requestPath;

            try {

                // path parameters mapping

                String realRequestPath = requestPath;
                Map<String, String> appendentParams = new LinkedHashMap<String, String>();
                String templateRequestPath = matchPathTemplate(appendentParams, webroot, "/", requestPath.substring(1));
                if (templateRequestPath != null) {
                    requestPath = templateRequestPath;
                    if (!appendentParams.isEmpty()) {
                        ParamsAdjustHttpServletRequestWrapper reqw = new ParamsAdjustHttpServletRequestWrapper(req);
                        for (Map.Entry<String, String> e : appendentParams.entrySet()) {
                            reqw.appendParam(e.getKey(), e.getValue());
                        }
                        req = reqw;
                    }
                }

                if (requestPath.endsWith("/")) {
                    chain.doFilter(req, resp);
                }

                // clear global injected object

                if (globalName != null && !globalName.equals("")) {
                    req.removeAttribute(globalName);
                }

                // direct access to glowworm data file

                for (String type : injectManager.getSupportedTypes()) {
                    String regexp = Pattern.quote(dataFolder) + "(.+)" + Pattern.quote(dataSuffix) + ".*?\\." + type;
                    if (requestPath.matches(regexp)) {
                        if (injectManager.exist(requestPath)) {
                            injectManager.inject(req, requestPath, type);
                            req.getRequestDispatcher("/" + requestPath.replaceAll(regexp, "$1")).forward(req, resp);
                            return;
                        }
                    }
                }

                // auto bind

                types:
                for (String type : injectManager.getSupportedTypes()) {
                    String folderPath = requestPath.replaceAll("/[^/]*$", "/");
                    String name = requestPath.substring(folderPath.length());
                    String main = name.substring(0, name.indexOf(".") < 0 ? name.length() : name.lastIndexOf("."));
                    String suffix = name.substring(main.length());
                    String regexp = Pattern.quote(main) + "(|" + Pattern.quote(suffix) + ")" + Pattern.quote(dataSuffix) + ".*?\\." + type;
                    File folder = new File(servletContext.getRealPath(folderPath + dataFolder));
                    if (folder.exists() && folder.isDirectory()) {
                        for (File f : folder.listFiles()) {
                            if (f.getName().matches(regexp)) {
                                injectManager.inject(req, dataFolder + "/" + f.getName(), type);
                                break types;
                            }
                        }
                    }
                }

                if (!realRequestPath.endsWith(requestPath)) {
                    req.getRequestDispatcher(requestPath).forward(req, resp);
                } else {
                    chain.doFilter(req, resp);
                }

            } catch (Exception e) {
                throw new GlowwormException(e);
            }
        }

        protected String matchPathTemplate(Map<String, String> params, File root, String basePath, String path) {

            if (path.equals("")) {
                return basePath;
            }

            List<File> files = Arrays.asList(new File(root, basePath).listFiles());
            Collections.sort(files, new Comparator<File>() {

                int score(String name) {
                    return name.matches("\\{\\w+\\}") ? 0 : name.matches(".*\\{\\w+\\}.*") ? 1 : 2;
                }

                @Override
                public int compare(File o1, File o2) {
                    return score(o2.getName()) - score(o1.getName());
                }
            });

            String nextName = path.replaceAll("/.*", "");
            for (File f : files) {
                String fname = nextName.replaceAll(Pattern.quote(dataSuffix) + ".*", "").replaceAll("\\..*", "");
                String tname = f.getName().replaceAll(Pattern.quote(dataSuffix) + ".*", "").replaceAll("\\..*", "");
                Matcher nameMatcher = Pattern.compile(tname.replaceAll("\\{\\w+\\}", "(.+?)")).matcher(fname);
                if (nameMatcher.matches()) {
                    Matcher templateMatcher = Pattern.compile(tname.replaceAll("\\{(\\w+)\\}", "\\\\{($1)\\\\}")).matcher(tname);
                    templateMatcher.matches();
                    for (int i = 1; i <= templateMatcher.groupCount(); i++) {
                        params.put(templateMatcher.group(i), nameMatcher.group(i));
                    }
                    return f.isFile() ? basePath + (basePath.equals("") ? "" : "/") + f.getName() :
                            matchPathTemplate(params, root, basePath + "/" + f.getName(), path.substring(nextName.length() + 1));
                }
            }

            return null;
        }

        public void setDataFolder(String dataFolder) {
            this.dataFolder = dataFolder;
        }

        public void setDataSuffix(String dataSuffix) {
            this.dataSuffix = dataSuffix;
        }

        public void setGlobalName(String globalName) {
            this.globalName = globalName;
        }

        public void setInjectManager(InjectManager injectManager) {
            this.injectManager = injectManager;
        }

        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }

        public void setWebroot(File webroot) {
            this.webroot = webroot;
        }
    }

    // api

    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath) throws GlowwormException {
        dispatcher = new GlowwormConfig(servletContext, configPath).getComponent(Dispatcher.class);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws GlowwormException {
        doDispatch(req, resp, null, chain);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws GlowwormException {
        dispatcher.doDispatch(req, resp, requestPath, chain);
    }

    // as filter

    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            try {
                doDispatch(req, resp, chain);
            } catch (GlowwormException e) {
                throw new ServletException(e);
            }
            chain.doFilter(req, resp);
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {

    }
}