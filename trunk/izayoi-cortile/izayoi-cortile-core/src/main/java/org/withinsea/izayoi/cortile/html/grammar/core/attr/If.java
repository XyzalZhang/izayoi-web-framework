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

package org.withinsea.izayoi.cortile.html.grammar.core.attr;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.html.HTMLCompiler;
import org.withinsea.izayoi.cortile.html.parser.HTMLDocumentFactory;

import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:36:16
 */
public class If implements AttrGrammar<HTMLCompiler> {

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        String attrname = attr.getName().replaceAll("[:_-]", ".");
        return attrname.equals("if") || attrname.startsWith("if.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processAttr(HTMLCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {

        String el = attr.getValue().trim();
        el = (el.startsWith("${") && el.endsWith("}")) ? el.substring(2, el.length() - 1).trim() : el;
        if (el.equals("") || el.indexOf("${") > 0 || el.matches(".*[^\\\\]}.*")) {
            throw new CortileException("\"" + attr.getValue() + "\" is not a valid EL script.");
        }

        String preScriptlet = "if ((Boolean) " + compiler.compileEL(el) + ") { " + compiler.elScope();
        String sufScriptlet = compiler.elScopeEnd() + " }";

        String attrname = attr.getName().replaceAll("[:_-]", ".");
        if (attrname.equals("if")) {
            try {
                DOMUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
            } catch (Exception e) {
                throw new CortileException(e);
            }
        } else {
            String ifname = attrname.substring("if.".length());
            String ifAttrname = ifname.startsWith("attr.") ? ifname.substring("attr.".length()) : ifname;
            for (HTMLDocumentFactory.SurroundableAttr ifAttr : (List<HTMLDocumentFactory.SurroundableAttr>) elem.attributes()) {
                if (ifAttr.getName().equals(ifAttrname)) {
                    ifAttr.setPrefix("<%" + preScriptlet + "%>");
                    ifAttr.setSuffix("<%" + sufScriptlet + "%>");
                }
            }
        }

        attr.detach();
    }
}
