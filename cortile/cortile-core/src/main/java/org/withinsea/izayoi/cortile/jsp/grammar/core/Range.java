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

package org.withinsea.izayoi.cortile.jsp.grammar.core;

import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.compiler.dom.PretreatGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.jsp.io.JSPWriter;
import org.withinsea.izayoi.cortile.util.StringUtils;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:16:41
 */
public class Range implements PretreatGrammar {

    @Override
    public boolean acceptPretreat(String code) {
        return true;
    }

    @Override
    public String pretreatCode(DOMCompiler compiler, Compilr.Result result, String code) throws CortileException {
        code = StringUtils.replaceAll(code, "<!--</\\s*>?\\s*-->", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "</" + JSPWriter.RANGE_TAG_NAME + ">";
            }
        });
        code = StringUtils.replaceAll(code, "<!--<([\\s\\S]*?)\\s*(/>|>)?\\s*-->", new StringUtils.Replace() {
            public String replace(String... groups) {
                String slash = (groups.length > 2 && "/>".equals(groups[2])) ? "/" : "";
                return "<" + JSPWriter.RANGE_TAG_NAME + " " + groups[1] + slash + ">";
            }
        });
        int start = (code.indexOf("<!DOCTYPE ") < 0) ? 0 : code.indexOf(">", code.indexOf("<!DOCTYPE ")) + 1;
        code = code.substring(0, start) + "<" + JSPWriter.RANGE_TAG_NAME + ">" +
                code.substring(start) + "</" + JSPWriter.RANGE_TAG_NAME + ">";
        return code;
    }
}
