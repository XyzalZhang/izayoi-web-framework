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

import org.withinsea.izayoi.core.dependency.DependencyManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpreter.Interpreter;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.inject.InjectManager;

import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-3
 * Time: 1:23:47
 */
public class ScriptInjector implements Injector {

    protected static final ScriptEngineManager SCRIPT_ENGINE_MANAGER = new ScriptEngineManager();
    protected static final String DEFAULT_TYPE = "default";

    protected String dataObjectName;
    protected DependencyManager dependencyManager;
    protected Map<String, Interpreter> interpreters;

    @Override
    public boolean isSupport(String type) {
        return interpreters.keySet().contains(type) || (SCRIPT_ENGINE_MANAGER.getEngineByExtension(type) != null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void inject(HttpServletRequest request, InjectManager.Scope scope, String dataPath, String type, String src) throws GlowwormException {

        Interpreter interpreter = interpreters.get(type);
        if (interpreter == null) interpreter = interpreters.get(DEFAULT_TYPE);

        Object ret;
        try {
            ret = interpreter.interpret(src, dependencyManager.getDependency(request), type);
        } catch (IzayoiException ex) {
            throw new GlowwormException(ex);
        }

        if (ret != null) {
            if (ret instanceof Map) {
                Object dataObject = getAttribute(request, scope, dataObjectName);
                if (dataObject == null || !(dataObject instanceof Map)) {
                    dataObject = new LinkedHashMap<String, Object>();
                }
                ((Map<String, Object>) dataObject).putAll((Map<String, Object>) ret);
                setAttribute(request, scope, dataObjectName, dataObject);
                for (Map.Entry<String, ?> e : ((Map<String, ?>) ret).entrySet()) {
                    setAttribute(request, scope, e.getKey(), e.getValue());
                }
            } else {
                setAttribute(request, scope, dataObjectName, ret);
            }
        }
    }

    protected static Object getAttribute(HttpServletRequest request, InjectManager.Scope scope, String name) {
        switch (scope) {
            case APPLICATION:
                return request.getSession().getServletContext().getAttribute(name);
            case SESSION:
                return request.getSession().getAttribute(name);
            case REQUEST:
                return request.getAttribute(name);
        }
        return null;
    }

    protected static void setAttribute(HttpServletRequest request, InjectManager.Scope scope, String name, Object value) {
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

    public void setDataObjectName(String dataObjectName) {
        this.dataObjectName = dataObjectName;
    }

    public void setDependencyManager(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    public void setInterpreters(Map<String, Interpreter> interpreters) {
        this.interpreters = interpreters;
    }
}