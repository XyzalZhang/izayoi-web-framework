package org.withinsea.izayoi.core.scope.custom;

import org.withinsea.izayoi.core.scope.context.ContextScope;

import javax.servlet.http.HttpSession;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:36:07
 */
public class Session extends Application {

    protected final HttpSession session;

    public Session(ContextScope contextScope, HttpSession session) {
        super(contextScope, session.getServletContext());
        this.session = session;
    }

    @Override
    public <T> void setBean(String name, T object) {
        session.setAttribute(name, object);
    }

    @Override
    protected Object lookupConstant(String name) {
        return name.equals("session") ? session
                : super.lookupConstant(name);
    }

    @Override
    protected Object lookupAttribute(String name) {
        Object obj = session.getAttribute(name);
        if (obj == null) obj = super.lookupAttribute(name);
        return obj;
    }

    public HttpSession getSession() {
        return session;
    }
}