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
 * The Original Code is the @PROJECT_NAME
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.cortile.template.html;

import org.withinsea.izayoi.cortile.core.compile.CompileContext;

import java.util.Collection;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-25
 * Time: 11:08:31
 */
public class HTMLCompileContextUtils {

    public static final String ELType_ATTR = HTMLCompileContextUtils.class.getCanonicalName() + ".EL_TYPE";
    public static final String IMPORTS_ATTR = HTMLCompileContextUtils.class.getCanonicalName() + ".IMPORTS";

    public static String[] getContextImports() {
        CompileContext ctx = CompileContext.get();
        Collection<String> importedClasses = ctx.getScopeAttribute(IMPORTS_ATTR);
        if (importedClasses == null) {
            return new String[]{};
        } else {
            return importedClasses.toArray(new String[importedClasses.size()]);
        }
    }

    public static String getContextELType() {
        CompileContext ctx = CompileContext.get();
        return ctx.getScopeAttribute(ELType_ATTR);
    }
}
