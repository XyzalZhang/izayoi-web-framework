package org.withinsea.izayoi.rosace.core.exception;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 12:49:18
 */
public class RosaceException extends Exception {

    public RosaceException() {
    }

    public RosaceException(Throwable cause) {
        super(cause);
    }

    public RosaceException(String message) {
        super(message);
    }

    public RosaceException(String message, Throwable cause) {
        super(message, cause);
    }
}