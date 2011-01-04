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

package org.withinsea.izayoi.common.servlet;

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
public class ServletFilterUtils {

    public static final String FORWARD_REQUEST_URI = "javax.servlet.forward.request_uri";
    public static final String FORWARD_CONTEXT_PATH = "javax.servlet.forward.context_path";
    public static final String FORWARD_PATH_INFO = "javax.servlet.forward.path_info";
    public static final String FORWARD_SERVLET_PATH = "javax.servlet.forward.servlet_path";
    public static final String FORWARD_QUERY_STRING = "javax.servlet.forward.query_string";
    public static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
    public static final String INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
    public static final String INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    public static final String INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";
    public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

    public static boolean matchUrlPattern(String path, List<String> urlPatterns) {
        for (String pattern : urlPatterns) {
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
        return request.getAttribute(INCLUDE_SERVLET_PATH) != null;
    }

    public static boolean isForwarded(HttpServletRequest request) {
        return !isIncluded(request) && request.getAttribute(FORWARD_SERVLET_PATH) != null;
    }

    public static String getRequestPath(HttpServletRequest request) {
        return isIncluded(request) ? (String) request.getAttribute(INCLUDE_SERVLET_PATH) : request.getServletPath();
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
}