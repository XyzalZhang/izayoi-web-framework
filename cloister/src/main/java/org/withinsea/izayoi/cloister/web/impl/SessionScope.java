package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.impl.scope.ScopeImpl;
import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.common.servlet.HttpSessionAttributesMap;
import org.withinsea.izayoi.common.util.Vars;

import javax.servlet.http.HttpSession;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-27
 * Time: 下午4:07
 */
public class SessionScope extends ScopeImpl {

    protected final HttpSession httpSession;

    public SessionScope(Scope globalScope, HttpSession httpSession) {
        super(new AppScope(globalScope, httpSession.getServletContext()),
                new HttpSessionAttributesMap(httpSession),
                new Vars(
                        "servletContext", httpSession.getServletContext(),
                        "application", httpSession.getServletContext(),
                        "session", httpSession
                ));
        this.httpSession = httpSession;
    }

    @Override
    public final String getName() {
        return "session";
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }
}
