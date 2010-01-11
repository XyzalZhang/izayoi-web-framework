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
        return attrname.equals("set") || attrname.startsWith("set.");
    }

    @Override
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {

        String attrname = attr.getName().replaceAll("[:_-]", ".");

        String el = attr.getValue().trim();
        el = (el.startsWith("${") && el.endsWith("}")) ? el.substring(2, el.length() - 1).trim() : el;
        if (attrname.startsWith("set.")) {
            el = attrname.substring("set.".length()) + "=" + el;
        }

        if (!el.equals("")) {
            String preScriptlet = "varstack.push();" + elInterpreter.compileEL(el) + ";";
            String sufScriptlet = "varstack.pop();";
            try {
                DOMUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
            } catch (Exception e) {
                throw new CortileException(e);
            }
        }

        attr.detach();
    }

    @SuppressWarnings("unused")
    public void setElInterpreter(ELInterpreter elInterpreter) {
        this.elInterpreter = elInterpreter;
    }
}