/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF
 *
 * ANY KIND, either express or implied. See the License for the specific language governing rights and
 *
 * limitations under the License.
 *
 * The Original Code is the IZAYOI web framework.
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.glowworm.core.invoke;

import org.withinsea.izayoi.commons.servlet.ByteArrayBufferedHttpServletResponseWrapper;
import org.withinsea.izayoi.commons.util.Varstack;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.context.Scope;
import org.withinsea.izayoi.core.context.ScopeManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:36:26
 */
public abstract class ResultInvoker<S extends Scope> implements Invoker<S> {

    protected CodeManager codeManager;
    protected ScopeManager scopeManager;
    protected InterpretManager interpretManager;

    @Override
    public boolean invoke(String codePath, S scope) throws GlowwormException {

        Varstack bindings = scopeManager.createVarstack(scope);
        {
            HttpServletResponse response = (HttpServletResponse) bindings.get("response");
            if (response != null) {
                bindings.put("response", new ByteArrayBufferedHttpServletResponseWrapper(response));
                bindings.push();
            }
        }

        try {
            Object result = interpretManager.interpret(codeManager.get(codePath), bindings);
            return !acceptResult(result) || processResult(result, codePath, scope);
        } catch (IzayoiException e) {
            throw new GlowwormException(e);
        }
    }

    protected abstract boolean acceptResult(Object result);

    protected abstract boolean processResult(Object result, String codePath, S scope) throws GlowwormException;

    public void setScopeManager(ScopeManager scopeManager) {
        this.scopeManager = scopeManager;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setInterpretManager(InterpretManager interpretManager) {
        this.interpretManager = interpretManager;
    }
}