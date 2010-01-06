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

package org.withinsea.izayoi.cortile.jsp.grammar.core.el;

import org.dom4j.Attribute;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Text;
import org.withinsea.izayoi.commons.lang.StringUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.core.compiler.StringGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.CommentGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.compiler.dom.TextGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 14:19:20
 */
public class EL implements AttrGrammar, CommentGrammar, TextGrammar, StringGrammar {

    protected ELInterpreter elInterpreter;

    @Override
    public boolean acceptString(String str) {
        return true;
    }

    @Override
    public String processString(Compilr compiler, Compilr.Result result, String str) throws CortileException {
        return compileEmbeddedELs(str);
    }

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return attr.getValue().indexOf("${") >= 0;
    }

    @Override
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {
        attr.setValue(compileEmbeddedELs(attr.getValue()));
    }

    @Override
    public boolean acceptText(Text text) {
        return true;
    }

    @Override
    public void processText(DOMCompiler compiler, Compilr.Result result, Text text) throws CortileException {
        text.setText(compileEmbeddedELs(text.getText()));
    }

    @Override
    public boolean acceptComment(Comment comment) {
        return true;
    }

    @Override
    public void processComment(DOMCompiler compiler, Compilr.Result result, Comment comment) throws CortileException {
        comment.setText(compileEmbeddedELs(comment.getText()));
    }

    protected String compileEmbeddedELs(String text) {
        return StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "<%=" + elInterpreter.compileEL(groups[1].replace("\\}", "}")) + "%>";
            }
        });
    }

    @SuppressWarnings("unused")
    public void setElInterpreter(ELInterpreter elInterpreter) {
        this.elInterpreter = elInterpreter;
    }
}