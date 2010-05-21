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

package org.withinsea.izayoi.commons.servlet;

import org.withinsea.izayoi.commons.util.IOUtils;
import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-23
 * Time: 15:55:40
 */
public class ServletFilterUtils {

    public static Map<String, String> getParamsMap(FilterConfig filterConfig) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        Enumeration<String> enu = filterConfig.getInitParameterNames();
        while (enu.hasMoreElements()) {
            String pname = enu.nextElement();
            params.put(pname, filterConfig.getInitParameter(pname));
        }
        return params;
    }

    public static Map<String, String> getParamsMap(ServletConfig servletConfig) {
        Map<String, String> params = new LinkedHashMap<String, String>();
        Enumeration<String> enu = servletConfig.getInitParameterNames();
        while (enu.hasMoreElements()) {
            String pname = enu.nextElement();
            params.put(pname, servletConfig.getInitParameter(pname));
        }
        return params;
    }

    public static boolean matchUrlPattern(String path, String urlPattern) {
        if (urlPattern == null) return false;
        urlPattern = urlPattern.trim();
        if (urlPattern.equals("")) return false;
        for (String pattern : urlPattern.split("[,;\\s]+")) {
            if (pattern.endsWith("/*")) {
                if (path.startsWith(pattern.substring(0, pattern.length() - 1))) return true;
            } else if (pattern.startsWith("*.")) {
                if (path.endsWith(pattern.substring(1))) return true;
            }
        }
        return false;
    }

    public static boolean matchContentType(String mimeType, String contentType) {
        if (contentType == null || contentType.equals("")) {
            return false;
        } else if (mimeType == null || mimeType.equals("")) {
            return true;
        } else {
            for (String ctItem : contentType.trim().split("[,;\\s]+")) {
                if (ctItem.indexOf("/") >= 0 && mimeType.matches(ctItem.replace("*", ".+"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isIncluded(HttpServletRequest request) {
        return request.getAttribute(RequestDispatcher.INCLUDE_SERVLET_PATH) != null;
    }

    public static boolean isForwarded(HttpServletRequest request) {
        return request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH) != null &&
                !request.getAttribute(RequestDispatcher.FORWARD_SERVLET_PATH).equals(request.getServletPath());
    }

    public static void chain(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                             Filter... filters) throws ServletException, IOException {
        chain(request, response, chain, Arrays.asList(filters));
    }

    public static void chain(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                             List<Filter> filters) throws ServletException, IOException {
        new HolderChain(chain, filters).doFilter(request, response);
    }

    protected static class HolderChain implements FilterChain {

        protected final FilterChain chain;
        protected final List<Filter> filters;

        protected int idx = 0;

        public HolderChain(FilterChain chain, List<Filter> filters) {
            this.chain = chain;
            this.filters = filters;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (idx > filters.size() - 1) {
                chain.doFilter(request, response);
            } else {
                filters.get(idx++).doFilter(request, response, this);
            }
        }
    }

    public static Set<String> getServletMappingPatterns(ServletContext servletContext) {
        return ServletRegistrations.getHelper(servletContext).getServletMappingPatterns(servletContext); 
    }

    protected static class ServletRegistrations {

        public static interface Helper {
            Set<String> getServletMappingPatterns(ServletContext servletContext);
        }

        protected static class Servlet3Helper implements Helper {
            public Set<String> getServletMappingPatterns(ServletContext servletContext) {
                Set<String> set = new HashSet<String>();
                for (ServletRegistration r : servletContext.getServletRegistrations().values()) {
                    set.addAll(r.getMappings());
                }
                return set;
            }
        }

        protected static class Servlet2Helper implements Helper {

            ServletContext servletContext;
            Set<String> set = new HashSet<String>();

            public Servlet2Helper(ServletContext servletContext) {
                this.servletContext = servletContext;
                try {
                    String deployer = IOUtils.toString(servletContext.getResourceAsStream("/WEB-INF/web.xml"), "UTF-8");
                    Matcher mapping = Pattern.compile("<servlet-mapping>([\\s\\S]+?)</servlet-mapping>").matcher(deployer);
                    while (mapping.find()) {
                        Matcher pattern = Pattern.compile("<url-pattern>([\\s\\S]+?)</url-pattern>").matcher(mapping.group(1));
                        while (mapping.find()) {
                            set.add(pattern.group(1).trim());
                        }
                    }
                } catch (Exception e) {
                    // do nothing;
                }
            }

            public Set<String> getServletMappingPatterns(ServletContext servletContext) {
                return (this.servletContext == servletContext) ? set :
                        new Servlet2Helper(servletContext).getServletMappingPatterns(servletContext);
            }
        }

        protected static Map<ServletContext, Helper> HELPERS = new LazyLinkedHashMap<ServletContext, Helper>() {
            @Override
            protected Helper createValue(ServletContext servletContext) {
                return ServletRegistrations.createHelper(servletContext);
            }
        };

        protected static Helper createHelper(ServletContext servletContext) {
            if (servletContext.getMajorVersion() >= 3) {
                return new Servlet3Helper();
            } else {
                return new Servlet2Helper(servletContext);
            }
        }

        public static Helper getHelper(ServletContext servletContext) {
            return HELPERS.get(servletContext);
        }
    }
}