package org.withinsea.izayoi.core.bindings.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-22
 * Time: 16:09:39
 */
public abstract class AttributeScope implements Scope {

    protected Scope contextScope;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(HttpServletRequest request, HttpServletResponse response, String name) {
        Object obj = lookupConstant(request, response, name);
        if (obj == null) obj = lookupAttribute(request, response, name);
        if (obj == null) obj = contextScope.getBean(request, response, name);
        return (T) obj;
    }

    protected Object lookupConstant(HttpServletRequest request, HttpServletResponse response, String name) {
        return null;
    }

    protected Object lookupAttribute(HttpServletRequest request, HttpServletResponse response, String name) {
        return null;
    }

    public void setContextScope(Scope contextScope) {
        this.contextScope = contextScope;
    }
}