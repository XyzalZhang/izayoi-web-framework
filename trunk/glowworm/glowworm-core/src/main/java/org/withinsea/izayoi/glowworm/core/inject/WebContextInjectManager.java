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

package org.withinsea.izayoi.glowworm.core.inject;

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.injector.Injector;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-11
 * Time: 16:47:54
 */
public class WebContextInjectManager implements InjectManager {

    protected CodeManager codeManager;
    protected Map<String, Injector> injectors;

    @Override
    public void inject(HttpServletRequest request, Scope scope, String dataPath, String asType) throws GlowwormException {

        String type = checkType(dataPath, asType);
        Injector injector = getInjector(type);

        if (injector != null && injector.isSupport(type)) {
            injector.inject(request, scope, dataPath, type, codeManager.get(dataPath).getCode());
        }
    }

    protected String checkType(String path, String asType) {
        if (asType == null || asType.equals("")) asType = path.replaceAll(".*\\.", "");
        return asType;
    }

    protected Injector getInjector(String type) {
        return injectors.get(injectors.containsKey(type) ? type : "default");
    }

    // dependency

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setInjectors(Map<String, Injector> injectors) {
        this.injectors = injectors;
    }
}
