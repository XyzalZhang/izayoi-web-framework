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

package org.withinsea.izayoi.core.dependency;

import org.withinsea.izayoi.commons.util.Varstack;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-3
 * Time: 20:24:57
 */
public abstract class DependencyManagerImpl implements DependencyManager {

    protected static final String VARSTACK_ATTR = DependencyManagerImpl.class.getCanonicalName() + ".VARSTACK";

    protected abstract Object getBean(HttpServletRequest request, String name);

    @Override
    public synchronized Varstack getDependency(HttpServletRequest request) {
        Varstack varstack = (Varstack) request.getAttribute(VARSTACK_ATTR);
        if (varstack == null) {
            varstack = new Varstack();
            varstack.push(new DependencyMap(request));
            varstack.push();
            request.setAttribute(VARSTACK_ATTR, varstack);
        }
        return varstack;
    }

    protected class DependencyMap implements Map<String, Object> {

        protected HttpServletRequest request;

        public DependencyMap(HttpServletRequest request) {
            this.request = request;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        // mock methods

        @Override
        public Object put(String name, Object value) {
            return null;
        }

        @Override
        public void putAll(Map<? extends String, ?> toMerge) {

        }

        @Override
        public Object get(Object key) {
            return getBean(request, key.toString());
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Set<String> keySet() {
            return null;
        }

        @Override
        public Collection<Object> values() {
            return null;
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }
    }
}
