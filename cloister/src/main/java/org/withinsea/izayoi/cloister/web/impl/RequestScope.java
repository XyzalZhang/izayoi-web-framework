package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.impl.scope.ScopeImpl;
import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.common.servlet.HttpParameterMap;
import org.withinsea.izayoi.common.servlet.HttpServletRequestAttributesMap;
import org.withinsea.izayoi.common.util.Vars;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-27
 * Time: 下午4:09
 */
public class RequestScope extends ScopeImpl {

    protected final HttpServletRequest httpServletRequest;
    protected final HttpServletResponse httpServletResponse;
    protected final FilterChain filterChain;
    protected final HttpParameterMap parameterMap;

    public RequestScope(Scope globalScope,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse) {
        this(globalScope, httpServletRequest, httpServletResponse, null);
    }

    public RequestScope(Scope globalScope,
                        HttpServletRequest httpServletRequest,
                        HttpServletResponse httpServletResponse,
                        FilterChain filterChain) {
        super(new SessionScope(globalScope, httpServletRequest.getSession()),
                new HttpServletRequestAttributesMap(httpServletRequest),
                new Vars(
                        "servletContext", httpServletRequest.getSession().getServletContext(),
                        "application", httpServletRequest.getSession().getServletContext(),
                        "session", httpServletRequest.getSession(),
                        "request", httpServletRequest,
                        "response", httpServletResponse,
                        "chain", filterChain,
                        "params", new HttpParameterMap(httpServletRequest)
                ));
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.filterChain = filterChain;
        this.parameterMap = (HttpParameterMap) getAttributes().get("params");
    }

    @Override
    public final String getName() {
        return "request";
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public HttpParameterMap getParameterMap() {
        return parameterMap;
    }
}
