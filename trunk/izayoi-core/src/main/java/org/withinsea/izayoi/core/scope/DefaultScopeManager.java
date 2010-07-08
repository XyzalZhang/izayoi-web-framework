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

package org.withinsea.izayoi.core.scope;

import org.withinsea.izayoi.commons.util.Varstack;

import javax.script.Bindings;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-30
 * Time: 23:46:57
 */
public class DefaultScopeManager implements ScopeManager {

    protected Scope globalScope;

    @Override
    public Varstack createVarstack(Scope scope) {
        Varstack varstack = new Varstack();
        {
            varstack.push(new NameBindings(globalScope));
            varstack.push(new NameBindings(scope));
        }
        return varstack;
    }

    public void setGlobalScope(Scope globalScope) {
        this.globalScope = globalScope;
    }

    protected static class NameBindings implements Bindings {

        protected final Scope scope;

        public NameBindings(Scope scope) {
            this.scope = scope;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public Object get(Object key) {
            return scope.getBean(key.toString());
        }

        // unsupported

        @Override
        public Object put(String name, Object value) {
            scope.setBean(name, value);
            return scope.getBean(name);
        }

        @Override
        public void putAll(Map<? extends String, ?> toMerge) {
            for (Map.Entry<? extends String, ?> e : toMerge.entrySet()) {
                scope.setBean(e.getKey(), e.getValue());
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
        public Set<Entry<String, Object>> entrySet() {
            throw new UnsupportedOperationException();
        }
    }

}
