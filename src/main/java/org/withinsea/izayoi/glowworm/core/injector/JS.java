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

import org.withinsea.izayoi.commons.js.JSUtils;
import org.withinsea.izayoi.glowworm.core.dependency.Dependency;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-29
 * Time: 16:53:13
 */
public class JS implements Injector {

    protected static Pattern IS_FUNC = Pattern.compile("^function(\\s+\\w+)?\\s*\\([\\s\\S]*");

    @Override
    public Object inject(Dependency dependency, HttpServletRequest request,
                         String srcPath, String src) throws GlowwormException {

        src = src.trim().replaceAll("^\\(", "").replaceAll("\\}\\s*\\)$", "}").trim();
        Object[] args;

        if (!IS_FUNC.matcher(src).matches()) {
            src = "function () {" + src + "}";
            args = new Object[]{};
        } else {
            String argsList = src.substring(src.indexOf("(") + 1, src.indexOf(")")).trim();
            String[] argNames = "".equals(argsList) ? new String[]{} : argsList.split("[,\\s]+");
            args = new Object[argNames.length];
            for (int i = 0; i < argNames.length; i++) {
                args[i] = dependency.getBean(argNames[i]);
            }
        }

        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");
            Object thiz = engine.eval("({ func: " + src + " })");
            Invocable invoke = (Invocable) engine;
            return JSUtils.js2java(invoke.invokeMethod(thiz, "func", args));
        } catch (Exception e) {
            throw new GlowwormException(e);
        }
    }
}
