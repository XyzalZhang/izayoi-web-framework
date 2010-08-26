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

import org.withinsea.izayoi.core.code.CodeContainer;
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpret.BindingsUtils;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.core.interpret.Vars;
import org.withinsea.izayoi.core.interpret.Varstack;
import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:36:26
 */
public class Servlet implements Invoker {

    @Resource
    IzayoiContainer izayoiContainer;

    @Resource
    CodeContainer codeContainer;

    @Resource
    InterpretManager interpretManager;

    @Override
    public boolean invoke(HttpServletRequest request, HttpServletResponse response, String codePath, Scope scope) throws GlowwormException {

        Varstack bindings = new Varstack(
                BindingsUtils.asBindings(izayoiContainer),
                BindingsUtils.asBindings(scope),
                new Vars("request", request, "response", response),
                new Vars()
        );

        try {
            interpretManager.interpret(codeContainer.get(codePath), bindings);
        } catch (IzayoiException e) {
            throw new GlowwormException(e);
        }

        return false;
    }
}