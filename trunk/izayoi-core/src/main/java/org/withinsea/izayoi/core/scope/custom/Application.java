package org.withinsea.izayoi.core.scope.custom;

import org.withinsea.izayoi.core.scope.context.ContextScope;

import javax.servlet.ServletContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:36:07
 */
public class Application extends Singleton {

    protected final ServletContext servletContext;

    public Application(ContextScope contextScope, ServletContext servletContext) {
        super(contextScope);
        this.servletContext = servletContext;
    }

    @Override
    public <T> void setBean(String name, T object) {
        servletContext.setAttribute(name, object);
    }

    @Override
    protected Object lookupConstant(String name) {
        return name.equals("application") ? servletContext
                : name.equals("servletContext") ? servletContext
                : super.lookupConstant(name);
    }

    @Override
    protected Object lookupAttribute(String name) {
        Object obj = servletContext.getAttribute(name);
        if (obj == null) obj = super.lookupAttribute(name);
        return obj;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
