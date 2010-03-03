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
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfig;
import org.withinsea.izayoi.glowworm.core.dependency.DependencyManager;
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

        protected static String NAMEPARAM_INJECTED_FLAG_NAME = Dispatcher.class.getCanonicalName() + ".NAMEPARAM_INJECTED_FLAG";
        protected static String GLOBAL_INJECTED_FLAG_NAME = Dispatcher.class.getCanonicalName() + ".GLOBAL_INJECTED_FLAG";
        protected static String INJECTED_FLAG_NAME = Dispatcher.class.getCanonicalName() + ".INJECTED_FLAG";

        protected ServletContext servletContext;
        protected DependencyManager dependencyManager;
        protected InjectManager injectManager;
        protected File webroot;
        protected String dataFolder;
        protected String dataSuffix;
        protected String globalPrefix;
        protected String dataObjectName;

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws GlowwormException {

            requestPath = (requestPath == null) ? req.getServletPath() : requestPath;

            try {

                if (req.getAttribute(INJECTED_FLAG_NAME) == null) {

                    // split request path

                    String folderPath = requestPath.replaceAll("/[^/]*$", "/");
                    String name = requestPath.substring(folderPath.length());
                    String main = name.substring(0, name.indexOf(".") < 0 ? name.length() : name.lastIndexOf("."));
                    String suffix = name.substring(main.length());

                    // template path parameters mapping

                    String realRequestPath = requestPath;
                    if (req.getAttribute(NAMEPARAM_INJECTED_FLAG_NAME) == null) {
                        Map<String, String> appendentParams = new LinkedHashMap<String, String>();
                        String templateRequestPath = matchPathTemplate(appendentParams, webroot, "/", requestPath.substring(1));
                        if (templateRequestPath != null && !templateRequestPath.equals(realRequestPath)) {
                            requestPath = templateRequestPath;
                            if (!appendentParams.isEmpty()) {
                                ParamsAdjustHttpServletRequestWrapper reqw = new ParamsAdjustHttpServletRequestWrapper(req);
                                for (Map.Entry<String, String> e : appendentParams.entrySet()) {
                                    reqw.appendParam(e.getKey(), e.getValue());
                                }
                                req.setAttribute(NAMEPARAM_INJECTED_FLAG_NAME, true);
                                reqw.getRequestDispatcher(requestPath).forward(reqw, resp);
                                return;
                            }
                        }
                    }

                    // global injections

                    Set<String> supportedTypes = injectManager.getSupportedTypes();

                    if (servletContext.getAttribute(GLOBAL_INJECTED_FLAG_NAME) == null) {
                        for (String type : supportedTypes) {
                            String globalFilePath = dataFolder + "/" + globalPrefix + "-application" + dataSuffix + "." + type;
                            if (injectManager.exist(globalFilePath)) {
                                injectManager.inject(req, InjectManager.Scope.APPLICATION, globalFilePath, type);
                                break;
                            }
                        }
                        servletContext.setAttribute(GLOBAL_INJECTED_FLAG_NAME, true);
                    }

                    if (req.getSession().getAttribute(GLOBAL_INJECTED_FLAG_NAME) == null) {
                        for (String type : supportedTypes) {
                            String globalFilePath = dataFolder + "/" + globalPrefix + "-session" + dataSuffix + "." + type;
                            if (injectManager.exist(globalFilePath)) {
                                injectManager.inject(req, InjectManager.Scope.SESSION, globalFilePath, type);
                                break;
                            }
                        }
                        req.getSession().setAttribute(GLOBAL_INJECTED_FLAG_NAME, true);
                    }

                    String[] folderPathSplit = folderPath.equals("/") ? new String[]{""} :
                            folderPath.replaceAll("/$", "").split("/");
                    String globalFolderPath = dataFolder;
                    for (String folderPathSplitItem : folderPathSplit) {
                        globalFolderPath = globalFolderPath + folderPathSplitItem + "/";
                        if (req.getAttribute(GLOBAL_INJECTED_FLAG_NAME + "#" + globalFolderPath) == null) {
                            for (String type : supportedTypes) {
                                String globalFilePath = globalFolderPath + globalPrefix + dataSuffix + "." + type;
                                if (injectManager.exist(globalFilePath)) {
                                    injectManager.inject(req, InjectManager.Scope.REQUEST, globalFilePath, type);
                                    break;
                                }
                            }
                            req.setAttribute(GLOBAL_INJECTED_FLAG_NAME + "#" + globalFolderPath, true);
                        }
                    }

                    if (requestPath.endsWith("/")) {
                        chain.doFilter(req, resp);
                    }

                    // direct access to glowworm data file

                    for (String type : supportedTypes) {
                        String regexp = Pattern.quote(dataFolder) + "(.+)" + Pattern.quote(dataSuffix) + "\\." + type;
                        if (requestPath.matches(regexp)) {
                            if (injectManager.exist(requestPath)) {
                                injectManager.inject(req, InjectManager.Scope.REQUEST, requestPath, type);
                                req.setAttribute(INJECTED_FLAG_NAME, true);
                                req.getRequestDispatcher("/" + requestPath.replaceAll(regexp, "$1")).forward(req, resp);
                                return;
                            }
                        }
                    }

                    // auto bind

                    types:
                    for (String type : supportedTypes) {
                        String regexp = Pattern.quote(main) + "(|" + Pattern.quote(suffix) + ")" + Pattern.quote(dataSuffix) + "\\." + type;
                        File folder = new File(servletContext.getRealPath(dataFolder + folderPath));
                        if (folder.exists() && folder.isDirectory()) {
                            for (File f : folder.listFiles()) {
                                if (f.getName().matches(regexp)) {
                                    injectManager.inject(req, InjectManager.Scope.REQUEST, dataFolder + folderPath + "/" + f.getName(), type);
                                    req.setAttribute(INJECTED_FLAG_NAME, true);
                                    break types;
                                }
                            }
                        }
                    }
                }

                chain.doFilter(req, resp);

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

            String fname = path.replaceAll("/.*", "");
            for (File templateF : files) {
                String tname = templateF.getName();
                String nameRegexp = StringUtils.replaceAll(
                        tname, "\\{\\w+\\}", new StringUtils.Replace() {
                            @Override
                            public String replace(String... groups) {
                                return "(.+?)";
                            }
                        }, new StringUtils.Transform() {
                            @Override
                            public String transform(String str) {
                                return Pattern.quote(str);
                            }
                        }
                );
                Matcher nameMatcher = Pattern.compile(nameRegexp).matcher(fname);
                if (nameMatcher.matches()) {
                    String templateRegexp = StringUtils.replaceAll(
                            tname, "\\{(\\w+)\\}", new StringUtils.Replace() {
                                @Override
                                public String replace(String... groups) {
                                    return "\\{(" + groups[1] + ")\\}";
                                }
                            }, new StringUtils.Transform() {
                                @Override
                                public String transform(String str) {
                                    return Pattern.quote(str);
                                }
                            }
                    );
                    Matcher templateMatcher = Pattern.compile(templateRegexp).matcher(tname);
                    templateMatcher.matches();
                    for (int i = 1; i <= templateMatcher.groupCount(); i++) {
                        params.put(templateMatcher.group(i), nameMatcher.group(i));
                    }
                    String ret = templateF.isFile()
                            ? basePath + (basePath.equals("") ? "" : "/") + templateF.getName()
                            : matchPathTemplate(params, root, basePath + "/" + templateF.getName(), path.substring(fname.length() + 1));
                    return (ret == null) ? null : ret.replaceAll("^/+", "/");
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

        public void setGlobalPrefix(String globalPrefix) {
            this.globalPrefix = globalPrefix;
        }

        public void setDataObjectName(String dataObjectName) {
            this.dataObjectName = dataObjectName;
        }

        public void setDependencyManager(DependencyManager dependencyManager) {
            this.dependencyManager = dependencyManager;
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
        } else {
            chain.doFilter(request, response);
        }
    }

    public void destroy() {

    }
}