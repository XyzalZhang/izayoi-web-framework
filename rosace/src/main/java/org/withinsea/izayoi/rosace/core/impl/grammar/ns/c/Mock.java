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
import org.dom4j.Node;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;

import java.util.ArrayList;
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
    public void processAttr(Attribute attr) throws RosaceException {

        Element elem = attr.getParent();

        String mockType = attr.getValue().trim();

        @SuppressWarnings("unchecked")
        List<Node> siblings = (List<Node>) DomUtils.parent(elem).content();
        int idx = siblings.indexOf(elem);

        java.util.Set<Node> mocks = new HashSet<Node>();

        if (mockType.equals("siblings")) {
            mocks.addAll(siblings);
        } else if (mockType.equals("neighbors")) {
            mocks.addAll(siblings);
            mocks.remove(elem);
        } else if (mockType.equals("below")) {
            mocks.addAll(siblings.subList(idx + 1, siblings.size()));
        } else if (mockType.equals("toend")) {
            mocks.addAll(siblings.subList(idx, siblings.size()));
        } else if (mockType.matches("^[+-]?\\d+$")) {
            int mockSize = Integer.parseInt(mockType);
            if (mockSize > 0) {
                for (Node node : siblings.subList(idx, siblings.size())) {
                    mocks.add(node);
                    if (node instanceof Element) mockSize--;
                    if (mockSize == 0) break;
                }
            } else {
                List<Node> toend = new ArrayList<Node>(siblings.subList(idx, siblings.size()));
                for (int i = toend.size() - 1; i >= 0; i--) {
                    Node node = toend.remove(i);
                    if (node instanceof Element) mockSize++;
                    if (mockSize == 0) break;
                }
                mocks.addAll(toend);
            }
        } else {
            mocks.add(elem);
        }

        for (Node mock : mocks) {
            mock.detach();
        }

        attr.detach();
    }
}
