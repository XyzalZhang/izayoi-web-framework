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

package org.withinsea.izayoi.cortile.template.compiler.html.grammar.core.attr;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.DefaultElement;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.compiler.Compilr;
import org.withinsea.izayoi.cortile.template.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.template.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.template.compiler.html.HTMLCompiler;
import org.withinsea.izayoi.cortile.template.compiler.html.parser.HTMLDocumentFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 21:50:40
 */
public class ByContent implements AttrGrammar<HTMLCompiler> {

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        String attrname = attr.getName().replaceAll("[:_-]", ".");
        return attrname.matches("^\\w+\\.content.*");
    }

    @Override
    @SuppressWarnings("unchecked")
    @Priority(50)
    public void processAttr(HTMLCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {
        String prefix = attr.getNamespacePrefix();
        List<Attribute> attrs = new ArrayList<Attribute>((List<Attribute>) elem.attributes());
        Element scope = elem;
        for (int i = elem.attributes().indexOf(attr); i < attrs.size(); i++) {
            Attribute attrI = attrs.get(i);
            String attrname = attrI.getName().replaceAll("[:_-]", ".");
            if (attrI.getNamespacePrefix().equals(prefix) && attrname.matches("^\\w+\\.content.*")) {
                Element contentScope = new DefaultElement(DOMCompiler.ANONYMOUS_TAG_NAME);
                try {
                    DOMUtils.surroundInsideBy(scope, contentScope);
                } catch (Exception e) {
                    throw new CortileException(e);
                }
                attrI.detach();
                contentScope.add(new HTMLDocumentFactory.SurroundableAttr(
                        new QName(attrname.replaceFirst("^(\\w+)\\.content", "$1"), attrI.getQName().getNamespace()),
                        attrI.getValue()
                ));
                for (int j = i + 1; j < attrs.size(); j++) {
                    Attribute attrJ = attrs.get(j);
                    attrJ.detach();
                    contentScope.add(attrJ);
                }
                scope = contentScope;
            }
        }
    }
}