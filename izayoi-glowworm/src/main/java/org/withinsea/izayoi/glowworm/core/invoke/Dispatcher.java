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
import org.withinsea.izayoi.core.scope.Request;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-15
 * Time: 6:49:57
 */
public class Dispatcher extends ResultInvoker<Request> {

    @Override
    protected boolean acceptResult(Object result) {
        return (result != null) && (result instanceof String);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean processResult(Object result, String codePath, Request scope) throws GlowwormException {

        HttpServletRequest request = scope.getRequest();
        HttpServletResponse response = scope.getResponse();

        String url = ((String) result).trim();

        try {

            if (url.startsWith("continue:")) {
                return true;
            } else if (url.startsWith("stop:")) {
                return false;
            } else if (url.startsWith("forward:")) {
                url = url.substring("forward:".length()).trim();
                ServletFilterUtils.forwardOrInclude(request, response, url);
//                request.getRequestDispatcher(url).forward(request, response);
                return false;
            } else if (url.startsWith("redirect:")) {
                url = url.substring("redirect:".length()).trim();
                url = (url.startsWith("/")) ? request.getContextPath() + url : url;
                response.sendRedirect(url);
                return false;
            } else {
                response.sendRedirect(url);
                return false;
            }

        } catch (ServletException e) {
            throw new GlowwormException(e);
        } catch (IOException e) {
            throw new GlowwormException(e);
        }
    }
}