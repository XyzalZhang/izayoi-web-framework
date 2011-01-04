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

package org.withinsea.izayoi.rosace.core.impl.grammar.ns.c;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-4
 * Time: 19:41:58
 */
public class With implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("with");
    }

    @Override
    public void processAttr(Attribute attr) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();

        Element elem = attr.getParent();
        String attrvalue = attr.getValue();

        String value = attrvalue.trim();
        String el = value.startsWith("${") ? value.substring(2, value.length() - 1).trim() : value;
        String beanMap = "new " + BeanMap.class.getCanonicalName() + "(" + engine.precompileEl(el) + ")";

        String preScriptlet = engine.precompileOpenScope(beanMap) + engine.precompileOpenScope();
        String sufScriptlet = engine.precompileCloseScope() + engine.precompileCloseScope();

        try {
            DomUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new RosaceException(e);
        }

        attr.detach();
    }

    public static class BeanMap implements Map<String, Object> {

        private final Object bean;
        private final Map<String, Method> gets = new HashMap<String, Method>();
        private final Map<String, Method> sets = new HashMap<String, Method>();

        public BeanMap(Object bean) {
            this.bean = bean;
            for (Method m : bean.getClass().getDeclaredMethods()) {
                String name = m.getName();
                m.setAccessible(true);
                if ((name.startsWith("get") || name.startsWith("is")) && m.getParameterTypes().length == 0) {
                    gets.put(incapitalize(name.startsWith("get") ? name.substring(3) : name.substring(2)), m);
                } else if (name.startsWith("sets") && m.getParameterTypes().length == 1) {
                    sets.put(incapitalize(name.substring(3)), m);
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
            return gets.containsKey(key);
        }

        public boolean containsValue(Object value) {
            return values().contains(value);
        }

        public Object get(Object key) {
            if (gets.containsKey(key.toString())) {
                try {
                    return gets.get(key).invoke(bean);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        }

        public Object put(String key, Object value) {
            if (sets.containsKey(key)) {
                try {
                    sets.get(key).invoke(bean, value);
                    return value;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    throw new NoSuchMethodException(bean.getClass().getCanonicalName() + ".set" + capitalize(key));
                } catch (NoSuchMethodException ex) {
                    throw new RuntimeException("no property named " + key + " in " + bean.toString(), ex);
                }
            }
        }

        public Object remove(Object key) {
            throw new UnsupportedOperationException("bean property can't be removed in " + bean.toString());
        }

        public void putAll(Map<? extends String, ?> m) {
            for (Map.Entry<? extends String, ?> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }

        public void clear() {
            throw new UnsupportedOperationException("bean properties can't be cleared in " + bean.toString());
        }

        public Set<String> keySet() {
            return gets.keySet();
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

        protected static String capitalize(String name) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        protected static String incapitalize(String name) {
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
    }
}
