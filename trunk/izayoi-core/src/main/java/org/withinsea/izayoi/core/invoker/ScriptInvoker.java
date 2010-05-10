package org.withinsea.izayoi.core.invoker;

import org.withinsea.izayoi.commons.util.Varstack;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.core.scope.ScopeUtils;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:36:26
 */
public abstract class ScriptInvoker<S extends Scope> implements Invoker<S> {

    protected CodeManager codeManager;
    protected InterpretManager interpretManager;

    @Override
    public boolean invoke(String codePath, S scope) throws IzayoiException {

        if (!codeManager.exist(codePath)) {
            throw new IzayoiException("code " + codePath + " does not exist.");
        }

        Object result = interpretManager.interpret(codeManager.get(codePath), getBinding(scope));

        return processResult(result, codePath, scope);
    }

    protected Varstack getBinding(Scope scope) {
        Varstack bindings = new Varstack();
        {
            bindings.push(ScopeUtils.getBindings(scope));
            bindings.push();
        }
        return bindings;
    }

    protected abstract boolean processResult(Object result, String codePath, S scope) throws IzayoiException;

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setInterpretManager(InterpretManager interpretManager) {
        this.interpretManager = interpretManager;
    }
}
