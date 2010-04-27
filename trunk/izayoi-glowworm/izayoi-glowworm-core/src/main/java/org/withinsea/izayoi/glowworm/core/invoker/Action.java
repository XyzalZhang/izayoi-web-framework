package org.withinsea.izayoi.glowworm.core.invoker;

import org.withinsea.izayoi.core.bindings.scope.Scope;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:54:20
 */
public class Action extends InterpretInvoker {

    protected Data data;
    protected Dispatcher dispatcher;

    @Override
    protected boolean processResult(HttpServletRequest request, HttpServletResponse response, Object result, Scope scope) throws GlowwormException {
        if (result != null) {
            if (result.getClass().isArray()) {
                for (Object item : (Object[]) result) {
                    if (!processResult(request, response, item, scope)) {
                        return false;
                    }
                }
                return true;
            } else if (result instanceof Iterable) {
                for (Object item : (Iterable) result) {
                    if (!processResult(request, response, item, scope)) {
                        return false;
                    }
                }
                return true;
            } else if (result instanceof String) {
                return dispatcher.processResult(request, response, result, scope);
            } else {
                return data.processResult(request, response, result, scope);
            }
        } else {
            return true;
        }
    }

    public void setData(Data data) {
        this.data = data;
    }

    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
}