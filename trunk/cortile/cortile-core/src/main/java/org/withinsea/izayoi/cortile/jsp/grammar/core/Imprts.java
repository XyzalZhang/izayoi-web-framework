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
import org.dom4j.Branch;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.GrammarUtils;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.BranchGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.CommentGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.util.DOMUtils;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:57:38
 */
public class Imprts implements AttrGrammar, BranchGrammar, CommentGrammar {

    public static final String IMPORTS_CLASSES_ATTR_NAME = Imprts.class.getCanonicalName() + ".IMPORT_CLASSES";

    public static void imports(Compilr.Result result, String imports) {
        imports = imports.trim().replaceAll("[\\s;,]+", ",").replaceAll("^\\s*,?|,?\\s*$", "").trim();
        String imported = getImports(result);
        for (String imp : imports.trim().split("[\\s;,]+")) {
            if (!imp.trim().equals("") && imported.indexOf(imp) < 0) {
                imported += ", " + imp;
            }
        }
        result.setAttribute(IMPORTS_CLASSES_ATTR_NAME, imported.replaceAll("^,\\s*", ""));
    }

    public static String getImports(Compilr.Result result) {
        String imports = result.getAttribute(IMPORTS_CLASSES_ATTR_NAME);
        return (imports == null) ? "" : imports;
    }

    @Override
    public boolean acceptBranch(Branch branch) {
        return true;
    }

    @Override
    @Priority(99)
    public void processBranch(DOMCompiler compiler, Compilr.Result result, Branch branch) throws CortileException {
        try {
            String imports = getImports(result);
            if (!imports.equals("")) {
                DOMUtils.prepend(branch, "<%@ page import=\"" + imports + "\" %>");
            }
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }

    @Override
    public boolean acceptComment(Comment comment) {
        return GrammarUtils.Accept.byTextPrefix(comment, "@imports");
    }

    @Override
    @Priority(99)
    public void processComment(DOMCompiler compiler, Compilr.Result result, Comment comment) {
        imports(result, comment.getText().substring("@imports".length()));
        comment.detach();
    }

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return GrammarUtils.Accept.byName(attr, "imports");
    }

    @Override
    @Priority(99)
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) {
        imports(result, attr.getValue());
        attr.detach();
    }
}
