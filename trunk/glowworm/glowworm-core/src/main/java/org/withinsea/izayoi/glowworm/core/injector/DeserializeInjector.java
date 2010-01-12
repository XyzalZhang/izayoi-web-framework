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

import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 18:00:15
 */
public abstract class DeserializeInjector implements Injector {

    protected abstract Object deserialize(String src) throws GlowwormException;

    protected String globalName;

    @Override
    public void inject(HttpServletRequest request, String srcPath, String src) throws GlowwormException {
        Object obj = deserialize(src);
        if (obj != null && globalName != null && !globalName.equals("")) {
            request.setAttribute(globalName, obj);
        }
        if (obj instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) obj;
            for (Map.Entry<String, ?> e : map.entrySet()) {
                request.setAttribute(e.getKey(), e.getValue());
            }
        }
    }

    public void setGlobalName(String globalName) {
        this.globalName = globalName;
    }
}
