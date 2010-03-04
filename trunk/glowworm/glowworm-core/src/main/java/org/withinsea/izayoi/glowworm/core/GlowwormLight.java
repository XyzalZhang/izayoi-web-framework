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
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.PathUtils;
import org.withinsea.izayoi.core.conf.IzayoiConfig;
import org.withinsea.izayoi.core.conf.IzayoiConfigurable;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfig;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.inject.InjectManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-12
 * Time: 23:49:57
 */
public class GlowwormLight implements Filter, IzayoiConfigurable {

    public static class Dispatcher {

        protected static final String NAMEPARAM_INJECTED_FLAG_ATTR = Dispatcher.class.getCanonicalName() + ".NAMEPARAM_INJECTED_FLAG";
        protected static final String GLOBAL_INJECTED_FLAG_ATTR = Dispatcher.class.getCanonicalName() + ".GLOBAL_INJECTED_FLAG";
        protected static final String INJECTED_FLAG_ATTR = Dispatcher.class.getCanonicalName() + ".INJECTED_FLAG";

        protected ServletContext servletContext;
        protected CodeManager codeManager;
        protected InjectManager injectManager;
        protected String dataFolder;
        protected String globalPrefix;
        protected String dataSuffix;

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, String requestPath, FilterChain chain) throws GlowwormException {

            requestPath = (requestPath == null) ? req.getServletPath() : requestPath;

            try {

                if (req.getAttribute(INJECTED_FLAG_ATTR) == null) {

                    // template path parameters mapping

                    if (req.getAttribute(NAMEPARAM_INJECTED_FLAG_ATTR) == null) {
                        Map<String, String> appendentParams = new LinkedHashMap<String, String>();
                        String templateRequestPath = matchPathTemplate(appendentParams, "/", requestPath.substring(1));
                        if (templateRequestPath != null && !templateRequestPath.equals(requestPath) && !appendentParams.isEmpty()) {
                            ParamsAdjustHttpServletRequestWrapper reqw = new ParamsAdjustHttpServletRequestWrapper(req);
                            for (Map.Entry<String, String> e : appendentParams.entrySet()) {
                                reqw.appendParam(e.getKey(), e.getValue());
                            }
                            req.setAttribute(NAMEPARAM_INJECTED_FLAG_ATTR, true);
                            reqw.getRequestDispatcher(templateRequestPath).forward(reqw, resp);
                            return;
                        }
                    }

                    // global injections

                    if (servletContext.getAttribute(GLOBAL_INJECTED_FLAG_ATTR) == null) {
                        for (String dataName : codeManager.listNames(dataFolder, Pattern.quote(globalPrefix + "-application" + dataSuffix + ".") + "\\w+")) {
                            injectManager.inject(req, InjectManager.Scope.APPLICATION, dataFolder + "/" + dataName, null);
                        }
                        servletContext.setAttribute(GLOBAL_INJECTED_FLAG_ATTR, true);
                    }

                    if (req.getSession().getAttribute(GLOBAL_INJECTED_FLAG_ATTR) == null) {
                        for (String dataName : codeManager.listNames(dataFolder, Pattern.quote(globalPrefix + "-session" + dataSuffix + ".") + "\\w+")) {
                            injectManager.inject(req, InjectManager.Scope.SESSION, dataFolder + "/" + dataName, null);
                        }
                        req.getSession().setAttribute(GLOBAL_INJECTED_FLAG_ATTR, true);
                    }

                    String folderPath = PathUtils.getFolderPath(requestPath);
                    String globalFolderPath = dataFolder;
                    for (String folderPathSplitItem : folderPath.split("/+")) {
                        globalFolderPath = globalFolderPath + folderPathSplitItem + "/";
                        if (req.getAttribute(GLOBAL_INJECTED_FLAG_ATTR + "#" + globalFolderPath) == null) {
                            for (String dataName : codeManager.listNames(globalFolderPath, Pattern.quote(globalPrefix + dataSuffix + ".") + "\\w+")) {
                                injectManager.inject(req, InjectManager.Scope.REQUEST, globalFolderPath + "/" + dataName, null);
                            }
                            req.setAttribute(GLOBAL_INJECTED_FLAG_ATTR + "#" + globalFolderPath, true);
                        }
                    }

                    // request injection

                    if (!requestPath.endsWith("/")) {
                        Matcher dataMatcher = Pattern.compile(Pattern.quote(folderPath) + "/(.+)" + Pattern.quote(dataSuffix) + "\\.\\w+").matcher(requestPath);
                        if (dataMatcher.matches() && codeManager.exist(requestPath)) {
                            // direct access to glowworm data file
                            injectManager.inject(req, InjectManager.Scope.REQUEST, requestPath, null);
                            req.setAttribute(INJECTED_FLAG_ATTR, true);
                            req.getRequestDispatcher("/" + dataMatcher.group(1)).forward(req, resp);
                        } else {
                            // auto bind
                            String main = PathUtils.getMainName(requestPath);
                            String ext = PathUtils.getExtName(requestPath);
                            for (String dataName : codeManager.listNames(folderPath, Pattern.quote(main) + "(|" + Pattern.quote("." + ext) + ")" + Pattern.quote(dataSuffix) + "\\.\\w+")) {
                                injectManager.inject(req, InjectManager.Scope.REQUEST, folderPath + "/" + dataName, null);
                                req.setAttribute(INJECTED_FLAG_ATTR, true);
                            }
                        }
                    }
                }

                chain.doFilter(req, resp);

            } catch (Exception e) {
                throw new GlowwormException(e);
            }
        }

        protected static final Pattern PATH_TEMPLATE_PATTERN = Pattern.compile("\\{\\w+\\}");

        protected int scorePathTemplate(String name) {
            Matcher matcher = PATH_TEMPLATE_PATTERN.matcher(name);
            int count = 0;
            while (matcher.find()) count++;
            return -count;
        }

        protected String matchPathTemplate(Map<String, String> params, String basePath, String path) {

            if (path.equals("")) {
                return basePath;
            }

            String fname = path.replaceAll("/.*", "");

            List<String> codeNames = codeManager.listNames(basePath);
            Collections.sort(codeNames, new Comparator<String>() {
                @Override
                public int compare(String n1, String n2) {
                    return scorePathTemplate(n1) - scorePathTemplate(n2);
                }
            });

            for (String tname : codeNames) {
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
                    String ret = codeManager.get(basePath + "/" + tname).isFolder()
                            ? matchPathTemplate(params, basePath + "/" + tname, path.substring(fname.length() + 1))
                            : basePath + (basePath.equals("") ? "" : "/") + tname;
                    return (ret == null) ? null : ret.replaceAll("^/+", "/");
                }
            }

            return null;
        }

        public void setCodeManager(CodeManager codeManager) {
            this.codeManager = codeManager;
        }

        public void setDataFolder(String dataFolder) {
            this.dataFolder = dataFolder.replaceAll("/+$", "");
        }

        public void setDataSuffix(String dataSuffix) {
            this.dataSuffix = dataSuffix;
        }

        public void setGlobalPrefix(String globalPrefix) {
            this.globalPrefix = globalPrefix;
        }

        public void setInjectManager(InjectManager injectManager) {
            this.injectManager = injectManager;
        }

        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }
    }

    // api

    protected Dispatcher dispatcher;

    @Override
    public IzayoiConfig config(ServletContext servletContext, String configPath) {
        return new GlowwormConfig(servletContext, configPath);
    }

    public void init(ServletContext servletContext, String configPath) throws GlowwormException {
        dispatcher = config(servletContext, configPath).getComponent(Dispatcher.class);
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