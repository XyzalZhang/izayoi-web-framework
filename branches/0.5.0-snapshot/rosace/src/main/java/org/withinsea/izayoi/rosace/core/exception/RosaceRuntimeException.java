package org.withinsea.izayoi.rosace.core.exception;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 12:49:18
 */
public class RosaceRuntimeException extends RuntimeException {

    public RosaceRuntimeException() {
    }

    public RosaceRuntimeException(Throwable cause) {
        super(cause);
    }

    public RosaceRuntimeException(String message) {
        super(message);
    }

    public RosaceRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}