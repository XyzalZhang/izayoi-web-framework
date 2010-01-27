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
import org.withinsea.izayoi.commons.html.DOMUtils;
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:09:11
 */
public class Set implements AttrGrammar {

    protected ELInterpreter elInterpreter;

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        String attrname = attr.getName().replaceAll("[:_-]", ".");
        return attrname.startsWith("set.");
    }

    @Override
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {

        String attrname = attr.getName().replaceAll("[:_-]", ".");
        String var = attrname.substring("set.".length());
        String value = attr.getValue();

        String preScriptlet = "varstack.push();";
        if (!(value.startsWith("${") && value.endsWith("}")) || value.substring(2, value.length() - 1).indexOf("${") > 0) {
            preScriptlet = preScriptlet + "varstack.put(\"" + var + "\", " + compileEmbeddedELs(value) + ");";
        } else {
            String el = value.substring(2, value.length() - 1).trim();
            preScriptlet = preScriptlet + "varstack.put(\"" + var + "\", " + elInterpreter.compileEL(el) + ");";
        }
        preScriptlet = preScriptlet + "varstack.push();";

        String sufScriptlet = "varstack.pop();varstack.pop();";

        try {
            DOMUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }

    protected String compileEmbeddedELs(String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + elInterpreter.compileEL(groups[1].replace("\\}", "}")) + "+ \"";
            }
        }) + "\"";
    }

    @SuppressWarnings("unused")
    public void setElInterpreter(ELInterpreter elInterpreter) {
        this.elInterpreter = elInterpreter;
    }
}