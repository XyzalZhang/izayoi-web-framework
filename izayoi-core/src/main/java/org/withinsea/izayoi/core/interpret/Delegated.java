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

package org.withinsea.izayoi.core.interpret;

import org.withinsea.izayoi.commons.servlet.BufferedHttpServletResponse;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.context.BeanContext;
import org.withinsea.izayoi.core.context.BeanContextManager;
import org.withinsea.izayoi.core.context.Request;
import org.withinsea.izayoi.core.exception.IzayoiException;

import javax.script.Bindings;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-14
 * Time: 6:16:04
 */
public class Delegated implements Interpreter, MultiTypeInterpreter {

    @Override
    public boolean supportType(String type) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {

        if (code.getPath() == null
                || !(bindings.get("request") instanceof HttpServletRequest)
                || !(bindings.get("response") instanceof HttpServletResponse)) {
            return null;
        }

        HttpServletRequest request = (HttpServletRequest) bindings.get("request");
        HttpServletResponse response = (HttpServletResponse) bindings.get("response");
        InterpretContext interpretContext = (InterpretContext) bindings.get("interpretContext"); 

        Object originalInterpretContext = request.getAttribute("interpretContext");
        request.setAttribute("interpretContext", interpretContext);

        try {
            request.getRequestDispatcher(code.getPath().getPath()).forward(request, response);
        } catch (Exception e) {
            throw new IzayoiException(e);
        }

        if (originalInterpretContext == null) {
            request.removeAttribute("interpretContext");
        } else {
            request.setAttribute("interpretContext", originalInterpretContext);
        }

        if (response instanceof BufferedHttpServletResponse) {
            return (T) ((BufferedHttpServletResponse) response).getBuffer();
        } else {
            return null;
        }
    }
}