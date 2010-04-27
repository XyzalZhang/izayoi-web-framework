package org.withinsea.izayoi.core.bindings.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-22
 * Time: 12:25:33
 */
public class Session extends Application {

    @Override
    public <T> void setBean(HttpServletRequest request, HttpServletResponse response, String name, T object) {
        request.getSession().setAttribute(name, object);
    }

    @Override
    protected Object lookupConstant(HttpServletRequest request, HttpServletResponse response, String name) {
        return name.equals("session") ? request.getSession()
                : super.lookupConstant(request, response, name);
    }

    @Override
    protected Object lookupAttribute(HttpServletRequest request, HttpServletResponse response, String name) {
        Object obj = request.getSession().getAttribute(name);
        if (obj == null) obj = super.lookupAttribute(request, response, name);
        return obj;
    }
}