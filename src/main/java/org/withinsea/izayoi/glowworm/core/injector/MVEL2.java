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
import org.withinsea.izayoi.glowworm.core.dependency.Dependency;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 18:00:15
 */
@SuppressWarnings("unused")
public class MVEL2 implements Injector {

    protected static Pattern IS_FUNC = Pattern.compile("^def(\\s+\\w+)?\\s*\\([\\s\\S]*");

    @Override
    public Object inject(Dependency dependency, HttpServletRequest request,
                         String srcPath, String src) throws GlowwormException {

        src = src.trim().replaceAll("^\\(", "").replaceAll("\\}\\s*\\)$", "}").trim();
        Map<String, Object> args = new HashMap<String, Object>();

        if (IS_FUNC.matcher(src).matches()) {
            String argsList = src.substring(src.indexOf("(") + 1, src.indexOf(")")).trim();
            String[] argNames = "".equals(argsList) ? new String[]{} : argsList.split("[,\\s]+");
            for (String argName : argNames) {
                args.put(argName, dependency.getBean(argName));
            }
            src = src.substring(src.indexOf("{") + 1, src.lastIndexOf("}"));
        }

        return MVEL.eval(src, args);
    }
}