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

package org.withinsea.izayoi.core.bindings;

import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-9
 * Time: 5:49:42
 */
public abstract class BindingsManagerImpl implements BindingsManager {

    protected static final String BINDINGS_MAP_ATTR = BindingsManagerImpl.class.getCanonicalName() + ".BINDINGS_MAP";

    protected abstract Object getBean(HttpServletRequest request, String name);

    @Override
    public Bindings getBindings(HttpServletRequest request) {

        @SuppressWarnings("unchecked")
        Map<BindingsManager, Bindings> map = (Map<BindingsManager, Bindings>) request.getAttribute(BINDINGS_MAP_ATTR);
        if (map == null) {
            map = new LinkedHashMap<BindingsManager, Bindings>();
            request.setAttribute(BINDINGS_MAP_ATTR, map);
        }

        Bindings bindings = map.get(this);
        if (bindings == null) {
            bindings = new NameBindings(request);
            map.put(this, bindings);
        }

        return bindings;
    }

    protected class NameBindings implements Bindings {

        protected final HttpServletRequest request;

        public NameBindings(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public Object get(Object key) {
            return getBean(request, key.toString());
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