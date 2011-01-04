package org.withinsea.izayoi.cloister.core.impl.request;

import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.Request;
import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.common.util.Varstack;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-29
 * Time: 上午9:28
 */
public class RequestWrapper implements Request {

    protected Request wrappedRequest;
    protected Map<String, Object> parameters;

    public RequestWrapper(Request wrappedRequest) {
        this(wrappedRequest, null);
    }

    public RequestWrapper(Request wrappedRequest, Map<String, ?> overriddenParameters) {
        this.wrappedRequest = wrappedRequest;
        if (overriddenParameters == null) {
            this.parameters = wrappedRequest.getParameters();
        } else {
            Varstack varstack = new Varstack();
            varstack.push(wrappedRequest.getParameters());
            varstack.push(overriddenParameters);
            this.parameters = varstack;
        }
    }

    @Override
    public Environment getEnvironment() {
        return wrappedRequest.getEnvironment();
    }

    @Override
    public String getInputEncoding() {
        return wrappedRequest.getInputEncoding();
    }

    @Override
    public String getInputContentType() {
        return wrappedRequest.getInputContentType();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return wrappedRequest.getInputStream();
    }

    @Override
    public String getMethod() {
        return wrappedRequest.getMethod();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return wrappedRequest.getOutputStream();
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String getPath() {
        return wrappedRequest.getPath();
    }

    @Override
    public Scope getScope() {
        return wrappedRequest.getScope();
    }

    @Override
    public void setOutputEncoding(String encoding) {
        wrappedRequest.setOutputEncoding(encoding);
    }

    @Override
    public void setOutputContentType(String contentType) {
        wrappedRequest.setOutputContentType(contentType);
    }
}
