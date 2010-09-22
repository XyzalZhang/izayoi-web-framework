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

package org.withinsea.izayoi.core.interpret;

import org.withinsea.izayoi.core.bean.BeanContainer;
import org.withinsea.izayoi.core.bean.BeanSource;
import org.withinsea.izayoi.core.scope.Scope;

import javax.script.Bindings;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-21
 * Time: 11:49:08
 */
public class BindingsUtils {

    public static Bindings asBindings(final BeanSource beanSource) {

        return new NameBindings() {

            @Override
            protected Set<String> getBeanNames() {
                return beanSource.names();
            }

            @Override
            public boolean containsBean(String name) {
                return beanSource.exist(name);
            }

            @Override
            protected Object getBean(String name) {
                return beanSource.get(name);
            }

            @Override
            protected void setBean(String name, Object value) {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static Bindings asBindings(final BeanContainer beanContainer) {

        return new NameBindings() {

            @Override
            protected Set<String> getBeanNames() {
                return beanContainer.names();
            }

            @Override
            public boolean containsBean(String name) {
                return beanContainer.exist(name);
            }

            @Override
            protected Object getBean(String name) {
                return beanContainer.get(name);
            }

            @Override
            protected void setBean(String name, Object value) {
                beanContainer.set(name, value);
            }
        };
    }

    public static Bindings asBindings(final Scope scope) {

        return new NameBindings() {

            @Override
            public Set<String> getBeanNames() {
                Set<String> names = new LinkedHashSet<String>();
                names.addAll(scope.getContantNames());
                names.addAll(scope.getAttributeNames());
                return names;
            }

            @Override
            public boolean containsBean(String name) {
                return scope.containsConstant(name) || scope.containsAttribute(name);
            }

            @Override
            protected Object getBean(String name) {
                return scope.containsConstant(name) ? scope.getConstant(name)
                        : scope.containsAttribute(name) ? scope.getAttribute(name)
                        : null;
            }

            @Override
            protected void setBean(String name, Object value) {
                scope.setAttribute(name, value);
            }
        };
    }

    protected static abstract class NameBindings implements Bindings {

        protected abstract Set<String> getBeanNames();

        protected abstract boolean containsBean(String name);

        protected abstract Object getBean(String name);

        protected abstract void setBean(String name, Object value);

        @Override
        public boolean containsKey(Object key) {
            return containsBean(key.toString());
        }

        @Override
        public Object get(Object key) {
            return getBean(key.toString());
        }

        @Override
        public Object put(String name, Object value) {
            setBean(name, value);
            return getBean(name);
        }

        @Override
        public void putAll(Map<? extends String, ?> toMerge) {
            for (Map.Entry<? extends String, ?> e : toMerge.entrySet()) {
                setBean(e.getKey(), e.getValue());
            }
        }

        @Override
        public int size() {
            return getBeanNames().size();
        }

        @Override
        public boolean isEmpty() {
            return getBeanNames().isEmpty();
        }

        @Override
        public Set<String> keySet() {
            return getBeanNames();
        }

        @Override
        public boolean containsValue(Object value) {
            return getBeanMap().containsValue(value);
        }

        @Override
        public Collection<Object> values() {
            return getBeanMap().values();
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            return getBeanMap().entrySet();
        }

        protected Map<String, Object> getBeanMap() {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            for (String name : getBeanNames()) {
                map.put(name, getBean(name));
            }
            return map;
        }

        // unsupported

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }
    }
}
