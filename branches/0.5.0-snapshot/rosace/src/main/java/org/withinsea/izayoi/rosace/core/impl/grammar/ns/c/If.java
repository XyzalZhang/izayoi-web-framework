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

package org.withinsea.izayoi.rosace.core.impl.grammar.ns.c;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.impl.template.dom.parser.DomSurroundableAttr;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;

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
    public void processAttr(Attribute attr) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();

        Element elem = attr.getParent();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        String value = attrvalue.trim();
        String el = value.startsWith("${") ? value.substring(2, value.length() - 1).trim() : value;

        String preScriptlet = "if (Boolean.TRUE.equals(" + engine.precompileEl(el) + ")) { " + engine.precompileOpenScope();
        String sufScriptlet = engine.precompileCloseScope() + " }";

        try {

            if (attrname.equals("if")) {
                DomUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
            } else {
                String ifAttrname = attrname.startsWith("if-attr.") ?
                        attr.getName().substring("if-attr.".length()) :
                        attr.getName().substring("if.".length());
                for (DomSurroundableAttr ifAttr : (List<DomSurroundableAttr>) elem.attributes()) {
                    if (ifAttr.getName().equals(ifAttrname)) {
                        ifAttr.setPrefix("<%" + preScriptlet + "%>");
                        ifAttr.setSuffix("<%" + sufScriptlet + "%>");
                    }
                }
            }

        } catch (Exception e) {
            throw new RosaceException(e);
        }

        attr.detach();
    }
}
