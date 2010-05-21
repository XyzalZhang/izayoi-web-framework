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

package org.withinsea.izayoi.cortile.template.html.grammar.core.attr;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.cortile.core.compile.el.ELSupportedCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.core.compile.Compilr;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:09:11
 */
public class Set implements AttrGrammar<HTMLCompiler> {

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        String attrname = attr.getName().replaceAll("[:_-]", ".");
        return attrname.startsWith("set.");
    }

    @Override
    public void processAttr(HTMLCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {

        String attrname = attr.getName().replaceAll("[:_-]", ".");
        String var = attrname.substring("set.".length());
        String value = attr.getValue();

        String preScriptlet = compiler.elScope();
        if (!(value.startsWith("${") && value.endsWith("}")) || value.substring(2, value.length() - 1).indexOf("${") > 0) {
            preScriptlet = preScriptlet + compiler.elBind(var, compileEmbeddedELs(compiler, value));
        } else {
            String el = value.substring(2, value.length() - 1).trim();
            preScriptlet = preScriptlet + compiler.elBind(var, compiler.compileEL(el));
        }
        preScriptlet = preScriptlet + compiler.elScope();

        String sufScriptlet = compiler.elScopeEnd() + compiler.elScopeEnd();

        try {
            DOMUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }

    protected String compileEmbeddedELs(final ELSupportedCompiler compiler, String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + compiler.el(groups[1].replace("\\}", "}"), true) + "+ \"";
            }
        }) + "\"";
    }

}