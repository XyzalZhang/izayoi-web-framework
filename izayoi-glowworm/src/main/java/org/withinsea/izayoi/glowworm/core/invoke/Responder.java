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

import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.context.Request;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.serialize.SerializeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-15
 * Time: 6:43:44
 */
public class Responder extends ResultInvoker<Request> {

    protected SerializeManager serializeManager;
    protected String encoding;

    @Override
    protected boolean acceptResult(Object result) {
        return true;
    }

    @Override
    protected boolean processResult(Object result, String codePath, Request scope) throws GlowwormException {

        HttpServletRequest request = scope.getRequest();
        HttpServletResponse response = scope.getResponse();

        String accept = request.getHeader("Accept");
        String asType = new Path(codePath).getMainType();
        if (asType.equals("")) {
            asType = serializeManager.findType(accept);
            if (asType == null || asType.equals("")) {
                return false;
            }
        }

        String mimeType = codeManager.getMimeType(asType);
        if (!ServletFilterUtils.matchContentType(mimeType, accept)) {
            return false;
        } else if (mimeType != null) {
            response.setContentType(mimeType + "; charset=" + encoding);
        }

        response.setCharacterEncoding(encoding);

        try {

            if (result == null) {

            } else if (result.getClass().isArray() && result.getClass().getComponentType() == byte.class) {
                response.getOutputStream().write((byte[]) result);
            } else if (result instanceof String) {
                response.getWriter().write((String) result);
            } else {
                serializeManager.serialize(result.getClass(), result, asType, response.getOutputStream(), encoding);
            }

            response.flushBuffer();

        } catch (Exception e) {
            throw new GlowwormException(e);
        }

        return false;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setSerializeManager(SerializeManager serializeManager) {
        this.serializeManager = serializeManager;
    }
}