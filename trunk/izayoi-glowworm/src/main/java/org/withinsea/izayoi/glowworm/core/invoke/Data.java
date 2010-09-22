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
import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.core.serialize.SerializeManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-15
 * Time: 6:43:44
 */
public class Data extends ResultInvoker {

    @Resource
    SerializeManager serializeManager;

    @Resource
    String encoding;

    @Override
    protected boolean acceptResult(Object result) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean processResult(HttpServletRequest request, HttpServletResponse response,
                                    String codePath, Scope scope, Object result) throws GlowwormException {

        String accept = request.getHeader("Accept");
        String asType = new Path(codePath).getMainType();
        if (asType.equals("")) {
            asType = serializeManager.findType(accept);
            if (asType == null || asType.equals("")) {
                return false;
            }
        }

        String mimeType = codeContainer.getMimeType(asType);
        if (!ServletFilterUtils.matchContentType(mimeType, accept)) {
            return false;
        } else if (mimeType != null) {
            response.setContentType(mimeType + "; charset=" + encoding);
        }

        response.setCharacterEncoding(encoding);

        try {

            if (serializeManager.isSerializable(asType)) {

                if (result == null) {

                } else if (result.getClass().isArray() && result.getClass().getComponentType() == byte.class) {
                    response.getOutputStream().write((byte[]) result);
                } else if (result instanceof String) {
                    response.getWriter().write((String) result);
                } else {
                    serializeManager.serialize(result.getClass(), result, asType, response.getOutputStream(), encoding);
                }

                response.flushBuffer();

            } else if (result instanceof Map) {

                for (Map.Entry<String, ?> e : ((Map<String, Object>) result).entrySet()) {
                    scope.setAttribute(e.getKey(), e.getValue());
                }

                Path path = new Path(codePath);
                String templatePath = path.getFolder() + "/" + path.getMainName() + "." + asType;
                ServletFilterUtils.forwardOrInclude(request, response, templatePath);
            }

        } catch (Exception e) {
            throw new GlowwormException(e);
        }

        return false;
    }
}