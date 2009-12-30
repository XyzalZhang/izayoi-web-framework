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

package org.withinsea.izayoi.cortile.util;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-22
 * Time: 7:23:47
 */
public class HttpContextMap implements Map<String, Object> {

    public static final String READONLY_ERROR_MSG = HttpContextMap.class.getSimpleName() + " is readonly";

    private final PageContext pageContext;
    private final HttpServletRequest request;

    public HttpContextMap(PageContext pageContext) {
        this.pageContext = pageContext;
        this.request = null;
    }

    public HttpContextMap(HttpServletRequest request) {
        this.pageContext = null;
        this.request = request;
    }

    public int size() {
        return keySet().size();
    }

    public boolean isEmpty() {
        return ((getPageContext() == null ||
                !getPageContext().getAttributeNamesInScope(PageContext.PAGE_SCOPE).hasMoreElements()) &&
                !getRequest().getAttributeNames().hasMoreElements() &&
                !getSession().getAttributeNames().hasMoreElements() &&
                !getServletContext().getAttributeNames().hasMoreElements());
    }

    public boolean containsKey(Object key) {
        return keySet().contains(key.toString());
    }

    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    public Object get(Object key) {
        String k = (String) key;
        Object v = (getPageContext() == null) ? null : getPageContext().getAttribute(k, PageContext.PAGE_SCOPE);
        if (v == null) v = getRequest().getAttribute(k);
        if (v == null) v = getSession().getAttribute(k);
        if (v == null) v = getServletContext().getAttribute(k);
        return v;
    }

    public Object put(String key, Object value) {
        throw new UnsupportedOperationException(READONLY_ERROR_MSG);
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException(READONLY_ERROR_MSG);
    }

    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException(READONLY_ERROR_MSG);
    }

    public void clear() {
        throw new UnsupportedOperationException(READONLY_ERROR_MSG);
    }

    @SuppressWarnings("unchecked")
    public Set<String> keySet() {
        Set<String> keys = new HashSet<String>();
        if (getPageContext() != null) {
            putAll(keys, getPageContext().getAttributeNamesInScope(PageContext.PAGE_SCOPE));
        }
        putAll(keys, getRequest().getAttributeNames());
        putAll(keys, getSession().getAttributeNames());
        putAll(keys, getServletContext().getAttributeNames());
        return keys;
    }

    public Collection<Object> values() {
        Set<Object> vs = new HashSet<Object>();
        for (String k : keySet()) {
            vs.add(get(k));
        }
        return vs;
    }

    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String k : keySet()) {
            map.put(k, get(k));
        }
        return map.entrySet();
    }

    protected PageContext getPageContext() {
        return pageContext;
    }

    protected ServletRequest getRequest() {
        return (request != null) ? request : pageContext.getRequest();
    }

    protected HttpSession getSession() {
        return (request != null) ? request.getSession() : pageContext.getSession();
    }

    protected ServletContext getServletContext() {
        return getSession().getServletContext();
    }

    protected static <T> void putAll(java.util.Collection<T> c, Enumeration<? extends T> e) {
        while (e.hasMoreElements()) {
            c.add(e.nextElement());
        }
    }
}