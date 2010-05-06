package org.withinsea.izayoi.commons.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-23
 * Time: 15:55:40
 */
public class ServletFilterUtils {

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

    public static void chain(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                             Filter... filters) throws ServletException, IOException {
        chain(request, response, chain, Arrays.asList(filters));
    }

    public static void chain(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                             List<Filter> filters) throws ServletException, IOException {
        new HolderChain(chain, filters).doFilter(request, response);
    }

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
}
