package org.withinsea.izayoi.cloister.core.impl.role;

import org.withinsea.izayoi.cloister.core.feature.postscript.RoleEngine;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-3
 * Time: 上午10:10
 */
public class Context implements RoleEngine<Map<String, Object>> {

    @Override
    public void process(Map<String, Object> data, Map<String, Object> context) {
        if (data != null) {
            context.putAll(data);
        }
    }
}
