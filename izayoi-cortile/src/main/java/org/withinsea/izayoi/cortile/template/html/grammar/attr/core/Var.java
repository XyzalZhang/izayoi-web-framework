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

package org.withinsea.izayoi.cortile.template.html.grammar.attr.core;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:09:11
 */
public class Var implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        String attrname = attr.getName();
        return attrname.startsWith("var.");
    }

    @Override
    public void processAttr(Attribute attr) throws CortileException {

        CompileContext ctx = CompileContext.get();
        HTMLCompiler compiler = ctx.getCompiler();

        Element elem = attr.getParent();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        String varname = attrname.substring("var.".length());

        String value = attrvalue.trim();
        boolean embedded = !(attrvalue.startsWith("${") && attrvalue.endsWith("}")) || attrvalue.indexOf("${", 1) > 0;
        String compiledValue = embedded
                ? compileEmbeddedELs(compiler, attrvalue)
                : compiler.el(value.substring(2, value.length() - 1).trim());

        String preScriptlet = compiler.openScope() + compiler.elBind(varname, compiledValue) + compiler.openScope();
        String sufScriptlet = compiler.closeScope() + compiler.closeScope();

        try {
            DOMUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }

    protected String compileEmbeddedELs(final HTMLCompiler compiler, String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + compiler.el(groups[1].replace("\\}", "}"), true) + "+ \"";
            }
        }) + "\"";
    }
}