package org.withinsea.izayoi.core.context;

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
