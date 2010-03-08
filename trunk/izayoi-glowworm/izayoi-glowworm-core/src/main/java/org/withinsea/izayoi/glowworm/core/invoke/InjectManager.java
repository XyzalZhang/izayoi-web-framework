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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-9
 * Time: 3:27:25
 */
public class InjectManager extends InvokeManagerImpl {

    @Override
    protected boolean processResult(HttpServletRequest request, HttpServletResponse response, Scope scope, Object result) throws GlowwormException {
        if (result != null) {
            if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> resultMap = (Map<String, Object>) result;
                for (Map.Entry<String, ?> e : resultMap.entrySet()) {
                    setAttribute(request, scope, e.getKey(), e.getValue());
                }
            } else if (result.getClass().isArray()) {
                for (Object object : (Object[]) result) {
                    if (!processResult(request, response, scope, object)) {
                        return false;
                    }
                }
            } else if (result instanceof Iterable) {
                for (Object object : (Iterable) result) {
                    if (!processResult(request, response, scope, object)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    protected static Object getAttribute(HttpServletRequest request, Scope scope, String name) {
        switch (scope) {
            case APPLICATION:
                return request.getSession().getServletContext().getAttribute(name);
            case SESSION:
                return request.getSession().getAttribute(name);
            case REQUEST:
                return request.getAttribute(name);
            default:
                return null;
        }
    }

    protected static void setAttribute(HttpServletRequest request, Scope scope, String name, Object value) {
        switch (scope) {
            case APPLICATION:
                request.getSession().getServletContext().setAttribute(name, value);
                break;
            case SESSION:
                request.getSession().setAttribute(name, value);
                break;
            case REQUEST:
                request.setAttribute(name, value);
                break;
        }
    }
}
