package org.withinsea.izayoi.cloister.core.impl.request;

import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.Request;
import org.withinsea.izayoi.cloister.core.kernal.Scope;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午3:13
 */
public class RequestImpl<SCOPE extends Scope> implements Request {

    protected Environment environment;
    protected SCOPE scope;
    protected String method;
    protected Map<String, Object> parameters;
    protected String inputContentType;
    protected String inputEncoding;
    protected InputStream inputStream;
    protected String outputContentType;
    protected String outputEncoding;
    protected OutputStream outputStream;

    public RequestImpl(Environment environment, SCOPE scope) {
        this(environment, scope, null, Collections.<String, Object>emptyMap());
    }

    public RequestImpl(Environment environment, SCOPE scope, String method, Map<String, Object> parameters) {
        this(environment, scope, method, parameters, null, null, null, null, null, null);
    }

    public RequestImpl(Environment environment, SCOPE scope, String method, Map<String, Object> parameters,
                       String inputContentType, String inputEncoding, InputStream inputStream,
                       String outputContentType, String outputEncoding, OutputStream outputStream) {
        this.environment = environment;
        this.scope = scope;
        this.method = method;
        this.parameters = parameters;
        this.inputContentType = inputContentType;
        this.inputEncoding = inputEncoding;
        this.inputStream = inputStream;
        this.outputContentType = outputContentType;
        this.outputEncoding = outputEncoding;
        this.outputStream = outputStream;
    }

    @Override
    public Environment getEnvironment() {
        return environment;
    }

    @Override
    public SCOPE getScope() {
        return scope;
    }

    @Override
    public String getPath() {
        return "/";
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public String getInputEncoding() {
        return inputEncoding;
    }

    @Override
    public String getInputContentType() {
        return inputContentType;
    }

    @Override
    public InputStream getInputStream() {
        if (inputStream == null) {
            throw new UnsupportedOperationException();
        } else {
            return inputStream;
        }
    }

    @Override
    public void setOutputEncoding(String encoding) {
        this.outputEncoding = encoding;
    }

    @Override
    public void setOutputContentType(String contentType) {
        this.outputContentType = contentType;
    }

    @Override
    public OutputStream getOutputStream() {
        if (outputStream == null) {
            throw new UnsupportedOperationException();
        } else {
            return outputStream;
        }
    }
}
