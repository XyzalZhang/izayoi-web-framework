package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.impl.request.RequestImpl;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.Scope;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午5:33
 */
public class SessionRequest extends RequestImpl<SessionScope> {

    protected HttpSession httpSession;

    public SessionRequest(Environment environment, Scope globalScope, HttpSession httpSession) {
        super(environment, new SessionScope(globalScope, httpSession));
        this.httpSession = httpSession;
    }

    public ServletContext getServletContext() {
        return httpSession.getServletContext();
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }
}
