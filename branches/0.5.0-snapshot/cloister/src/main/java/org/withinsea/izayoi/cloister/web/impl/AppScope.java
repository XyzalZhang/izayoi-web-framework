package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.impl.scope.ScopeImpl;
import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.common.servlet.ServletContextAttributesMap;
import org.withinsea.izayoi.common.util.Vars;

import javax.servlet.ServletContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 下午12:58
 */
public class AppScope extends ScopeImpl {

    protected final ServletContext servletContext;

    public AppScope(Scope globalScope, ServletContext servletContext) {
        super(globalScope,
                new ServletContextAttributesMap(servletContext),
                new Vars(
                        "servletContext", servletContext,
                        "application", servletContext
                ));
        this.servletContext = servletContext;
    }

    @Override
    public final String getName() {
        return "application";
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
