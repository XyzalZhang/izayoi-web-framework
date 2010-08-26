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

import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-16
 * Time: 3:50:04
 */
public class Action extends ResultInvoker {

    @Resource
    Map<String, Invoker> invokers;

    @Resource
    List<String> invokersOrder;

    @Override
    protected boolean acceptResult(Object result) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected boolean processResult(HttpServletRequest request, HttpServletResponse response,
                                    String codePath, Scope scope, Object result) throws GlowwormException {

        Collection<Object> results = (result == null) ? Arrays.asList((Object) null)
                : (result instanceof Collection) ? (Collection<Object>) result
                : (result.getClass().isArray() && result.getClass().getComponentType() != byte.class) ? Arrays.asList((Object[]) result)
                : Arrays.asList(result);

        for (Object resultItem : results) {
            for (ResultInvoker invoker : getResultInvokers()) {
                if (invoker.acceptResult(resultItem)) {
                    if (!invoker.processResult(request, response, codePath, scope, resultItem)) {
                        return false;
                    }
                    break;
                }
            }
        }

        return true;
    }

    protected List<ResultInvoker> getResultInvokers() {

        List<ResultInvoker> resultInvokers = new ArrayList<ResultInvoker>();
        for (String invokerType : invokersOrder) {
            Invoker invoker = invokers.get(invokerType);
            if (!(invoker instanceof Action) && (invoker instanceof ResultInvoker)) {
                resultInvokers.add((ResultInvoker) invoker);
            }
        }

        return resultInvokers;
    }
}
