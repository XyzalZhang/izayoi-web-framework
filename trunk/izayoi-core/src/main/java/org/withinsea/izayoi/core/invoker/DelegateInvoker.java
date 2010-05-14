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
public abstract class DelegateInvoker<S extends Scope> implements Invoker<S> {

    protected CodeManager codeManager;
    protected InterpretManager interpretManager;

    @Override
    public boolean invoke(String codePath, S scope) throws IzayoiException {

        if (!codeManager.exist(codePath)) {
            throw new IzayoiException("code " + codePath + " does not exist.");
        }

        interpretManager.interpret(codeManager.get(codePath), getBinding(scope));

        return true;
    }

    protected Varstack getBinding(Scope scope) {
        Varstack bindings = new Varstack();
        {
            bindings.push(ScopeUtils.getBindings(scope));
            bindings.push();
        }
        return bindings;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setInterpretManager(InterpretManager interpretManager) {
        this.interpretManager = interpretManager;
    }
}