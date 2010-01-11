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

import org.withinsea.izayoi.commons.util.IOUtils;
import org.withinsea.izayoi.glowworm.core.conf.Configurable;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.injector.Injector;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 16:02:45
 */
public class GlowwormLight implements Filter {

    protected ServletContext servletContext;
    protected Properties conf;
    protected Map<String, Injector> injectors;

    public void init(FilterConfig filterConfig) throws ServletException {

        servletContext = filterConfig.getServletContext();

        conf = new Properties();
        {
            String confPath = filterConfig.getInitParameter("config-path");
            for (InputStream is : new InputStream[]{
                    getClass().getResourceAsStream("conf/glowworm.properties"),
                    servletContext.getResourceAsStream(confPath == null ? "/WEB-INF/glowworm.properties" : confPath)
            }) {
                if (is != null) {
                    try {
                        conf.load(is);
                    } catch (IOException e) {
                        throw new ServletException();
                    }
                }
            }
        }

        injectors = new LinkedHashMap<String, Injector>();
        {
            Map<String, Injector> injs = new LinkedHashMap<String, Injector>();
            for (String name : conf.stringPropertyNames()) {
                if (name.startsWith("glowworm.class.injector.")) {
                    String type = name.substring("glowworm.class.injector.".length());
                    try {
                        @SuppressWarnings("unchecked")
                        Class<? extends Injector> claz = (Class<? extends Injector>) Class.forName(conf.getProperty(name));
                        Injector injector = claz.newInstance();
                        if (injector instanceof Configurable) {
                            ((Configurable) injector).setConf(conf);
                        }
                        injs.put(type, injector);
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
            }
            for (String type : conf.getProperty("glowworm.types").trim().split("\\s*[;,]\\s*")) {
                if (injs.containsKey(type)) {
                    injectors.put(type, injs.get(type));
                }
            }
            injectors.putAll(injs);
        }
    }

    @SuppressWarnings("unchecked")
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
        }

        String encoding = conf.getProperty("glowworm.encoding");
        String gwFolder = conf.getProperty("glowworm.folder").replaceAll("/+$", "/");
        String gwSuffix = conf.getProperty("glowworm.suffix");
        String globalName = conf.getProperty("glowworm.globalName");

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getServletPath();

        if (path.endsWith("/")) {
            chain.doFilter(request, response);
        }

        if (globalName != null && !globalName.equals("")) {
            req.setAttribute(globalName, null);
        }

        try {

            for (String type : injectors.keySet()) {
                String regexp = Pattern.quote(gwFolder) + "(.+)" + Pattern.quote(gwSuffix) + ".*?\\." + type;
                if (path.matches(regexp)) {
                    String srcPath = path;
                    path = "//" + srcPath.replaceAll(regexp, "$1");
                    InputStream is = servletContext.getResourceAsStream(srcPath);
                    if (is != null) {
                        String src = IOUtils.toString(servletContext.getResourceAsStream(srcPath), encoding);
                        injectors.get(type).inject(req, resp, srcPath, src);
                        req.getRequestDispatcher(path).forward(req, resp);
                        return;
                    }
                }
            }

            injs:
            for (String type : injectors.keySet()) {
                String folder = path.replaceAll("/[^/]*$", "/");
                String name = path.substring(folder.length());
                String suffix = (name.indexOf(".") < 0) ? "" : name.replaceAll(".*\\.", ".");
                String main = (suffix.equals("")) ? name : name.substring(0, name.length() - suffix.length());
                String regexp = Pattern.quote(main) + "(|" + Pattern.quote(suffix) + ")" + Pattern.quote(gwSuffix) + ".*?\\." + type;
                String srcFolderPath = folder + gwFolder;
                for (File f : new File(servletContext.getRealPath(srcFolderPath)).listFiles()) {
                    if (f.getName().matches(regexp)) {
                        String src = IOUtils.toString(f, encoding);
                        injectors.get(type).inject(req, resp, srcFolderPath + "/" + f.getName(), src);
                        break injs;
                    }
                }
            }

        } catch (GlowwormException e) {
            throw new ServletException(e);
        }

        chain.doFilter(req, resp);
    }

    public void destroy() {

    }
}
