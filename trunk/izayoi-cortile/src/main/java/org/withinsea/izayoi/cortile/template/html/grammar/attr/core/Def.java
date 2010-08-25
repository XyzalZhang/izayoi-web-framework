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
import org.dom4j.Document;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compile.dom.Grammar;
import org.withinsea.izayoi.cortile.core.compile.dom.RoundoffGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:04:16
 */
public class Def extends Call implements AttrGrammar, RoundoffGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("def") || attr.getName().equals("def-only");
    }

    @Override
    public void processAttr(Attribute attr) throws CortileException {

        CompileContext ctx = CompileContext.get();

        Element elem = attr.getParent();
        Document doc = elem.getDocument();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        String func = attrvalue.trim();

        if (attrname.equals("def")) {
            processAttr(elem, ctx.getTemplatePath() + ":" + func);
        }

        try {
            DOMUtils.prepend(doc, "<% return; } /*FUNC*/ %>");
            DOMUtils.prepend(doc, elem.detach());
            DOMUtils.prepend(doc, "<% /*FUNC*/ if (" + Call.class.getCanonicalName() + ".isIncluded(request, \"" + func + "\")) { %>");
        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }

    @Override
    public boolean acceptRoundoff(String code) {
        return true;
    }

    @Override
    @Priority(Grammar.Priority.HIGH)
    public String roundoffCode(String code) throws CortileException {
        int dt = code.indexOf("<!DOCTYPE");
        int start = code.indexOf("<% /*FUNC*/");
        int end = code.lastIndexOf("/*FUNC*/ %>") + "/*FUNC*/ %>".length();
        if (start >= 0 && end >= 0 && start <= end) {
            code = code.substring(0, dt) +
                    code.substring(start, end).replace("<% /*FUNC*/", "<%").replace("/*FUNC*/ %>", "%>")
                    + code.substring(dt, start) + code.substring(end);
        }
        return code;
    }
}

