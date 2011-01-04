package org.withinsea.izayoi.cloister.web.impl;

import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.Request;
import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.cloister.web.kernal.RequestAware;
import org.withinsea.izayoi.common.servlet.ServletFilterUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午5:33
 */
public class RequestRequest implements Request, RequestAware {

    protected Environment environment;
    protected RequestScope requestScope;
    protected HttpServletRequest httpServletRequest;
    protected HttpServletResponse httpServletResponse;
    protected FilterChain filterChain;

    public RequestRequest(Environment environment, Scope globalScope,
                          HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) {
        this.environment = environment;
        this.requestScope = new RequestScope(globalScope, httpServletRequest, httpServletResponse, filterChain);
        this.httpServletRequest = httpServletRequest;
        this.httpServletResponse = httpServletResponse;
        this.filterChain = filterChain;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public RequestScope getScope() {
        return requestScope;
    }

    @Override
    public String getPath() {
        String path = ServletFilterUtils.getRequestPath(httpServletRequest);
        return path.equals("") ? "/" : path;
    }

    @Override
    public String getMethod() {
        return httpServletRequest.getMethod().toLowerCase();
    }

    @Override
    public Map<String, Object> getParameters() {
        return requestScope.getParameterMap();
    }

    @Override
    public String getInputEncoding() {
        return httpServletRequest.getCharacterEncoding();
    }

    @Override
    public String getInputContentType() {
        return httpServletRequest.getContentType();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return httpServletRequest.getInputStream();
    }

    @Override
    public void setOutputEncoding(String encoding) {
        httpServletResponse.setCharacterEncoding(encoding);
    }

    @Override
    public void setOutputContentType(String contentType) {
        httpServletResponse.setContentType(contentType);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return httpServletResponse.getOutputStream();
    }

    public ServletContext getServletContext() {
        return httpServletRequest.getSession().getServletContext();
    }

    public HttpSession getSession() {
        return httpServletRequest.getSession();
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public FilterChain getFilterChain() {
        return filterChain;
    }
}
