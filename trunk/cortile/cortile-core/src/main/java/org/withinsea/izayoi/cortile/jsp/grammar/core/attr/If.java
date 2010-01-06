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

package org.withinsea.izayoi.cortile.jsp.grammar.core.attr;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.xml.DOM4JUtils;
import org.withinsea.izayoi.commons.xml.HTMLDocumentFactory;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:36:16
 */
public class If implements AttrGrammar {

    protected ELInterpreter elInterpreter;

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return attr.getName().startsWith("if");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {

        String el = attr.getValue().trim();
        el = (el.startsWith("${") && el.endsWith("}")) ? el.substring(2, el.length() - 3).trim() : el;

        if (!el.equals("")) {

            String preScriptlet = "if ((Boolean) " + elInterpreter.compileEL(attr.getValue()) + ") { varstack.push();";
            String sufScriptlet = "varstack.pop(); }";

            String ifname = attr.getName().substring("if".length());
            if (ifname.equals("")) {
                try {
                    DOM4JUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
                } catch (Exception e) {
                    throw new CortileException(e);
                }
            } else if (ifname.equals(".content")) {
                try {
                    DOM4JUtils.surroundInside(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
                } catch (Exception e) {
                    throw new CortileException(e);
                }
            } else if (ifname.startsWith(".")) {
                String ifAttrname = ifname.startsWith(".attr.") ? ifname.substring(".attr.".length()) : ifname.substring(1);
                for (HTMLDocumentFactory.SurroundableAttr ifAttr : (List<HTMLDocumentFactory.SurroundableAttr>) elem.attributes()) {
                    if (ifAttr.getName().equals(ifAttrname)) {
                        ifAttr.setPrefix("<%" + preScriptlet + "%>");
                        ifAttr.setSuffix("<%" + sufScriptlet + "%>");
                    }
                }
            }
        }

        attr.detach();
    }

    @SuppressWarnings("unused")
    public void setElInterpreter(ELInterpreter elInterpreter) {
        this.elInterpreter = elInterpreter;
    }
}
