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

package org.withinsea.izayoi.core.bean;

import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-19
 * Time: 16:39:59
 */
public abstract class LazyCreateBeanContainer implements BeanContainer {

    protected Map<String, Set<Object>> no = new LazyLinkedHashMap<String, Set<Object>>() {
        @Override
        protected Set<Object> createValue(String s) {
            return new LinkedHashSet<Object>();
        }
    };

    protected Map<String, Set<Class<?>>> nt = new LazyLinkedHashMap<String, Set<Class<?>>>() {
        @Override
        protected Set<Class<?>> createValue(String s) {
            return new LinkedHashSet<Class<?>>();
        }
    };

    protected Map<Class<?>, Set<Object>> to = new LazyLinkedHashMap<Class<?>, Set<Object>>() {
        @Override
        protected Set<Object> createValue(Class<?> aClass) {
            return new LinkedHashSet<Object>();
        }
    };

    protected Set<Class<?>> ts = new LinkedHashSet<Class<?>>();

    @Override
    public boolean exist(Object bean) {
        for (Set<Object> beans : no.values())
            if (beans.contains(bean))
                return true;
        for (Set<Object> beans : to.values())
            if (beans.contains(bean))
                return true;
        return false;
    }

    @Override
    public boolean exist(Class<?> claz) {
        for (Set<Object> beans : no.values())
            for (Object bean : beans)
                if (claz.isInstance(bean))
                    return true;
        for (Set<Object> beans : to.values())
            for (Object bean : beans)
                if (claz.isInstance(bean))
                    return true;
        for (Set<Class<?>> classes : nt.values())
            for (Class<?> c : classes)
                if (claz.isAssignableFrom(c))
                    return true;
        for (Class<?> c : ts)
            if (claz.isAssignableFrom(c))
                return true;
        return false;
    }

    @Override
    public boolean exist(String name) {
        return no.containsKey(name) || nt.containsKey(name);
    }

    @Override
    public synchronized <T> T get(Class<T> claz) {
        List<T> beans = list(claz);
        return beans.isEmpty() ? null : beans.get(0);
    }

    @Override
    public synchronized <T> T get(String name) {
        List<T> beans = list(name);
        return beans.isEmpty() ? null : beans.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<T> claz) {

        List<T> beans = new ArrayList<T>();

        for (Set<Object> nbeans : no.values()) {
            for (Object bean : nbeans) {
                if (claz.isInstance(bean)) {
                    beans.add((T) bean);
                }
            }
        }

        for (Class c : to.keySet()) {
            if (claz.isAssignableFrom(c)) {
                beans.addAll((Set<T>) to.get(c));
            }
        }

        for (String name : nt.keySet()) {
            Set<Class<?>> classes = nt.get(name);
            Set<Class<?>> instantedClasses = new HashSet<Class<?>>();
            for (Class<?> c : classes) {
                if (claz.isAssignableFrom(c)) {
                    try {
                        Object bean = create(c);
                        add(name, bean);
                        beans.add((T) bean);
                        instantedClasses.add(c);
                    } catch (InstantiationException e) {
                        throw new IzayoiRuntimeException(e);
                    }
                }
            }
            classes.removeAll(instantedClasses);
        }

        {
            Set<Class<?>> instantedClasses = new HashSet<Class<?>>();
            for (Class<?> c : ts) {
                if (claz.isAssignableFrom(c)) {
                    try {
                        Object bean = create(c);
                        add(bean);
                        beans.add((T) bean);
                        instantedClasses.add(c);
                    } catch (InstantiationException e) {
                        throw new IzayoiRuntimeException(e);
                    }
                }
            }
            ts.removeAll(instantedClasses);
        }

        clean();

        return beans;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> list(String name) {
        if (nt.containsKey(name) && nt.get(name).size() > 0) {
            for (Class<?> c : nt.get(name)) {
                try {
                    Object bean = create(c);
                    add(name, bean);
                } catch (InstantiationException e) {
                    throw new IzayoiRuntimeException(e);
                }
            }
            nt.remove(name);
        }
        return no.containsKey(name) ? new ArrayList<T>((Set<T>) no.get(name)) : new ArrayList<T>();
    }

    @Override
    public synchronized void add(Object bean) {
        if (!exist(bean)) {
            if (!to.containsKey(bean.getClass())) {
                to.put(bean.getClass(), new LinkedHashSet<Object>());
            }
            to.get(bean.getClass()).add(bean);
        }
    }

    @Override
    public synchronized void add(Class<?> claz) {
        ts.add(claz);
    }

    @Override
    public synchronized void add(String name, Object bean) {
        for (Set<Object> beans : to.values()) {
            beans.remove(bean);
        }
        if (!no.containsKey(name)) {
            no.put(name, new LinkedHashSet<Object>());
        }
        no.get(name).add(bean);
    }

    @Override
    public synchronized void add(String name, Class<?> claz) {
        if (!nt.containsKey(name)) {
            nt.put(name, new LinkedHashSet<Class<?>>());
        }
        nt.get(name).add(claz);
    }

    @Override
    public synchronized void add(String name, String value) {
        add(name, (Object) value);
    }

    @Override
    public synchronized void remove(Object bean) {
        for (Set<Object> beans : no.values()) {
            beans.remove(bean);
        }
        for (Set<Object> beans : to.values()) {
            beans.remove(bean);
        }
        clean();
    }

    @Override
    public synchronized void remove(Class<?> claz) {

        for (Set<Object> beans : no.values()) {
            Set<Object> removedBeans = new HashSet<Object>();
            for (Object bean : beans) {
                if (claz.isInstance(bean)) {
                    removedBeans.add(bean);
                }
            }
            beans.removeAll(removedBeans);
        }

        for (Set<Class<?>> classes : nt.values()) {
            Set<Class<?>> removedClasses = new HashSet<Class<?>>();
            for (Class<?> c : classes) {
                if (claz.isAssignableFrom(c)) {
                    removedClasses.add(c);
                }
            }
            classes.removeAll(removedClasses);
        }

        {
            Set<Class<?>> removedClasses = new HashSet<Class<?>>();
            for (Class<?> c : to.keySet()) {
                if (claz.isAssignableFrom(c)) {
                    removedClasses.add(c);
                }
            }
            for (Class<?> c : removedClasses) {
                to.remove(c);
            }
        }

        {
            Set<Class<?>> removedClasses = new HashSet<Class<?>>();
            for (Class<?> c : ts) {
                if (claz.isAssignableFrom(c)) {
                    removedClasses.add(c);
                }
            }
            ts.removeAll(removedClasses);
        }

        clean();
    }

    @Override
    public synchronized void remove(String name) {
        no.remove(name);
        nt.remove(name);
    }

    @Override
    public void set(Class<?> claz, Object bean) {
        remove(claz);
        add(bean);
    }

    @Override
    public void set(String name, Object bean) {
        remove(name);
        add(name, bean);
    }

    @Override
    public void set(String name, Class<?> claz) {
        remove(name);
        add(name, claz);
    }

    @Override
    public void set(String name, String value) {
        remove(name);
        add(name, value);
    }

    protected void clean() {

        Set<String> nokeys = new HashSet<String>(no.keySet());
        for (String k : nokeys) {
            if (no.get(k).isEmpty()) {
                no.remove(k);
            }
        }

        Set<String> ntkeys = new HashSet<String>(nt.keySet());
        for (String k : ntkeys) {
            if (nt.get(k).isEmpty()) {
                nt.remove(k);
            }
        }

        Set<Class<?>> tokeys = new HashSet<Class<?>>(to.keySet());
        for (Class<?> k : tokeys) {
            if (to.get(k).isEmpty()) {
                to.remove(k);
            }
        }
    }
}
