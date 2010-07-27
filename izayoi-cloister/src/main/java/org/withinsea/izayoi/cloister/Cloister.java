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

package org.withinsea.izayoi.cloister;

import org.withinsea.izayoi.cloister.core.CloisterConfigurator;
import org.withinsea.izayoi.commons.servlet.ParamsAdjustHttpServletRequestWrapper;
import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.conf.IzayoiContainer;

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
public class Cloister implements Filter, Configurable {

    // dispatcher

    public static class Dispatcher {

        protected final String MAPPED_ATTR = Dispatcher.class.getCanonicalName() + ".MAPPED_PATH";

        protected CodeManager codeManager;
        protected String appendantFolder;
        protected String bypass;

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws ServletException, IOException {

            String requestPath = (String) req.getAttribute(ServletFilterUtils.INCLUDE_SERVLET_PATH);
            if (requestPath == null) requestPath = req.getServletPath();

            if (ServletFilterUtils.matchUrlPattern(requestPath, bypass)) {
                chain.doFilter(req, resp);
                return;
            }

            Boolean mapped = (Boolean) req.getAttribute(MAPPED_ATTR);
            if (mapped != null && mapped) {
                chain.doFilter(req, resp);
                return;
            }

            Map<String, String> pathVariables = new LinkedHashMap<String, String>();
            String templateRequestPath = matchPathTemplate(pathVariables, requestPath);
            if (templateRequestPath == null || pathVariables.isEmpty()) {
                chain.doFilter(req, resp);
                return;
            }

            if (codeManager.isFolder(templateRequestPath) && !requestPath.endsWith("/")
                    && !isMappedServlet(req.getSession().getServletContext(), templateRequestPath)) {
                resp.sendRedirect(req.getSession().getServletContext().getContextPath() + requestPath + "/");
                return;
            }

            ParamsAdjustHttpServletRequestWrapper reqw = new ParamsAdjustHttpServletRequestWrapper(req);
            for (Map.Entry<String, String> e : pathVariables.entrySet()) {
                reqw.appendParam(e.getKey(), e.getValue());
            }

            reqw.setAttribute(MAPPED_ATTR, true);
            reqw.getRequestDispatcher(templateRequestPath).forward(reqw, resp);
            reqw.removeAttribute(MAPPED_ATTR);
        }

        protected boolean isMappedServlet(ServletContext servletContext, String path) {
            for (String pattern : ServletFilterUtils.getServletMappingPatterns(servletContext)) {
                if (path.startsWith(pattern) || (pattern.startsWith("*.") && path.endsWith(pattern.substring(1)))) {
                    return true;
                }
            }
            return false;
        }

        protected static final Pattern PATH_TEMPLATE_PATTERN = Pattern.compile("\\{\\w+\\}");

        protected int scorePathTemplate(String name) {
            Matcher matcher = PATH_TEMPLATE_PATTERN.matcher(name);
            int count = 0;
            while (matcher.find()) count++;
            return -count;
        }

        protected String matchPathTemplate(Map<String, String> pathVariables, String path) {
            path = path.trim();
            if (codeManager.exist(path)) {
                return path;
            } else {
                String match = matchPathTemplate("/", pathVariables, "/", path);
                if (match == null) {
                    return null;
                } else {
                    if (path.endsWith("/") && !match.endsWith("/")) {
                        match += "/";
                    }
                    return match.replaceAll("/+", "/");
                }
            }
        }

        protected String matchPathTemplate(String templatePath, Map<String, String> pathVariables, String folder, String path) {

            path = path.replaceAll("^/+", "").replaceAll("/+", "/");
            if (path.equals("")) {
                return folder;
            }

            String pathName = path.replaceAll("/.*", "");
            if (codeManager.isFolder(folder + "/" + pathName) || codeManager.isFolder(appendantFolder + folder + "/" + pathName)) {
                return matchPathTemplate(templatePath + "/" + pathName, pathVariables, folder + "/" + pathName, path.substring(pathName.length()));
            }

            Set<String> codeNameSet = new LinkedHashSet<String>();
            codeNameSet.addAll(codeManager.listNames(folder));
            codeNameSet.addAll(codeManager.listNames(appendantFolder + "/" + folder));
            List<String> codeNames = new ArrayList<String>(codeNameSet);
            Collections.sort(codeNames, new Comparator<String>() {
                @Override
                public int compare(String n1, String n2) {
                    return scorePathTemplate(n2) - scorePathTemplate(n1);
                }
            });

            for (String codeName : codeNames) {

                String codeMainName = codeName.replaceAll("\\..*$", "");

                Matcher pathMatcher = Pattern.compile(StringUtils.replaceAll(
                        codeMainName, "\\{\\w+\\}", new StringUtils.Replace() {
                            @Override
                            public String replace(String... groups) {
                                return "(.+)";
                            }
                        }, new StringUtils.Transform() {
                            @Override
                            public String transform(String str) {
                                return Pattern.quote(str);
                            }
                        }
                ) + ".*?").matcher(pathName);

                if (pathMatcher.matches()) {

                    Matcher codeMatcher = Pattern.compile(StringUtils.replaceAll(
                            codeMainName, "\\{(\\w+)\\}", new StringUtils.Replace() {
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
                    ) + ".*?").matcher(codeName);

                    codeMatcher.matches();
                    for (int i = 1; i <= codeMatcher.groupCount(); i++) {
                        pathVariables.put(codeMatcher.group(i), pathMatcher.group(i));
                    }

                    StringBuffer templateNameBuffer = new StringBuffer(pathName);
                    for (int i = codeMatcher.groupCount(); i >= 1; i--) {
                        templateNameBuffer.replace(pathMatcher.start(i), pathMatcher.end(i), "{" + codeMatcher.group(i) + "}");
                    }
                    String templateName = templateNameBuffer.toString();

                    if (codeManager.isFolder(folder + "/" + codeName) || codeManager.isFolder(appendantFolder + folder + "/" + codeName)) {
                        return matchPathTemplate(templatePath + "/" + templateName, pathVariables, folder + "/" + codeName, path.substring(pathName.length()));
                    } else {
                        return templatePath + "/" + templateName;
                    }
                }
            }

            return null;
        }

        public void setCodeManager(CodeManager codeManager) {
            this.codeManager = codeManager;
        }

        public void setAppendantFolder(String appendantFolder) {
            this.appendantFolder = appendantFolder;
        }

        public void setBypass(String bypass) {
            this.bypass = bypass;
        }
    }

    // api

    protected Configurator configurator = new CloisterConfigurator();
    protected Dispatcher dispatcher;

    public void init(ServletContext servletContext, String configPath, Map<String, String> confOverrides) {
        IzayoiContainer container = IzayoiContainer.get(servletContext, configPath, configurator, confOverrides);
        dispatcher = container.getComponent(Dispatcher.class);
    }

    public String findTemplatePath(String requestPath) {
        Map<String, String> pathVariables = new LinkedHashMap<String, String>();
        return dispatcher.matchPathTemplate(pathVariables, requestPath.replaceAll("^/+", ""));
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
        Map<String, String> confOverrides = ServletFilterUtils.getParamsMap(filterConfig);
        confOverrides.remove("config-path");
        init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"), confOverrides);
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, chain);
    }

    @Override
    public void destroy() {
    }
}