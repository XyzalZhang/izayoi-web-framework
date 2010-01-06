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

package org.withinsea.izayoi.glowworm.core.injector;

import org.withinsea.izayoi.commons.servlet.NulHttpServletResponseWrapper;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 18:00:15
 */
@SuppressWarnings("unused")
public class JSP implements Injector {

    @Override
    public void inject(HttpServletRequest request, HttpServletResponse response, String srcPath, String src) throws GlowwormException {
        try {
            request.getRequestDispatcher(srcPath).forward(request, new NulHttpServletResponseWrapper(response));
        } catch (ServletException e) {
            throw new GlowwormException(e);
        } catch (IOException e) {
            throw new GlowwormException(e);
        }
    }
}