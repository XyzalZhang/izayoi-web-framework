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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class ParamIgnoringHttpServletRequestWrapper extends HttpServletRequestWrapper {

    protected final Set<String> ignoreParams = new HashSet<String>();

    public ParamIgnoringHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    public ParamIgnoringHttpServletRequestWrapper(HttpServletRequest request, String... ignoreParams) {
        this(request, Arrays.asList(ignoreParams));
    }

    public ParamIgnoringHttpServletRequestWrapper(HttpServletRequest request, Collection<String> ignoreParams) {
        this(request);
        ignoreParams.addAll(ignoreParams);
    }

    @Override
    public String getQueryString() {
        String queryString = "&" + super.getQueryString();
        for (String ignoreParam : ignoreParams) {
            queryString = queryString.replaceAll("\\&" + ignoreParam + "=[^\\&]*", "");
        }
        return queryString.substring(1);
    }

    @Override
    public String getParameter(String name) {
        return (ignoreParams.contains(name.toLowerCase())) ? null : super.getParameter(name);
    }

    @Override
    public String[] getParameterValues(String name) {
        return (ignoreParams.contains(name.toLowerCase())) ? null : super.getParameterValues(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map getParameterMap() {
        Map map = new java.util.HashMap(super.getParameterMap());
        for (String ignoreParam : ignoreParams) {
            map.remove(ignoreParam);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration getParameterNames() {
        return new Vector(getParameterMap().keySet()).elements();
    }

    public Set<String> getIgnoreParams() {
        return ignoreParams;
    }
}
