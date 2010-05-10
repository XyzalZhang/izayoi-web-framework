package org.withinsea.izayoi.core.scope.custom;

import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.core.scope.context.ContextScope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:13:17
 */
public abstract class CustomizedScope implements Scope {

    protected final ContextScope contextScope;

    protected CustomizedScope(ContextScope contextScope) {
        this.contextScope = contextScope;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        Object obj = lookupConstant(name);
        if (obj == null) obj = lookupAttribute(name);
        if (obj == null) obj = contextScope.getBean(name);
        return (T) obj;
    }

    protected Object lookupConstant(String name) {
        return null;
    }

    protected Object lookupAttribute(String name) {
        return null;
    }

    public Scope getContextScope() {
        return contextScope;
    }
}
