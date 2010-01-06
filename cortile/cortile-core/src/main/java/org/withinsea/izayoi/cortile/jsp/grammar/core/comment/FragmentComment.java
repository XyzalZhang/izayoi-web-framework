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

package org.withinsea.izayoi.cortile.jsp.grammar.core.comment;

import org.dom4j.Comment;
import org.withinsea.izayoi.commons.xml.DOM4JUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.CommentGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:02:01
 */
public class FragmentComment implements CommentGrammar {

    @Override
    public boolean acceptComment(Comment comment) {
        return comment.getText().startsWith("=");
    }

    @Override
    @Priority(-50)
    public void processComment(DOMCompiler compiler, Compilr.Result result, Comment comment) throws CortileException {
        try {
            DOM4JUtils.replaceBy(comment, comment.getText().substring("=".length()));
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }
}
