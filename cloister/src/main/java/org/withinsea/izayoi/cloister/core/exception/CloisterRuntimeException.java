package org.withinsea.izayoi.cloister.core.exception;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 12:49:18
 */
public class CloisterRuntimeException extends RuntimeException {

    public CloisterRuntimeException() {
    }

    public CloisterRuntimeException(Throwable cause) {
        super(cause);
    }

    public CloisterRuntimeException(String message) {
        super(message);
    }

    public CloisterRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}