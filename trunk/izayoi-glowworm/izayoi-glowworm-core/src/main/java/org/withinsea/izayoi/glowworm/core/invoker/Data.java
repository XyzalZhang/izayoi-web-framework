package org.withinsea.izayoi.glowworm.core.invoker;

import org.withinsea.izayoi.core.bindings.scope.Scope;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:54:20
 */
public class Data extends InterpretInvoker {

    @Override
    @SuppressWarnings("unchecked")
    protected boolean processResult(HttpServletRequest request, HttpServletResponse response, Object result, Scope scope) throws GlowwormException {
        if (result instanceof Map) {
            for (Map.Entry<String, ?> e : ((Map<String, Object>) result).entrySet()) {
                scope.setBean(request, response, e.getKey(), e.getValue());
            }
        }
        return true;
    }
}