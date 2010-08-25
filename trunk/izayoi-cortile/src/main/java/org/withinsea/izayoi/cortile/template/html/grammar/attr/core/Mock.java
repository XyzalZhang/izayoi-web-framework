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
import org.dom4j.Node;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.HashSet;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:21:31
 */
public class Mock implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("mock");
    }

    @Override
    @Priority(Priority.HIGHER)
    public void processAttr(Attribute attr) throws CortileException {

        Element elem = attr.getParent();

        String mockType = attr.getValue();

        java.util.Set<Node> mocks = new HashSet<Node>();
        @SuppressWarnings("unchecked")
        List<Node> siblings = (List<Node>) DOMUtils.parent(elem).content();
        if (mockType.equals("siblings")) {
            mocks.addAll(siblings);
        } else if (mockType.equals("neighbors")) {
            mocks.addAll(siblings);
            mocks.remove(elem);
        } else if (mockType.equals("below")) {
            mocks.addAll(siblings.subList(siblings.indexOf(elem) + 1, siblings.size()));
        } else if (mockType.equals("toend")) {
            mocks.addAll(siblings.subList(siblings.indexOf(elem), siblings.size()));
        } else {
            mocks.add(elem);
        }

        for (Node mock : mocks) {
            mock.detach();
        }

        attr.detach();
    }
}
