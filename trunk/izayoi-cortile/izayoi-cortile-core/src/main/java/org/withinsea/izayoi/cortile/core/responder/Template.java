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

package org.withinsea.izayoi.cortile.core.responder;

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.custom.Request;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.compile.CompileManager;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-8
 * Time: 14:12:03
 */
public class Template implements Invoker<Request> {

    protected CodeManager codeManager;
    protected CompileManager compileManager;
    protected String encoding;

    @Override
    public boolean invoke(String codePath, Request scope) throws IzayoiException {

        HttpServletRequest request = scope.getRequest();
        HttpServletResponse response = scope.getResponse();
        FilterChain chain = scope.getChain();

        String entrancePath = compileManager.update(codePath, false);

        if (!entrancePath.equals(codePath)) {

            response.setCharacterEncoding(encoding);

            Path parsedPath = new Path(codePath);
            String mimeType = codeManager.getMimeType(parsedPath.getMainType());
            if (mimeType == null) mimeType = codeManager.getMimeType(parsedPath.getType());
            if (mimeType != null) {
                response.setContentType(mimeType + "; charset=" + encoding);
            }

            try {
                request.getRequestDispatcher(entrancePath).forward(request, response);
            } catch (Exception e) {
                throw new CortileException(e);
            }

            if (mimeType != null) {
                response.setContentType(mimeType + "; charset=" + encoding);
            }

            return true;

        } else if (chain != null) {

            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                throw new CortileException(e);
            }

            return true;

        } else {

            return false;
        }
    }

    public void setCompileManager(CompileManager compileManager) {
        this.compileManager = compileManager;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }
}
