package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.impl.request.RequestImpl;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.Scope;

import javax.servlet.ServletContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午5:33
 */
public class AppRequest extends RequestImpl<AppScope> {

    protected ServletContext servletContext;

    public AppRequest(Environment environment, Scope globalScope, ServletContext servletContext) {
        super(environment, new AppScope(globalScope, servletContext));
        this.servletContext = servletContext;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }
}
