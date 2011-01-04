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

package org.withinsea.izayoi.rosace.core.impl.grammar.global;

import org.dom4j.Attribute;
import org.dom4j.Comment;
import org.dom4j.Text;
import org.withinsea.izayoi.common.util.StringUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.CommentGrammar;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.TextGrammar;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 14:19:20
 */
public class EmbeddedEL implements AttrGrammar, CommentGrammar, TextGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getValue().indexOf("${") >= 0;
    }

    @Override
    @Priority(Priority.LOW)
    public void processAttr(Attribute attr) throws RosaceException {
        attr.setValue(precompileEmbeddedELs(attr.getValue()));
    }

    @Override
    public boolean acceptText(Text text) {
        return true;
    }

    @Override
    @Priority(Priority.LOW)
    public void processText(Text text) throws RosaceException {
        text.setText(precompileEmbeddedELs(text.getText()));
    }

    @Override
    public boolean acceptComment(Comment comment) {
        return true;
    }

    @Override
    @Priority(Priority.LOW)
    public void processComment(Comment comment) throws RosaceException {
        comment.setText(precompileEmbeddedELs(comment.getText()));
    }

    protected String precompileEmbeddedELs(String text) {
        if (text.indexOf("${") < 0) {
            return text;
        } else {
            final PrecompiletimeContext ctx = PrecompiletimeContext.get();
            final DomTemplateEngine engine = ctx.getEngine();
            return StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
                @Override
                public String replace(String... groups) {
                    return "<%=" + engine.precompileEl(groups[1].replace("\\}", "}"), true) + "%>";
                }
            });
        }
    }
}