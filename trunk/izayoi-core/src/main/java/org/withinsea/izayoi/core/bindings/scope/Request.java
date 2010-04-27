package org.withinsea.izayoi.core.bindings.scope;

import org.withinsea.izayoi.commons.servlet.HttpParameterMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-22
 * Time: 12:25:33
 */
public class Request extends Session {

    protected static final String PARAMS_ATTR = Request.class.getCanonicalName() + ".PARAMS";

    @Override
    public <T> void setBean(HttpServletRequest request, HttpServletResponse response, String name, T object) {
        request.setAttribute(name, object);
    }

    @Override
    protected Object lookupConstant(HttpServletRequest request, HttpServletResponse response, String name) {
        return name.equals("params") ? getParameterMap(request)
                : name.equals("request") ? request
                : name.equals("response") ? response
                : super.lookupConstant(request, response, name);
    }

    @Override
    protected Object lookupAttribute(HttpServletRequest request, HttpServletResponse response, String name) {
        Object obj = request.getAttribute(name);
        if (obj == null) obj = request.getParameter(name);
        if (obj == null) obj = super.lookupAttribute(request, response, name);
        return obj;
    }

    protected HttpParameterMap getParameterMap(HttpServletRequest request) {
        HttpParameterMap params = (HttpParameterMap) request.getAttribute(PARAMS_ATTR);
        if (params == null) {
            params = new HttpParameterMap(request);
            request.setAttribute(PARAMS_ATTR, params);
        }
        return params;
    }
}