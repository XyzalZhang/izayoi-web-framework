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

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-21
 * Time: 13:11:26
 */
public class LinkedProperties extends Properties {

    protected Map<Object, Object> props = new LinkedHashMap<Object, Object>();

    public LinkedProperties() {
        super();
    }

    public LinkedProperties(Properties defaults) {
        super(defaults);
    }

    @Override
    public Object get(Object key) {
        return props.get(key);
    }

    @Override
    public String getProperty(String key) {
        Object oval = get(key);
        String sval = (oval instanceof String) ? (String) oval : null;
        return ((sval == null) && (defaults != null)) ? defaults.getProperty(key) : sval;
    }

    @Override
    public boolean contains(Object value) {
        return props.containsValue(value);
    }

    @Override
    public boolean containsKey(Object key) {
        return props.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return props.containsValue(value);
    }

    @Override
    public Enumeration<Object> elements() {
        return Collections.enumeration(props.values());
    }

    @Override
    public Set<Map.Entry<Object, Object>> entrySet() {
        return props.entrySet();
    }

    @Override
    public boolean isEmpty() {
        return props.isEmpty();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Set<String> stringPropertyNames() {
        Set<String> names = new LinkedHashSet<String>();
        for (Object key : props.keySet()) {
            names.add(key.toString());
        }
        return names;
    }

    @Override
    public Enumeration<?> propertyNames() {
        return keys();
    }

    @Override
    public Enumeration<Object> keys() {
        return Collections.enumeration(props.keySet());
    }

    @Override
    public Set<Object> keySet() {
        return props.keySet();
    }

    @Override
    public Object put(Object key, Object value) {
        return props.put(key, value);
    }

    @Override
    public void putAll(Map<?, ?> t) {
        props.putAll(t);
    }

    @Override
    public Object remove(Object key) {
        return props.remove(key);
    }

    @Override
    protected void rehash() {

    }

    @Override
    public Collection<Object> values() {
        return props.values();
    }

    @Override
    public int size() {
        return props.size();
    }

    @Override
    public void clear() {
        props.clear();
    }
}
