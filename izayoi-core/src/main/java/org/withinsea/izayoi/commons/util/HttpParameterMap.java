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

package org.withinsea.izayoi.commons.util;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-23
 * Time: 4:23:02
 */
public class HttpParameterMap implements Map<String, Object> {

    public static final String READONLY_ERROR_MSG = HttpParameterMap.class.getSimpleName() + " is readonly";

    private final HttpServletRequest request;

    public HttpParameterMap(HttpServletRequest request) {
        this.request = request;
    }

    public int size() {
        return getParameterMap().size();
    }

    public boolean isEmpty() {
        return getParameterMap().isEmpty();
    }

    public boolean containsKey(Object key) {
        return getParameterMap().containsKey(key);
    }

    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    public Object get(Object key) {
        return toSingleValue(getParameterMap().get(key));
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

    public Set<String> keySet() {
        return getParameterMap().keySet();
    }

    public Collection<Object> values() {
        Set<Object> values = new HashSet<Object>();
        for (Object v : getParameterMap().values()) {
            values.add(toSingleValue(v));
        }
        return values;
    }

    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Entry<String, String[]> e : getParameterMap().entrySet()) {
            map.put(e.getKey(), toSingleValue(e.getValue()));
        }
        return map.entrySet();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    protected Map<String, String[]> getParameterMap() {
        return getRequest().getParameterMap();
    }

    protected static Object toSingleValue(Object obj) {
        if (obj != null && obj.getClass().isArray() && Array.getLength(obj) == 1) {
            obj = Array.get(obj, 0);
        }
        return obj;
    }
}