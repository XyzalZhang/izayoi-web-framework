package org.withinsea.izayoi.cloister.core.kernal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
* Created by Mo Chen <withinsea@gmail.com>
* Date: 10-12-13
* Time: 上午2:38
*/
public interface Request {

    Environment getEnvironment();

    Scope getScope();

    String getPath();

    String getMethod();

    Map<String, Object> getParameters();

    String getInputContentType();

    String getInputEncoding();

    InputStream getInputStream() throws IOException;

    void setOutputContentType(String contentType);

    void setOutputEncoding(String encoding);

    OutputStream getOutputStream() throws IOException;
}
