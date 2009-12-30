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
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Text;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 21:24:16
 */
public class EntityTrick implements PretreatGrammar, TextGrammar, AttrGrammar, CommentGrammar {

    public static final String ENTITY_TRICK_ATTR_NAME = EntityTrick.class.getCanonicalName() + ".ENTITY_TRICK";

    @Override
    public boolean acceptPretreat(String code) {
        return true;
    }

    @Override
    @Priority(99)
    public String pretreatCode(DOMCompiler compiler, Compilr.Result result, String code) {
        String trick = "＆";
        while (code.indexOf(trick) >= 0) {
            trick += "＆";
        }
        trick = "_" + trick + "_";
        result.setAttribute(ENTITY_TRICK_ATTR_NAME, trick);
        return code.replace("&", trick);
    }

    @Override
    public boolean acceptText(Text text) {
        return true;
    }

    @Override
    @Priority(99)
    public void processText(DOMCompiler compiler, Compilr.Result result, Text text) {
        String trick = result.getAttribute(ENTITY_TRICK_ATTR_NAME);
        text.setText(text.getText().replace(trick, "&"));
    }

    @Override
    public boolean acceptComment(Comment comment) {
        return true;
    }

    @Override
    @Priority(99)
    public void processComment(DOMCompiler compiler, Compilr.Result result, Comment comment) {
        String trick = result.getAttribute(ENTITY_TRICK_ATTR_NAME);
        comment.setText(comment.getText().replace(trick, "&"));
    }

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return true;
    }

    @Override
    @Priority(99)
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) {
        String trick = result.getAttribute(ENTITY_TRICK_ATTR_NAME);
        attr.setValue(attr.getValue().replace(trick, "&"));
    }
}
