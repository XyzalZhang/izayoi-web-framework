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

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 8:26:19
 */
public class BeanMap implements Map<String, Object> {

    public static final String READONLY_ERROR_MSG = "a " + BeanMap.class.getSimpleName() + " is readonly";

    private final Object bean;
    private final Map<String, Method> methods = new HashMap<String, Method>();

    public BeanMap(Object bean) {
        this.bean = bean;
        for (Method m : bean.getClass().getDeclaredMethods()) {
            String name = m.getName();
            m.setAccessible(true);
            if ((name.startsWith("get") || name.startsWith("is"))
                    && m.getParameterTypes().length == 0) {
                methods.put(incapitalize(name.startsWith("get") ? name.substring(3) : name.substring(2)), m);
            }
        }
    }

    public int size() {
        return keySet().size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean containsKey(Object key) {
        return methods.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return values().contains(value);
    }

    public Object get(Object key) {
        if (methods.containsKey(key.toString())) {
            try {
                return methods.get(key).invoke(bean);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
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
        return methods.keySet();
    }

    public Collection<Object> values() {
        Set<Object> values = new HashSet<Object>();
        for (String name : keySet()) {
            values.add(get(name));
        }
        return values;
    }

    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> map = new HashMap<String, Object>();
        for (String name : keySet()) {
            map.put(name, get(name));
        }
        return map.entrySet();
    }

    protected static String incapitalize(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}