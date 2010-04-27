package org.withinsea.izayoi.commons.servlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-23
 * Time: 15:55:40
 */
public class FilterUtils {

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
}
