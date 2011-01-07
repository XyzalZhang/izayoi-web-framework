package org.withinsea.izayoi.cloister.core.impl.scope;

import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.common.util.Vars;
import org.withinsea.izayoi.common.util.Varstack;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 下午12:05
 */
public class ScopeImpl implements Scope {

    protected Scope parentScope;
    protected Varstack attributes;
    protected Map<String, Object> scopeAttributes;

    public ScopeImpl() {
        this(null);
    }

    public ScopeImpl(Scope parentScope) {
        this(parentScope, new Vars(), new Vars());
    }

    public ScopeImpl(Scope parentScope, Map<String, Object> scopeAttributes, Map<String, Object> scopeConstants) {
        this.parentScope = parentScope;
        this.attributes = new Varstack(scopeConstants);
        {
            if (parentScope != null) attributes.push(parentScope.getAttributes());
            attributes.push(scopeAttributes);
        }
        this.scopeAttributes = scopeAttributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Map<String, Object> getScopeAttributes() {
        return scopeAttributes;
    }

    @Override
    public Scope getParentScope() {
        return parentScope;
    }

    @Override
    public String getName() {
        return "";
    }
}
