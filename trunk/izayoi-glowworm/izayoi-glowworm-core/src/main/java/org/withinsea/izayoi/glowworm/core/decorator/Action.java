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

package org.withinsea.izayoi.glowworm.core.decorator;

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.core.invoker.ResultInvoker;
import org.withinsea.izayoi.core.scope.custom.Request;

import java.util.Arrays;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:54:20
 */
public class Action extends ResultInvoker<Request> {

    protected Data data = new Data();
    protected Dispatcher dispatcher = new Dispatcher();

    @Override
    protected boolean processResult(Object result, String codePath, Request scope) throws IzayoiException {

        if (result == null) {
            return true;
        } else if (Boolean.valueOf(false).equals(result)) {
            return false;
        } else if (result instanceof Iterable) {
            for (Object item : (Iterable) result) {
                if (!processResult(item, codePath, scope)) {
                    return false;
                }
            }
            return true;
        } else if (result.getClass().isArray()) {
            return processResult(Arrays.asList((Object[]) result), codePath, scope);
        } else if (result instanceof String) {
            return dispatcher.processResult(result, codePath, scope);
        } else {
            return data.processResult(result, codePath, scope);
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