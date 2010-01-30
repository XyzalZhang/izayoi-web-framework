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

import org.withinsea.izayoi.commons.conf.CodeManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.injector.Injector;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-11
 * Time: 16:47:54
 */
public class WebappInjectManager implements InjectManager {

    protected String encoding;
    protected String dataObjectName;
    protected CodeManager codeManager;
    protected Map<String, Injector> injectors;

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public Set<String> getSupportedTypes() {
        return injectors.keySet();
    }

    @Override
    public boolean exist(String dataPath) {
        return codeManager.exist(dataPath);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inject(HttpServletRequest req, String dataPath, String asType) throws GlowwormException {
        Object ret = injectors.get(asType).inject(req, dataPath, codeManager.get(dataPath).getCode());
        if (ret != null && dataObjectName != null && !dataObjectName.equals("")) {
            if (ret instanceof Map) {
                Object dataObject = req.getAttribute(dataObjectName);
                if (dataObject == null || !(dataObject instanceof Map)) {
                    dataObject = new LinkedHashMap<String, Object>();
                    req.setAttribute(dataObjectName, dataObject);
                }
                ((Map<String, Object>) dataObject).putAll((Map<String, Object>) ret);
                for (Map.Entry<String, ?> e : ((Map<String, ?>) ret).entrySet()) {
                    req.setAttribute(e.getKey(), e.getValue());
                }
            } else {
                req.setAttribute(dataObjectName, ret);
            }
        }
    }

    // dependency

    @SuppressWarnings("unused")
    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    @SuppressWarnings("unused")
    public void setInjectors(Map<String, Injector> injectors) {
        this.injectors = injectors;
    }

    @SuppressWarnings("unused")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @SuppressWarnings("unused")
    public void setDataObjectName(String dataObjectName) {
        this.dataObjectName = dataObjectName;
    }
}