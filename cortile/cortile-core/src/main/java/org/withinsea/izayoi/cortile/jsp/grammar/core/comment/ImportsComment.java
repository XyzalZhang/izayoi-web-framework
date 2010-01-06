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
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.core.compiler.dom.CommentGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.compiler.dom.RoundoffGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:57:38
 */
public class ImportsComment implements RoundoffGrammar, CommentGrammar {

    protected static final String IMPORTS_ATTR_NAME = ImportsComment.class.getCanonicalName() + "_IMPORTS";

    protected ELInterpreter elInterpreter;

    @Override
    public boolean acceptComment(Comment comment) {
        return comment.getText().startsWith("@imports");
    }

    @Override
    @Priority(99)
    public void processComment(DOMCompiler compiler, Compilr.Result result, Comment comment) throws CortileException {
        String imports = result.getAttribute(IMPORTS_ATTR_NAME);
        imports = (imports == null ? "" : imports + ",") + comment.getText().substring("@imports".length())
                .trim().replaceAll("[\\s;,]+", ",").replaceAll("^\\s*,?|,?\\s*$", "");
        result.setAttribute(IMPORTS_ATTR_NAME, imports);
    }

    @Override
    public boolean acceptRoundoff(String code) {
        return true;
    }

    @Override
    public String roundoffCode(DOMCompiler compiler, Compilr.Result result, String code) throws CortileException {
        String imports = result.getAttribute(IMPORTS_ATTR_NAME);
        try {
            if (imports == null || imports.equals("")) {
                String elInitScriptlet = elInterpreter.compileInit("");
                return "<%" + elInitScriptlet + "%>" + code;
            } else {
                String elInitScriptlet = elInterpreter.compileInit("import " + imports.replace(",", "; import ") + ";");
                return "<%@ page import=\"" + imports + "\"%><%" + elInitScriptlet + "%>" + code;
            }
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }

    @SuppressWarnings("unused")
    public void setElInterpreter(ELInterpreter elInterpreter) {
        this.elInterpreter = elInterpreter;
    }
}