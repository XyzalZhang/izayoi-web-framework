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

import org.withinsea.izayoi.core.bindings.BindingsManager;
import org.withinsea.izayoi.core.bindings.Varstack;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-9
 * Time: 3:27:43
 */
public abstract class InvokeManagerImpl implements InvokeManager {

    protected CodeManager codeManager;
    protected BindingsManager bindingsManager;
    protected InterpretManager interpretManager;

    protected abstract boolean processResult(HttpServletRequest request, HttpServletResponse response, Scope scope, Object result) throws GlowwormException;

    @Override
    public boolean invoke(HttpServletRequest request, HttpServletResponse response, String scriptPath, String asType, Scope scope) throws GlowwormException {

        if (!codeManager.exist(scriptPath)) {
            throw new GlowwormException("script " + scriptPath + " does not exist.");
        }

        asType = (asType != null) ? asType : scriptPath.replaceAll(".*\\.", "");

        Code code = codeManager.get(scriptPath);
        Varstack bindings = new Varstack();
        {
            bindings.push(bindingsManager.getBindings(request, response));
            bindings.push();
        }

        Object result;
        try {
            result = interpretManager.interpret(code.getCode(), bindings, asType);
        } catch (IzayoiException e) {
            throw new GlowwormException(e.getMessage(), e.getCause());
        }

        return processResult(request, response, scope, result);
    }

    public void setBindingsManager(BindingsManager bindingsManager) {
        this.bindingsManager = bindingsManager;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setInterpretManager(InterpretManager interpretManager) {
        this.interpretManager = interpretManager;
    }
}
