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
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;
import org.withinsea.izayoi.cortile.template.html.parser.SurroundableAttr;

import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:36:16
 */
public class If implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        String attrname = attr.getName();
        return attrname.equals("if") || attrname.startsWith("if-");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processAttr(Attribute attr) throws CortileException {

        CompileContext ctx = CompileContext.get();
        HTMLCompiler compiler = ctx.getCompiler();

        Element elem = attr.getParent();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        String value = attrvalue.trim();
        String el = value.startsWith("${") ? value.substring(2, value.length() - 1).trim() : value;

        String preScriptlet = "if ((Boolean) " + compiler.el(el) + ") { " + compiler.openScope();
        String sufScriptlet = compiler.closeScope() + " }";

        try {

            if (attrname.equals("if")) {
                DOMUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
            } else {
                String ifAttrname = attrname.startsWith("if.attr.") ?
                        attr.getName().substring("if.attr.".length()) :
                        attr.getName().substring("if.".length());
                for (SurroundableAttr ifAttr : (List<SurroundableAttr>) elem.attributes()) {
                    if (ifAttr.getName().equals(ifAttrname)) {
                        ifAttr.setPrefix("<%" + preScriptlet + "%>");
                        ifAttr.setSuffix("<%" + sufScriptlet + "%>");
                    }
                }
            }

        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }
}
