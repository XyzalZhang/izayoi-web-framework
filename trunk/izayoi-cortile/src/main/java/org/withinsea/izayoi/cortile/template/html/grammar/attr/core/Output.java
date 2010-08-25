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
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-11
 * Time: 15:14:01
 */
public class Output implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        String attrname = attr.getName();
        return attrname.startsWith("attr.") || attrname.equals("content");
    }

    @Override
    @Priority(Priority.LOWER)
    public void processAttr(Attribute attr) throws CortileException {

        Element elem = attr.getParent();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        if (attrname.startsWith("attr.")) {
            String forAttrname = attrname.substring("attr.".length());
            Attribute forAttr = elem.attribute(forAttrname);
            String escapedValue = attrvalue
                    .replace("<%=", "<%=" + Output.class.getCanonicalName() + ".escapeAttrValue(")
                    .replace("%>", ")%>");
            if (forAttr == null) {
                elem.addAttribute(forAttrname, escapedValue);
            } else {
                forAttr.setValue(escapedValue);
            }
        } else {
            elem.clearContent();
            elem.addText(attr.getValue());
        }

        attr.detach();
    }

    public static String escapeAttrValue(Object value) {
        return (value == null) ? "" : value.toString().replace("\"", "&quot;");
    }
}
