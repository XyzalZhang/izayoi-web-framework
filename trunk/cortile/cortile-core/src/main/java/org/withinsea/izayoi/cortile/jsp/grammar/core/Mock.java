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

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.GrammarUtils;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.compiler.dom.PretreatGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.RoundoffGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:21:31
 */
public class Mock implements PretreatGrammar, RoundoffGrammar, AttrGrammar {

    public static final String MOCK_TRICK_ATTR_NAME = Mock.class.getCanonicalName() + ".MOCK_TRICK";

    @Override
    public boolean acceptPretreat(String code) {
        return true;
    }

    @Override
    @Priority(99)
    public String pretreatCode(DOMCompiler compiler, Compilr.Result result, String code) throws CortileException {
        String mockTrickString = "ＭＯＣＫ";
        while (code.indexOf(mockTrickString) >= 0) {
            mockTrickString += "ＭＯＣＫ";
        }
        final String mockValue = "_" + mockTrickString + "_";
        result.setAttribute(MOCK_TRICK_ATTR_NAME, mockValue);
        return StringUtils.replaceAll(code, "(<[^<]*?:mock)([^>]*?>)", new StringUtils.Replace() {
            public String replace(String... groups) {
                if (!groups[2].trim().startsWith("=") && !groups[1].startsWith("<!--")) {
                    return groups[1] + "=\"" + mockValue + "\"" + groups[2];
                } else {
                    return groups[0];
                }
            }
        });
    }

    @Override
    public boolean acceptRoundoff(String code) {
        return true;
    }

    @Override
    @Priority(99)
    public String roundoffCode(DOMCompiler compiler, Compilr.Result result, String code) throws CortileException {
        String mockTrick = result.getAttribute(MOCK_TRICK_ATTR_NAME);
        return code.replaceAll("(\\w+:)?mock\\s*=\\s*\"" + Pattern.quote(mockTrick) + "\"", "");
    }

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return GrammarUtils.Accept.byName(attr, "mock");
    }

    @Override
    @Priority(99)
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {
        elem.detach();
    }
}
