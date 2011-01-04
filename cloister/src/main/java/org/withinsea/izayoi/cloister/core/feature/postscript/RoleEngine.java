package org.withinsea.izayoi.cloister.core.feature.postscript;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-13
 * Time: 下午3:17
 */
public interface RoleEngine<T> {

    void process(T data, Map<String, Object> context) throws CloisterException;
}
