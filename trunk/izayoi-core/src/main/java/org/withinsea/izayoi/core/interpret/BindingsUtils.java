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
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-21
 * Time: 11:49:08
 */
public class BindingsUtils {

    public static Bindings asBindings(final BeanSource beanSource) {

        return new NameBindings() {

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
            protected Object getBean(String name) {
                Object bean = scope.getConstant(name);
                if (bean == null) bean = scope.getAttribute(name);
                return bean;
            }

            @Override
            protected void setBean(String name, Object value) {
                scope.setScopeAttribute(name, value);
            }
        };
    }

    protected static abstract class NameBindings implements Bindings {

        protected abstract Object getBean(String name);

        protected abstract void setBean(String name, Object value);

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public Object get(Object key) {
            return getBean(key.toString());
        }

        // unsupported

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
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Object> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }
}
