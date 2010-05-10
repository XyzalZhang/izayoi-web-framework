package org.withinsea.izayoi.glowworm.core.decorator;

import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.ScriptInvoker;
import org.withinsea.izayoi.core.scope.Scope;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:54:20
 */
public class Data extends ScriptInvoker<Scope> {

    @Override
    @SuppressWarnings("unchecked")
    protected boolean processResult(Object result, String codePath, Scope scope) throws IzayoiException {

        if (result instanceof Map) {
            for (Map.Entry<String, ?> e : ((Map<String, Object>) result).entrySet()) {
                scope.setBean(e.getKey(), e.getValue());
            }
        }

        return true;
    }
}