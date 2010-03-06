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
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.cortile.core.compiler.grammar.Grammar;
import org.withinsea.izayoi.cortile.core.compiler.grammar.GrammarCompiler;
import org.withinsea.izayoi.cortile.core.compiler.grammar.GrammarUtils;
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
public abstract class DOMCompiler implements GrammarCompiler {

    // grammar

    protected Map<String, Set<Grammar>> grammars;

    @Override
    public void setGrammars(Map<String, Set<Grammar>> grammars) {
        this.grammars = grammars;
    }

    // dom

    protected abstract Document parseTemplate(String templatePath, String templateCode) throws CortileException;

    protected abstract String buildTarget(Branch root) throws CortileException;

    public abstract String mapTargetPath(String path, String suffix);

    @Override
    @SuppressWarnings("unchecked")
    public Result compile(String templatePath, String templateCode) throws CortileException {

        Result result = new Result(templatePath);

        for (PretreatGrammar pg : GrammarUtils.sort(grammars, PretreatGrammar.class, "pretreatCode")) {
            if (pg.acceptPretreat(templateCode)) {
                try {
                    templateCode = pg.pretreatCode(this, result, templateCode);
                } catch (ClassCastException e) { /* ignore */ }
            }
        }

        Document doc = parseTemplate(templatePath, templateCode);

        DOMUtils.mergeTexts(doc);

        for (Map.Entry<Integer, List<TextGrammar>> groups :
                GrammarUtils.sortAsGroups(grammars, TextGrammar.class, "processText").entrySet()) {
            for (Text text : DOMUtils.selectTypedNodes(Text.class, doc, false)) {
                for (TextGrammar tg : groups.getValue()) {
                    if (tg.acceptText(text)) {
                        try {
                            tg.processText(this, result, text);
                        } catch (ClassCastException cce) { /* ignore */ }
                    }
                    if (text.getParent() == null && text.getDocument() == null) {
                        break;
                    }
                }
            }
        }

        for (Map.Entry<Integer, List<CommentGrammar>> groups :
                GrammarUtils.sortAsGroups(grammars, CommentGrammar.class, "processComment").entrySet()) {
            for (Comment comment : DOMUtils.selectTypedNodes(Comment.class, doc, false)) {
                if (comment.getParent() != null || comment.getDocument() != null) {
                    for (CommentGrammar cg : groups.getValue()) {
                        if (cg.acceptComment(comment)) {
                            try {
                                cg.processComment(this, result, comment);
                            } catch (ClassCastException cce) { /* ignore */ }
                        }
                        if (comment.getParent() == null && comment.getDocument() == null) {
                            break;
                        }
                    }
                }
            }
        }

        compileTo(result, mapTargetPath(result.getTemplatePath(), null), doc);

        List<RoundoffGrammar> sortedRgs = GrammarUtils.sort(grammars, RoundoffGrammar.class, "roundoffGrammar");
        for (Map.Entry<String, String> e : result.getTargets().entrySet()) {
            for (RoundoffGrammar rg : sortedRgs) {
                if (rg.acceptRoundoff(e.getValue())) {
                    try {
                        result.getTargets().put(e.getKey(), rg.roundoffCode(this, result, e.getValue()));
                    } catch (ClassCastException cce) { /* ignore */ }
                }
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    public void compileTo(Result result, String targetPath, Branch root) throws CortileException {

        for (Map.Entry<Integer, List<GrammarUtils.NamespacedWrapper<AttrGrammar>>> group :
                GrammarUtils.sortAsNamespacedGroups(grammars, AttrGrammar.class, "processAttr").entrySet()) {
            for (Element elem : DOMUtils.selectTypedNodes(Element.class, root, false)) {
                if (elem.getParent() != null || elem.getDocument() != null) {
                    eachAttr:
                    for (Attribute attr : new ArrayList<Attribute>((List<Attribute>) elem.attributes())) {
                        if (attr.getParent() != null) {
                            String attrNs = attr.getNamespacePrefix();
                            for (GrammarUtils.NamespacedWrapper<AttrGrammar> w : group.getValue()) {
                                String agNs = w.getNamespace();
                                AttrGrammar ag = w.getGrammar();
                                if (agNs.equals("") || agNs.equals(attrNs)) {
                                    if (ag.acceptAttr(elem, attr)) {
                                        try {
                                            ag.processAttr(this, result, elem, attr);
                                        } catch (ClassCastException cce) { /* ignore */ }
                                    }
                                    if (elem.getParent() == null && elem.getDocument() == null) {
                                        break eachAttr;
                                    }
                                    if (attr.getParent() == null) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Map.Entry<Integer, List<ElementGrammar>> groups :
                GrammarUtils.sortAsGroups(grammars, ElementGrammar.class, "processElement").entrySet()) {
            for (Element elem : DOMUtils.selectTypedNodes(Element.class, root, false)) {
                if (elem.getParent() != null || elem.getDocument() != null) {
                    for (ElementGrammar eg : groups.getValue()) {
                        if (eg.acceptElement(elem)) {
                            try {
                                eg.processElement(this, result, elem);
                            } catch (ClassCastException cce) { /* ignore */ }
                        }
                        if (elem.getParent() == null && elem.getDocument() == null) {
                            break;
                        }
                    }
                }
            }
        }

        for (BranchGrammar bg : GrammarUtils.sort(grammars, BranchGrammar.class, "processBranch")) {
            if (bg.acceptBranch(root)) {
                try {
                    bg.processBranch(this, result, root);
                } catch (ClassCastException cce) { /* ignore */ }
            }
        }

        result.getTargets().put(targetPath, buildTarget(root));
    }
}