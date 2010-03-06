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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-12
 * Time: 21:49:10
 */
public class ParamsAdjustHttpServletRequestWrapper extends HttpServletRequestWrapper {

    protected final String uriEncoding;
    protected final Set<String> ignoreParams = new HashSet<String>();
    protected final Map<String, String[]> appendentParams = new LinkedHashMap<String, String[]>();

    public ParamsAdjustHttpServletRequestWrapper(HttpServletRequest request) {
        this(request, "UTF-8");
    }

    public ParamsAdjustHttpServletRequestWrapper(HttpServletRequest request, String uriEncoding) {
        super(request);
        this.uriEncoding = uriEncoding;
    }

    public void reset() {
        ignoreParams.clear();
        appendentParams.clear();
    }

    public void ignoreParams(String... names) {
        ignoreParams(Arrays.asList(names));
    }

    public void ignoreParams(Collection<String> names) {
        ignoreParams.addAll(names);
    }

    public void appendParam(String name, String... values) {
        appendParam(name, Arrays.asList(values));
    }

    public void appendParam(String name, Collection<String> values) {
        if (values.isEmpty()) {
            return;
        }
        @SuppressWarnings("unchecked")
        List<String> params = appendentParams.containsKey(name) ? Arrays.asList(appendentParams.get(name)) : new ArrayList();
        params.addAll(values);
        appendentParams.put(name, params.toArray(new String[params.size()]));
    }

    @Override
    public String getQueryString() {
        String queryString = (super.getQueryString() == null) ? "" : "&" + super.getQueryString();
        for (String ignoreParam : ignoreParams) {
            queryString = queryString.replaceAll("&" + ignoreParam + "=[^&]*", "");
        }
        for (String name : appendentParams.keySet()) {
            String[] params = appendentParams.get(name);
            if (params != null) {
                for (String param : params) {
                    try {
                        queryString += "&" + name + "=" + URLEncoder.encode(param, uriEncoding);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return queryString.equals("") ? null : queryString.substring(1);
    }

    @Override
    public String getParameter(String name) {
        if (ignoreParams.contains(name)) {
            return null;
        } else if (super.getParameter(name) != null) {
            return super.getParameter(name);
        } else {
            String[] params = appendentParams.get(name);
            return (params == null || params.length == 0) ? null : params[0];
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        if (ignoreParams.contains(name)) {
            return null;
        } else if (super.getParameterValues(name) != null) {
            return super.getParameterValues(name);
        } else {
            return appendentParams.get(name);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map getParameterMap() {
        Map map = new LinkedHashMap(super.getParameterMap());
        for (String ignoreParam : ignoreParams) {
            map.remove(ignoreParam);
        }
        map.putAll(appendentParams);
        return Collections.unmodifiableMap(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Enumeration getParameterNames() {
        Vector names = new Vector();
        Enumeration enu = super.getParameterNames();
        while (enu.hasMoreElements()) {
            String name = enu.nextElement().toString();
            if (!ignoreParams.contains(name)) {
                names.add(name);
            }
        }
        names.add(appendentParams.keySet());
        return names.elements();
    }
}
