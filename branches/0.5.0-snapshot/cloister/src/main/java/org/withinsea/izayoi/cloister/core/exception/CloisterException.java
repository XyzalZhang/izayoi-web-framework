package org.withinsea.izayoi.cloister.core.exception;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 12:49:18
 */
public class CloisterException extends Exception {

    public CloisterException() {
    }

    public CloisterException(Throwable cause) {
        super(cause);
    }

    public CloisterException(String message) {
        super(message);
    }

    public CloisterException(String message, Throwable cause) {
        super(message, cause);
    }
}