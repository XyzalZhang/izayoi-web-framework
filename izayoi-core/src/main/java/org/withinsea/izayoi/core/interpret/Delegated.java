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

import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.exception.IzayoiException;

import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-7-9
 * Time: 11:51:57
 */
public class Delegated implements Interpreter {

    @Override
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {

        if (code.getPath() == null
                || !(bindings.get("request") instanceof HttpServletRequest)
                || !(bindings.get("response") instanceof HttpServletResponse)) {
            return null;
        }

        HttpServletRequest request = (HttpServletRequest) bindings.get("request");
        HttpServletResponse response = (HttpServletResponse) bindings.get("response");

        try {
            request.getRequestDispatcher(code.getPath().getPath()).forward(request, response);
        } catch (Exception e) {
            throw new IzayoiException(e);
        }

        return null;
    }
}
