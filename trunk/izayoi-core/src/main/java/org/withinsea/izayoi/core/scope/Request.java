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

package org.withinsea.izayoi.core.scope;

import org.withinsea.izayoi.commons.util.HttpParameterMap;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:16:18
 */
public class Request extends AbstractScope<Session> {

    protected static final String PARAMS_ATTR = Request.class.getCanonicalName() + ".PARAMS";

    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected final FilterChain chain;

    public Request(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, null);
    }

    public Request(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        super(new Session(request.getSession()));
        this.request = request;
        this.response = response;
        this.chain = chain;
    }

    @Override
    public Object getConstant(String name) {
        return name.equals("params") ? getParameterMap(request)
                : name.equals("request") ? request
                : name.equals("response") ? response
                : name.equals("chain") ? chain
                : null;
    }

    @Override
    public Object getAttribute(String name) {
        Object obj = request.getAttribute(name);
        if (obj == null) obj = request.getParameter(name);
        return obj;
    }

    @Override
    public void setAttribute(String name, Object obj) {
        request.setAttribute(name, obj);
    }

    protected HttpParameterMap getParameterMap(HttpServletRequest request) {
        HttpParameterMap params = (HttpParameterMap) request.getAttribute(PARAMS_ATTR);
        if (params == null) {
            params = new HttpParameterMap(request);
            request.setAttribute(PARAMS_ATTR, params);
        }
        return params;
    }

    public FilterChain getChain() {
        return chain;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}