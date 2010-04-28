package org.withinsea.izayoi.glowworm.core.invoker;

import org.withinsea.izayoi.core.bindings.BindingsManager;
import org.withinsea.izayoi.core.bindings.Varstack;
import org.withinsea.izayoi.core.bindings.scope.Scope;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-22
 * Time: 10:44:05
 */
public abstract class InterpretInvoker implements Invoker {

    protected BindingsManager bindingsManager;
    protected InterpretManager interpretManager;

    protected abstract boolean processResult(HttpServletRequest request, HttpServletResponse response, Object result, Scope scope) throws GlowwormException;

    @Override
    public boolean process(HttpServletRequest request, HttpServletResponse response, Code code, String asType, Scope scope) throws GlowwormException {

        Varstack bindings = new Varstack();
        {
            bindings.push(bindingsManager.getBindings(request, response, scope));
            bindings.push();
        }

        Object result;
        try {
            result = interpretManager.interpret(code, bindings);
        } catch (IzayoiException e) {
            throw new GlowwormException(e.getMessage(), e.getCause());
        }

        return processResult(request, response, result, scope);
    }

    public void setBindingsManager(BindingsManager bindingsManager) {
        this.bindingsManager = bindingsManager;
    }

    public void setInterpretManager(InterpretManager interpretManager) {
        this.interpretManager = interpretManager;
    }
}
