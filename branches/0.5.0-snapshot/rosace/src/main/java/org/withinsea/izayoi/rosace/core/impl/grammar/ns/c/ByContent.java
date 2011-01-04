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
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.impl.template.dom.parser.DomSurroundableAttr;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 21:50:40
 */
public class ByContent implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        String attrname = attr.getName();
        return attrname.matches("^\\w+-(content|c)(|\\..+)$");
    }

    @Override
    @SuppressWarnings("unchecked")
    @Priority(Priority.HIGHEST)
    public void processAttr(Attribute attr) throws RosaceException {

        Element elem = attr.getParent();
        String prefix = attr.getNamespacePrefix();
        List<Attribute> attrs = new ArrayList<Attribute>((List<Attribute>) elem.attributes());

        try {
            Element scope = elem;
            for (int i = attrs.indexOf(attr); i < attrs.size(); i++) {
                Attribute iAttr = attrs.get(i);
                String iAttrname = iAttr.getName();
                if (iAttr.getNamespacePrefix().equals(prefix) && iAttrname.matches("^\\w+\\-(c|content).*")) {
                    Element contentScope = new DefaultElement(RosaceConstants.TAGNAME_ANONYMOUS);
                    DomUtils.surroundInsideBy(scope, contentScope);
                    iAttr.detach();
                    contentScope.add(new DomSurroundableAttr(
                            new QName(iAttrname.replaceFirst("^(\\w+)\\-(content|c)", "$1"), iAttr.getQName().getNamespace()),
                            iAttr.getValue()
                    ));
                    for (int j = i + 1; j < attrs.size(); j++) {
                        Attribute jAttr = attrs.get(j);
                        jAttr.detach();
                        contentScope.add(jAttr);
                    }
                    scope = contentScope;
                }
            }
        } catch (Exception e) {
            throw new RosaceException(e);
        }
    }
}