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

import org.withinsea.izayoi.commons.servlet.HttpParameterMap;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:16:18
 */
public class Request extends InheritedScope {

    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected final FilterChain chain;

    public Request(HttpServletRequest request, HttpServletResponse response) {
        this(request, response, null);
    }

    public Request(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        super(new Session(request.getSession()), null);
        this.request = request;
        this.response = response;
        this.chain = chain;
        this.declaredScope = new DeclaredScope();
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

    protected static final String PARAMS_ATTR = Request.class.getCanonicalName() + ".PARAMS";

    protected static final Set<String> CONSTANT_NAMES = new LinkedHashSet<String>(Arrays.asList(
            "request", "response", "chain", "params"));

    public class DeclaredScope extends SimpleScope {

        @Override
        public Set<String> getContantNames() {
            return CONSTANT_NAMES;
        }

        @Override
        public Set<String> getAttributeNames() {
            Set<String> names = new LinkedHashSet<String>();
            Enumeration<String> enu = request.getAttributeNames();
            while (enu.hasMoreElements()) {
                names.add(enu.nextElement());
            }
            names.addAll(getParameterMap(request).keySet());
            return names;
        }

        @Override
        public boolean containsConstant(String name) {
            return CONSTANT_NAMES.contains(name);
        }

        @Override
        public boolean containsAttribute(String name) {

            Enumeration<String> enu = request.getAttributeNames();
            while (enu.hasMoreElements()) {
                String e = enu.nextElement();
                if (name.equals(e)) {
                    return true;
                }
            }
            return getParameterMap(request).containsKey(name);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getConstant(String name) {
            Object obj = name.equals("params") ? getParameterMap(request)
                    : name.equals("request") ? request
                    : name.equals("response") ? response
                    : name.equals("chain") ? chain
                    : null;
            return (T) obj;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAttribute(String name) {
            Object obj = request.getAttribute(name);
            if (obj == null) obj = getParameterMap(request).get(name);
            return (T) obj;
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
    }
}