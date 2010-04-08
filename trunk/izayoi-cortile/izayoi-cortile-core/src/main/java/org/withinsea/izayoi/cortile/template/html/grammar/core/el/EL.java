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

package org.withinsea.izayoi.cortile.template.html.grammar.core.el;

import org.dom4j.Attribute;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Text;
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.CommentGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.TextGrammar;
import org.withinsea.izayoi.cortile.core.compiler.el.ELSupportedCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 14:19:20
 */
public class EL implements AttrGrammar<HTMLCompiler>, CommentGrammar<HTMLCompiler>, TextGrammar<HTMLCompiler> {

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return attr.getValue().indexOf("${") >= 0;
    }

    @Override
    @Priority(-50)
    public void processAttr(HTMLCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {
        attr.setValue(compileEmbeddedELs(compiler, attr.getValue()));
    }

    @Override
    public boolean acceptText(Text text) {
        return true;
    }

    @Override
    @Priority(-50)
    public void processText(HTMLCompiler compiler, Compilr.Result result, Text text) throws CortileException {
        text.setText(compileEmbeddedELs(compiler, text.getText()));
    }

    @Override
    public boolean acceptComment(Comment comment) {
        return true;
    }

    @Override
    @Priority(-50)
    public void processComment(HTMLCompiler compiler, Compilr.Result result, Comment comment) throws CortileException {
        comment.setText(compileEmbeddedELs(compiler, comment.getText()));
    }

    protected String compileEmbeddedELs(final ELSupportedCompiler compiler, String text) {
        return StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            @Override
            public String replace(String... groups) {
                return "<%=" + compiler.el(groups[1].replace("\\}", "}"), true) + "%>";
            }
        });
    }
}