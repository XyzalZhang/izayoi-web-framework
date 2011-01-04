package org.withinsea.izayoi.cloister.core.kernal;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-13
 * Time: 下午12:16
 */
public interface Scope {

    Scope getParentScope();

    String getName();

    Map<String, Object> getAttributes();
}
