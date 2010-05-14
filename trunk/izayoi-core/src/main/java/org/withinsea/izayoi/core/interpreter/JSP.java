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

package org.withinsea.izayoi.core.interpreter;

import org.withinsea.izayoi.commons.servlet.ByteArrayHttpServletResponse;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.exception.IzayoiException;

import javax.script.Bindings;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-14
 * Time: 6:16:04
 */
public class JSP implements Interpreter {

    protected String encoding;

    @Override
    public <T> T interpret(String script, String asType, Bindings bindings, String... importedClasses) throws IzayoiException {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public String interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {

        HttpServletRequest request = (HttpServletRequest) bindings.get("request");
        HttpServletResponse response = (HttpServletResponse) bindings.get("response");

        if (request == null || response == null) {
            return null;
        }

        try {
            request.getRequestDispatcher(code.getPath()).forward(request, response);
            if (response instanceof ByteArrayHttpServletResponse) {
                return new String(((ByteArrayHttpServletResponse) response).getContent(), encoding);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new IzayoiException(e);
        }
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
