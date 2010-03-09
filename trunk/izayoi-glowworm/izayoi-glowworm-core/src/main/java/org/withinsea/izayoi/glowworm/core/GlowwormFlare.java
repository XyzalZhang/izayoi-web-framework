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
import org.withinsea.izayoi.core.conf.ComponentContainer;
import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfigurator;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.invoke.ActManager;
import org.withinsea.izayoi.glowworm.core.invoke.InjectManager;
import org.withinsea.izayoi.glowworm.core.invoke.InvokeManager;
import org.withinsea.izayoi.glowworm.core.invoke.Scope;

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
public class GlowwormFlare implements Filter, Configurable {

    // config

    protected Configurator configurator = new GlowwormConfigurator();

    @Override
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    // api

    protected DataDispatcher dataDispatcher;
    protected ActionDispatcher actionDispatcher;

    public void init(ServletContext servletContext, String configPath) throws GlowwormException {
        ComponentContainer container = ComponentContainer.get(configurator, servletContext, configPath);
        dataDispatcher = container.getComponent(DataDispatcher.class);
        actionDispatcher = container.getComponent(ActionDispatcher.class);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, final FilterChain chain) throws GlowwormException {
        dataDispatcher.doDispatch(req, resp, new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
                try {
                    actionDispatcher.doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, chain);
                } catch (GlowwormException e) {
                    throw (e.getCause() instanceof ServletException) ? (ServletException) e.getCause() : new ServletException(e);
                }
            }
        });
    }

    // dispatcher

    public static class DataDispatcher extends Dispatcher {

        protected InjectManager injectManager;
        protected String dataSuffix;

        @Override
        protected InvokeManager getInvokeManager() {
            return injectManager;
        }

        @Override
        protected String getSuffix() {
            return dataSuffix;
        }

        public void setDataSuffix(String dataSuffix) {
            this.dataSuffix = dataSuffix;
        }

        public void setInjectManager(InjectManager injectManager) {
            this.injectManager = injectManager;
        }
    }

    public static class ActionDispatcher extends Dispatcher {

        protected ActManager actManager;
        protected String actionSuffix;

        @Override
        protected InvokeManager getInvokeManager() {
            return actManager;
        }

        @Override
        protected String getSuffix() {
            return actionSuffix;
        }

        public void setActionSuffix(String actionSuffix) {
            this.actionSuffix = actionSuffix;
        }

        public void setActManager(ActManager actManager) {
            this.actManager = actManager;
        }
    }

    public static abstract class Dispatcher {

        protected static final String NAMEPARAM_INVOKED_FLAG_ATTR = Dispatcher.class.getCanonicalName() + ".NAMEPARAM_INVOKED_FLAG";
        protected static final String GLOBAL_INVOKED_FLAG_ATTR = Dispatcher.class.getCanonicalName() + ".GLOBAL_INVOKED_FLAG";
        protected static final String INVOKED_FLAG_ATTR = Dispatcher.class.getCanonicalName() + ".INVOKED_FLAG";

        protected ServletContext servletContext;
        protected CodeManager codeManager;
        protected String scriptFolder;
        protected String globalPrefix;

        protected abstract InvokeManager getInvokeManager();

        protected abstract String getSuffix();

        public void doDispatch(HttpServletRequest req, HttpServletResponse resp, FilterChain chain) throws GlowwormException {

            InvokeManager invokeManager = getInvokeManager();

            String requestPath = req.getServletPath();

            try {

                if (req.getAttribute(INVOKED_FLAG_ATTR) == null) {

                    // template path parameters mapping

                    if (req.getAttribute(NAMEPARAM_INVOKED_FLAG_ATTR) == null) {
                        Map<String, String> appendentParams = new LinkedHashMap<String, String>();
                        String templateRequestPath = matchPathTemplate(appendentParams, "/", requestPath.substring(1));
                        if (templateRequestPath != null && !templateRequestPath.equals(requestPath) && !appendentParams.isEmpty()) {
                            ParamsAdjustHttpServletRequestWrapper reqw = new ParamsAdjustHttpServletRequestWrapper(req);
                            for (Map.Entry<String, String> e : appendentParams.entrySet()) {
                                reqw.appendParam(e.getKey(), e.getValue());
                            }
                            req.setAttribute(NAMEPARAM_INVOKED_FLAG_ATTR, true);
                            reqw.getRequestDispatcher(templateRequestPath).forward(reqw, resp);
                            return;
                        }
                    }

                    // global injections

                    if (servletContext.getAttribute(GLOBAL_INVOKED_FLAG_ATTR) == null) {
                        for (String scriptName : codeManager.listNames(scriptFolder, Pattern.quote(globalPrefix + "-application" + getSuffix() + ".") + "\\w+")) {
                            if (!invokeManager.invoke(req, resp, scriptFolder + "/" + scriptName, null, Scope.APPLICATION)) {
                                return;
                            }
                        }
                        servletContext.setAttribute(GLOBAL_INVOKED_FLAG_ATTR, true);
                    }

                    if (req.getSession().getAttribute(GLOBAL_INVOKED_FLAG_ATTR) == null) {
                        for (String scriptName : codeManager.listNames(scriptFolder, Pattern.quote(globalPrefix + "-session" + getSuffix() + ".") + "\\w+")) {
                            if (!invokeManager.invoke(req, resp, scriptFolder + "/" + scriptName, null, Scope.SESSION)) {
                                return;
                            }
                        }
                        req.getSession().setAttribute(GLOBAL_INVOKED_FLAG_ATTR, true);
                    }

                    String folderPath = PathUtils.getFolderPath(requestPath);
                    String globalFolderPath = scriptFolder;
                    for (String folderPathSplitItem : folderPath.split("/+")) {
                        globalFolderPath = globalFolderPath + folderPathSplitItem + "/";
                        if (req.getAttribute(GLOBAL_INVOKED_FLAG_ATTR + "#" + globalFolderPath) == null) {
                            for (String scriptName : codeManager.listNames(globalFolderPath, Pattern.quote(globalPrefix + getSuffix() + ".") + "\\w+")) {
                                if (!invokeManager.invoke(req, resp, scriptFolder + "/" + scriptName, null, Scope.REQUEST)) {
                                    return;
                                }
                            }
                            req.setAttribute(GLOBAL_INVOKED_FLAG_ATTR + "#" + globalFolderPath, true);
                        }
                    }

                    // request injection

                    if (!requestPath.endsWith("/")) {
                        Matcher dataMatcher = Pattern.compile(Pattern.quote(folderPath) + "/(.+)" + Pattern.quote(getSuffix()) + "\\.\\w+").matcher(requestPath);
                        if (dataMatcher.matches() && codeManager.exist(requestPath)) {
                            // direct access to glowworm data file
                            if (!invokeManager.invoke(req, resp, requestPath, null, Scope.REQUEST)) {
                                return;
                            }
                            req.setAttribute(INVOKED_FLAG_ATTR, true);
                            req.getRequestDispatcher("/" + dataMatcher.group(1)).forward(req, resp);
                        } else {
                            // auto bind
                            String main = PathUtils.getMainName(requestPath);
                            String ext = PathUtils.getExtName(requestPath);
                            for (String scriptName : codeManager.listNames(folderPath,
                                    Pattern.quote(main) + "(|" + Pattern.quote("." + ext) + ")" + Pattern.quote(getSuffix()) + "\\.\\w+")) {
                                if (!invokeManager.invoke(req, resp, folderPath + "/" + scriptName, null, Scope.REQUEST)) {
                                    return;
                                }
                                req.setAttribute(INVOKED_FLAG_ATTR, true);
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

        public void setGlobalPrefix(String globalPrefix) {
            this.globalPrefix = globalPrefix;
        }

        public void setScriptFolder(String scriptFolder) {
            this.scriptFolder = scriptFolder;
        }

        public void setServletContext(ServletContext servletContext) {
            this.servletContext = servletContext;
        }
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

    @Override
    public void destroy() {
    }
}