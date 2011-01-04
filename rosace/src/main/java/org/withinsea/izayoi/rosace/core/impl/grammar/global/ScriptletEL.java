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

import org.dom4j.Comment;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.CommentGrammar;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 14:19:20
 */
public class ScriptletEL implements CommentGrammar {

    @Override
    public boolean acceptComment(Comment comment) {
        return comment.getText().startsWith("$");
    }

    @Override
    public void processComment(Comment comment) throws RosaceException {
        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();
        try {
            if (comment.getText().startsWith("$=")) {
                DomUtils.replaceBy(comment, "<%=" + engine.precompileEl(comment.getText().substring("$=".length())) + "%>");
            } else if (comment.getText().startsWith("$")) {
                DomUtils.replaceBy(comment, "<%" + engine.precompileEl(comment.getText().substring("$".length())) + ";%>");
            }
        } catch (Exception e) {
            throw new RosaceException(e);
        }
    }
}