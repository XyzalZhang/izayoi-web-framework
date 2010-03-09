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

import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-9
 * Time: 4:27:45
 */
public class ActManager extends InjectManager {

    @Override
    protected boolean processResult(HttpServletRequest request, HttpServletResponse response, Scope scope, Object result) throws GlowwormException {
        if (result != null) {
            if (result instanceof String) {
                String url = ((String) result).trim();
                try {
                    if (url.startsWith("forward:")) {
                        url = url.substring("forward:".length()).trim();
                        request.getRequestDispatcher(url).forward(request, response);
                    } else if (url.startsWith("page:")) {
                        url = url.substring("page:".length()).trim();
                        url = (url.startsWith("/")) ? request.getContextPath() + url : url;
                        response.sendRedirect(url);
                    } else if (url.startsWith("redirect:")) {
                        url = url.substring("redirect:".length()).trim();
                        response.sendRedirect(url);
                    } else {
                        response.sendRedirect(url);
                    }
                } catch (ServletException e) {
                    throw new GlowwormException(e);
                } catch (IOException e) {
                    throw new GlowwormException(e);
                }
                return false;
            } else {
                return super.processResult(request, response, scope, result);
            }
        }
        return true;
    }
}
