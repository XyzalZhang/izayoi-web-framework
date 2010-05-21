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
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.core.compile.Compilr;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-11
 * Time: 15:14:01
 */
public class Out implements AttrGrammar<HTMLCompiler> {

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        String attrname = attr.getName().replaceAll("[:_-]", ".");
        return attrname.startsWith("attr.") || attrname.equals("content");
    }

    @Override
    @Priority(-99)
    public void processAttr(HTMLCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {
        String attrname = attr.getName().replaceAll("[:_-]", ".");
        if (attrname.startsWith("attr.")) {
            elem.addAttribute(attrname.substring("attr.".length()), attr.getValue()
                    .replace("<%=", "<%=" + Out.class.getCanonicalName() + ".escapeAttrValue(")
                    .replace("%>", ")%>")
            );
        } else {
            elem.clearContent();
            elem.addText(attr.getValue());
        }
        attr.detach();
    }

    public static String escapeAttrValue(Object value) {
        return (value == null) ? "null" : value.toString().replace("\"", "&quot;");
    }
}
