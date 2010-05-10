package org.withinsea.izayoi.core.scope.custom;

import org.withinsea.izayoi.commons.servlet.HttpParameterMap;
import org.withinsea.izayoi.core.scope.context.ContextScope;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:36:07
 */
public class Request extends Session {

    protected static final String PARAMS_ATTR = Request.class.getCanonicalName() + ".PARAMS";

    protected final HttpServletRequest request;
    protected final HttpServletResponse response;
    protected final FilterChain chain;

    public Request(ContextScope contextScope, HttpServletRequest request, HttpServletResponse response) {
        this(contextScope, request, response, null);
    }

    public Request(ContextScope contextScope, HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        super(contextScope, request.getSession());
        this.request = request;
        this.response = response;
        this.chain = chain;
    }

    @Override
    public <T> void setBean(String name, T object) {
        request.setAttribute(name, object);
    }

    @Override
    protected Object lookupConstant(String name) {
        return name.equals("params") ? getParameterMap(request)
                : name.equals("request") ? request
                : name.equals("response") ? response
                : name.equals("chain") ? chain
                : super.lookupConstant(name);
    }

    @Override
    protected Object lookupAttribute(String name) {
        Object obj = request.getAttribute(name);
        if (obj == null) obj = request.getParameter(name);
        if (obj == null) obj = super.lookupAttribute(name);
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

    public FilterChain getChain() {
        return chain;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}