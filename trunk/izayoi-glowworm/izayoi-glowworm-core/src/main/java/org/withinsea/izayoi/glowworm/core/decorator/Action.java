package org.withinsea.izayoi.glowworm.core.decorator;

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.core.invoker.ScriptInvoker;
import org.withinsea.izayoi.core.scope.custom.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:54:20
 */
public class Action extends ScriptInvoker<Request> {

    protected Data data = new Data();
    protected Dispatcher dispatcher = new Dispatcher();

    @Override
    protected boolean processResult(Object result, String codePath, Request scope) throws IzayoiException {

        HttpServletRequest request = scope.getRequest();
        HttpServletResponse response = scope.getResponse();

        if (result != null) {
            if (result.getClass().isArray()) {
                for (Object item : (Object[]) result) {
                    if (!processResult(item, codePath, scope)) {
                        return false;
                    }
                }
                return true;
            } else if (result instanceof Iterable) {
                for (Object item : (Iterable) result) {
                    if (!processResult(item, codePath, scope)) {
                        return false;
                    }
                }
                return true;
            } else if (result instanceof String) {
                return dispatcher.processResult(result, codePath, scope);
            } else {
                return data.processResult(result, codePath, scope);
            }
        } else {
            return true;
        }
    }

    @Override
    public void setCodeManager(CodeManager codeManager) {
        super.setCodeManager(codeManager);
        data.setCodeManager(codeManager);
        dispatcher.setCodeManager(codeManager);
    }

    @Override
    public void setInterpretManager(InterpretManager interpretManager) {
        super.setInterpretManager(interpretManager);
        data.setInterpretManager(interpretManager);
        dispatcher.setInterpretManager(interpretManager);
    }
}