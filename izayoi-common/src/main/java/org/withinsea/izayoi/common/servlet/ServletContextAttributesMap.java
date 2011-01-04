package org.withinsea.izayoi.common.servlet;

import javax.script.Bindings;
import javax.servlet.ServletContext;
import java.util.*;

/**
* Created by Mo Chen <withinsea@gmail.com>
* Date: 10-12-27
* Time: 下午4:03
*/
public class ServletContextAttributesMap implements Bindings, Map<String, Object> {

    protected ServletContext owner;

    public ServletContextAttributesMap(ServletContext owner) {
        this.owner = owner;
    }

    @Override
    public boolean containsKey(Object key) {
        return owner.getAttribute((String) key) != null;
    }

    @Override
    public void putAll(Map<? extends String, ?> toMerge) {
        for (Entry<? extends String, ?> entry : toMerge.entrySet()) {
            owner.setAttribute(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public int size() {
        int count = 0;
        Enumeration enu = owner.getAttributeNames();
        while (enu.hasMoreElements()) {
            enu.nextElement();
            count++;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        Enumeration enu = owner.getAttributeNames();
        return !enu.hasMoreElements();
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) return false;
        Enumeration enu = owner.getAttributeNames();
        while (enu.hasMoreElements()) {
            if (value.equals(get(enu.nextElement()))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        return owner.getAttribute((String) key);
    }

    @Override
    public Object put(String key, Object value) {
        owner.setAttribute(key, value);
        return get(key);
    }

    @Override
    public Object remove(Object key) {
        Object value = get(key);
        owner.removeAttribute((String) key);
        return value;
    }

    @Override
    public void clear() {
        for (String key : keySet()) {
            remove(key);
        }
    }

    @Override
    public Set<String> keySet() {
        Set<String> keys = new LinkedHashSet<String>();
        Enumeration enu = owner.getAttributeNames();
        while (enu.hasMoreElements()) {
            keys.add((String) enu.nextElement());
        }
        return keys;
    }

    @Override
    public Collection<Object> values() {
        Set<Object> values = new LinkedHashSet<Object>();
        Enumeration enu = owner.getAttributeNames();
        while (enu.hasMoreElements()) {
            values.add(get(enu.nextElement()));
        }
        return values;
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        Map<String, Object> copyMap = new LinkedHashMap<String, Object>();
        Enumeration enu = owner.getAttributeNames();
        while (enu.hasMoreElements()) {
            Object key = enu.nextElement();
            copyMap.put((String) key, get(key));
        }
        return copyMap.entrySet();
    }
}
