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

package org.withinsea.izayoi.cortile.jsp.grammar.el;

import org.dom4j.Attribute;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Text;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.core.compiler.GrammarUtils;
import org.withinsea.izayoi.cortile.core.compiler.StringGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.*;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.util.DOMUtils;
import org.withinsea.izayoi.cortile.util.StringUtils;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 14:19:20
 */
public class EL implements PretreatGrammar, AttrGrammar, CommentGrammar, TextGrammar, StringGrammar {

    protected ELInterpreter elInterpreter;

    @Override
    public boolean acceptString(String str) {
        return true;
    }

    @Override
    public String processString(Compilr compiler, Compilr.Result result, String str) throws CortileException {
        return replaceOutputELs(str);
    }

    @Override
    public boolean acceptPretreat(String code) {
        return true;
    }

    @Override
    public String pretreatCode(DOMCompiler compiler, Compilr.Result result, String code) throws CortileException {
        code = StringUtils.replaceAll(code, "\\$\\{(.*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "${" + groups[1].replace("<", "&lt;").replace(">", "&gt;") + "}";
            }
        });
        code = StringUtils.replaceAll(code, "\"(.+?)\"", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"" + groups[1].replace("<", "&lt;").replace(">", "&gt;") + "\"";
            }
        });
        return code;
    }

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return attr.getValue().indexOf("${") >= 0;
    }

    @Override
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {
        attr.setValue(replaceOutputELs(attr.getValue()));
    }

    @Override
    public boolean acceptText(Text text) {
        return true;
    }

    @Override
    public void processText(DOMCompiler compiler, Compilr.Result result, Text text) throws CortileException {
        text.setText(replaceOutputELs(text.getText()));
    }

    @Override
    public boolean acceptComment(Comment comment) {
        return true;
    }

    @Override
    public void processComment(DOMCompiler compiler, Compilr.Result result, Comment comment) throws CortileException {
        try {
            if (GrammarUtils.Accept.byTextPrefix(comment, "$=")) {
                DOMUtils.replace(comment, compileOutputEL(comment.getText().substring("$=".length())));
            } else if (GrammarUtils.Accept.byTextPrefix(comment, "$")) {
                DOMUtils.replace(comment, compileExecuteEL(comment.getText().substring("$".length())));
            } else {
                comment.setText(replaceOutputELs(comment.getText()));
            }
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }

    protected String compileExecuteEL(String el) {
        return "<% " + elInterpreter.compileEL(el.replace("\n", "").replace("\r", "")) + "; %>";
    }

    protected String compileOutputEL(String el) {
        return "<%= " + elInterpreter.compileEL(el.replace("\n", "").replace("\r", "")) + " %>";
    }

    protected String replaceOutputELs(String text) {
        return StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return compileOutputEL(groups[1].replace("\\}", "}"));
            }
        });
    }

    @SuppressWarnings("unused")
    public void setElInterpreter(ELInterpreter elInterpreter) {
        this.elInterpreter = elInterpreter;
    }
}