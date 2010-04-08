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

package org.withinsea.izayoi.cortile.template.elt;

import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.cortile.core.compiler.java.JSPCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-9
 * Time: 1:46:46
 */
public class ELTCompiler extends JSPCompiler {

    @Override
    public String compileJSP(String jspContent) throws CortileException {
        return super.compileJSP(StringUtils.replaceAll(jspContent, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            @Override
            public String replace(String... groups) {
                return "<%=" + el(groups[1].replace("\\}", "}"), true) + "%>";
            }
        }));
    }
}
