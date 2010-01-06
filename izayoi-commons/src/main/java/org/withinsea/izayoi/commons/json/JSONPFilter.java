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
package org.withinsea.izayoi.commons.json;

import org.withinsea.izayoi.commons.servlet.ContentWrappingHttpServletResponseWrapper;
import org.withinsea.izayoi.commons.servlet.ParamIgnoringHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class JSONPFilter implements Filter {

    protected String jsonp = "callback";

    protected String[] jsonMimeTypes = new String[]{
            "application/json",
            "application/x-json",
            "text/json",
            "text/x-json"
    };

    @Override
    public void init(FilterConfig config) throws ServletException {
        String jsonp = config.getInitParameter("jsonp");
        if (jsonp != null && !jsonp.equals("")) {
            this.jsonp = jsonp;
        }
        String jsonMimeTypes = config.getInitParameter("json-mime-types");
        if (jsonMimeTypes != null) {
            if (jsonMimeTypes.equals("")) {
                this.jsonMimeTypes = new String[]{};
            } else {
                this.jsonMimeTypes = jsonMimeTypes.trim().split("\\s*,\\s*");
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse
                && request.getParameterMap().containsKey(jsonp)) {

            final HttpServletRequest req = (HttpServletRequest) request;
            ParamIgnoringHttpServletRequestWrapper reqw = new ParamIgnoringHttpServletRequestWrapper(req, jsonp);

            final HttpServletResponse resp = (HttpServletResponse) response;
            ContentWrappingHttpServletResponseWrapper respw = new ContentWrappingHttpServletResponseWrapper(resp) {

                @Override
                public String getContentType() {
                    return committed ? "text/javascript; charset=utf-8" : super.getContentType();
                }

                @Override
                public byte[] wrap(byte[] content) throws UnsupportedEncodingException {
                    String contentstr = new String(content, getCharacterEncoding());
                    String json = isJson(super.getResponse()) ? contentstr : JSONUtils.quote(contentstr);
                    String callback = req.getParameterValues(jsonp)[0];
                    return (callback + "(" + json + ");").getBytes(getCharacterEncoding());
                }

                protected boolean isJson(ServletResponse resp) {
                    String ctype = resp.getContentType();
                    if (ctype == null || ctype.equals("")) {
                        return false;
                    }
                    for (String jsonMimeType : jsonMimeTypes) {
                        if (ctype.indexOf(jsonMimeType) >= 0) {
                            return true;
                        }
                    }
                    return false;
                }
            };

            respw.setCharacterEncoding("UTF-8");
            chain.doFilter(reqw, respw);
            respw.flushWrapper();

        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }

}
