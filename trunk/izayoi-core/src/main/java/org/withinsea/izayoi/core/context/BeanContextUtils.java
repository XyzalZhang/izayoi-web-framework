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

package org.withinsea.izayoi.core.context;

import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:02:56
 */
public class BeanContextUtils {

    public static final String REQUEST_CONTEXT_ATTR = BeanContextUtils.class.getCanonicalName() + ".REQUEST_CONTEXT";

    public static void setBeanContext(HttpServletRequest request, BeanContext context) {
        request.setAttribute(REQUEST_CONTEXT_ATTR, context);
    }

    public static BeanContext getBeanContext(HttpServletRequest request) {
        return (BeanContext) request.getAttribute(REQUEST_CONTEXT_ATTR);
    }

    public static Bindings getBindings(BeanContext beanContext) {
        return new NameBindings(beanContext);
    }

    protected static class NameBindings implements Bindings {

        protected final BeanContext beanContext;

        public NameBindings(BeanContext beanContext) {
            this.beanContext = beanContext;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public Object get(Object key) {
            return beanContext.getBean(key.toString());
        }

        // unsupported

        @Override
        public Object put(String name, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends String, ?> toMerge) {
            throw new UnsupportedOperationException();
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
        public Set<Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }
}