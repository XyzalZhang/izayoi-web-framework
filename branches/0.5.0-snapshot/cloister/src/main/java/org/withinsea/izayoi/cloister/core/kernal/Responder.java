package org.withinsea.izayoi.cloister.core.kernal;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-13
 * Time: 上午8:32
 */
public interface Responder {

    void respond(Request request) throws CloisterException;
}
