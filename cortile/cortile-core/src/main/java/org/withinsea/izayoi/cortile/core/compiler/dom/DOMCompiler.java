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

package org.withinsea.izayoi.cortile.core.compiler.dom;

import org.dom4j.*;
import org.withinsea.izayoi.commons.html.DOMUtils;
import org.withinsea.izayoi.cortile.core.compiler.CompilerUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.Grammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 4:06:20
 */
public abstract class DOMCompiler implements Compilr {

    protected abstract Document parseTemplate(String templatePath, String templateCode) throws CortileException;

    protected abstract String buildTarget(Branch root) throws CortileException;

    public abstract String mapTargetPath(String path, String suffix);

    public String mapTargetPath(String path) {
        return mapTargetPath(path, null);
    }

    protected Map<String, Set<Grammar>> grammars;

    @Override
    public Result compile(String templatePath, String templateCode) throws CortileException {

        Result result = new Result(templatePath);

        for (PretreatGrammar pg : CompilerUtils.sortAll(grammars, PretreatGrammar.class, "pretreatCode")) {
            if (pg.acceptPretreat(templateCode)) {
                templateCode = pg.pretreatCode(this, result, templateCode);
            }
        }

        Document doc = parseTemplate(templatePath, templateCode);

        DOMUtils.mergeTexts(doc);

        for (Map.Entry<Integer, List<TextGrammar>> groups : CompilerUtils.sortAllAsPriorityGroups(
                grammars, TextGrammar.class, "processText").entrySet()) {
            for (Text text : DOMUtils.selectTypedNodes(Text.class, doc, false)) {
                for (TextGrammar tg : groups.getValue()) {
                    if (tg.acceptText(text)) {
                        tg.processText(this, result, text);
                    }
                    if (text.getParent() == null && text.getDocument() == null) {
                        break;
                    }
                }
            }
        }

        compileTo(result, mapTargetPath(result.getTemplatePath()), doc);

        List<RoundoffGrammar> sortedRgs = CompilerUtils.sortAll(grammars, RoundoffGrammar.class, "roundoffGrammar");
        for (Map.Entry<String, String> e : result.getTargets().entrySet()) {
            for (RoundoffGrammar rg : sortedRgs) {
                if (rg.acceptRoundoff(e.getValue())) {
                    result.getTargets().put(e.getKey(), rg.roundoffCode(this, result, e.getValue()));
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public void compileTo(Result result, String targetPath, Branch root) throws CortileException {

        for (Map.Entry<Integer, List<CommentGrammar>> groups : CompilerUtils.sortAllAsPriorityGroups(
                grammars, CommentGrammar.class, "processComment").entrySet()) {
            for (Comment comment : DOMUtils.selectTypedNodes(Comment.class, root, false)) {
                if (comment.getParent() != null || comment.getDocument() != null) {
                    for (CommentGrammar cg : groups.getValue()) {
                        if (cg.acceptComment(comment)) {
                            cg.processComment(this, result, comment);
                        }
                        if (comment.getParent() == null && comment.getDocument() == null) {
                            break;
                        }
                    }
                }
            }
        }

        for (Map.Entry<Integer, Map<String, List<AttrGrammar>>> groups : CompilerUtils.sortAsPriorityGroups(
                grammars, AttrGrammar.class, "processAttr").entrySet()) {
            for (Element elem : DOMUtils.selectTypedNodes(Element.class, root, false)) {
                if (elem.getParent() != null || elem.getDocument() != null) {
                    eachAttr:
                    for (Attribute attr : new ArrayList<Attribute>((List<Attribute>) elem.attributes())) {
                        if (attr.getParent() != null) {
                            String attrNs = attr.getNamespacePrefix();
                            eachGrammar:
                            for (Map.Entry<String, List<AttrGrammar>> e : groups.getValue().entrySet()) {
                                if (e.getKey().equals("") || e.getKey().equals(attrNs)) {
                                    for (AttrGrammar ag : e.getValue()) {
                                        if (ag.acceptAttr(elem, attr)) {
                                            ag.processAttr(this, result, elem, attr);
                                        }
                                        if (elem.getParent() == null && elem.getDocument() == null) {
                                            break eachAttr;
                                        }
                                        if (attr.getParent() == null) {
                                            break eachGrammar;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<Integer, List<ElementGrammar>> groups : CompilerUtils.sortAllAsPriorityGroups(
                grammars, ElementGrammar.class, "processElement").entrySet()) {
            for (Element elem : DOMUtils.selectTypedNodes(Element.class, root, false)) {
                if (elem.getParent() != null || elem.getDocument() != null) {
                    for (ElementGrammar eg : groups.getValue()) {
                        if (eg.acceptElement(elem)) {
                            eg.processElement(this, result, elem);
                        }
                        if (elem.getParent() == null && elem.getDocument() == null) {
                            break;
                        }
                    }
                }
            }
        }

        for (BranchGrammar bg : CompilerUtils.sortAll(grammars, BranchGrammar.class, "processBranch")) {
            if (bg.acceptBranch(root)) {
                bg.processBranch(this, result, root);
            }
        }

        result.getTargets().put(targetPath, buildTarget(root));
    }

    @SuppressWarnings("unused")
    public void setGrammars(Map<String, Set<Grammar>> grammars) {
        this.grammars = grammars;
    }
}