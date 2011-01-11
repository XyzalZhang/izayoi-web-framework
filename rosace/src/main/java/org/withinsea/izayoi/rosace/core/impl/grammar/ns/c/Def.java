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
import org.dom4j.Document;
import org.dom4j.Element;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.HostlangUtils;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.Grammar;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.RoundoffGrammar;
import org.withinsea.izayoi.rosace.core.kernel.IncludeSupport;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConstants;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:04:16
 */
public class Def extends Call implements AttrGrammar, RoundoffGrammar {

    protected static final String SECTION_MARK = "/*" + Def.class.getCanonicalName() + "_SECTION_MARK*/";

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("def") || attr.getName().equals("def-only");
    }

    @Override
    public void processAttr(Attribute attr) throws RosaceException {

        Element elem = attr.getParent();
        Document doc = elem.getDocument();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        String section = attrvalue.trim();

        if (attrname.equals("def")) {
            processAttr(elem, ":" + section);
        }

        String sectionCheck = Call.class.getCanonicalName() + ".isSection("
                + RosaceConstants.VARIABLE_VARSTACK + ", \"" + HostlangUtils.jspString(section) + "\")";
        try {
            DomUtils.prepend(doc, "<% return; } " + SECTION_MARK + " %>");
            DomUtils.prepend(doc, elem.detach());
            DomUtils.prepend(doc, "<% " + SECTION_MARK + " if (" + sectionCheck + ") { %>");
        } catch (Exception e) {
            throw new RosaceException(e);
        }

        attr.detach();
    }

    @Override
    public boolean acceptRoundoff(String code) {
        return true;
    }

    @Override
    @Priority(Grammar.Priority.HIGH)
    public String roundoffCode(String code) throws RosaceException {
        int dt = code.indexOf("<!DOCTYPE");
        if (dt < 0) dt = 0;
        int start = code.indexOf("<% " + SECTION_MARK);
        int end = code.lastIndexOf(SECTION_MARK + " %>") + (SECTION_MARK + " %>").length();
        if (start >= 0 && end >= 0 && start <= end) {
            String invalidSectionCheck = "if (" +
                    Call.class.getCanonicalName() + ".isSection(" + RosaceConstants.VARIABLE_VARSTACK + ")) {" +
                    Def.class.getCanonicalName() + ".setIncludingFailed();" +
                    "return; }";
            code = code.substring(0, dt)
                    + code.substring(start, end).replace("<% " + SECTION_MARK, "<%").replace(SECTION_MARK + " %>", "%>")
                    + "<%" + invalidSectionCheck + "%>"
                    + code.substring(dt, start) + code.substring(end);
        }
        return code;
    }

    public static void setIncludingFailed() {
        IncludeSupport.Tracer.getIncludingStack().peek().setFailed(true);
    }
}

