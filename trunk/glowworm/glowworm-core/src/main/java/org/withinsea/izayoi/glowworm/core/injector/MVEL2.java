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

import org.mvel2.MVEL;
import org.withinsea.izayoi.commons.el.Varstack;
import org.withinsea.izayoi.commons.servlet.HttpContextMap;
import org.withinsea.izayoi.commons.servlet.HttpParameterMap;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 18:00:15
 */
@SuppressWarnings("unused")
public class MVEL2 implements Injector {

    @Override
    public void inject(HttpServletRequest request, HttpServletResponse response, String srcPath, String src) throws GlowwormException {

        HttpParameterMap paramMap = new HttpParameterMap(request);
        HttpContextMap contextMap = new HttpContextMap(request);
        Map<String, Object> consts = new HashMap<String, Object>();
        {
            consts.put("params", paramMap);
            consts.put("application", request.getSession().getServletContext());
            consts.put("session", request.getSession());
            consts.put("request", request);
        }
        Varstack varstack = new Varstack();
        {
            varstack.push(paramMap);
            varstack.push(contextMap);
            varstack.push(consts);
            varstack.push();
        }

        MVEL.eval(src, varstack);

        Deque<Map<String, Object>> changes = new LinkedList<Map<String, Object>>();
        for (Map<String, Object> vars = varstack.pop(); vars != null && vars != consts; vars = varstack.pop()) {
            changes.push(vars);
        }
        for (Map<String, Object> vars : changes) {
            for (Map.Entry<String, Object> e : vars.entrySet()) {
                request.setAttribute(e.getKey(), e.getValue());
            }
        }
    }
}