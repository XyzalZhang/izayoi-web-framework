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

package org.withinsea.izayoi.cortile.core.compile.dom;

import org.dom4j.*;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.Compilr;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 4:06:20
 */
public abstract class DOMCompiler implements Compilr {

    protected abstract Document parseTemplate(String templatePath, String templateCode) throws CortileException;

    protected abstract String mapTargetPath(String path, String suffix);

    protected abstract String buildTarget(Branch root) throws CortileException;

    protected abstract Map<String, List<Grammar>> getGrammars();

    protected List<PretreatGrammar> pretreatGrammars;
    protected List<RoundoffGrammar> roundoffGrammars;
    protected List<TextGrammar> textGrammars;
    protected List<CommentGrammar> commentGrammars;
    protected List<ElementGrammar> elementGrammars;
    protected Map<Integer, Map<String, List<AttrGrammar>>> attrGrammars;

    protected boolean classified = false;

    protected synchronized void classifyGrammars() {
        if (!classified) {
            Map<String, List<Grammar>> grammars = getGrammars();
            List<Grammar> allGrammars = new ArrayList<Grammar>();
            for (Map.Entry<String, List<Grammar>> entry : grammars.entrySet()) {
                allGrammars.addAll(entry.getValue());
            }
            pretreatGrammars = GrammarUtils.sort(allGrammars, PretreatGrammar.class, "pretreatCode");
            roundoffGrammars = GrammarUtils.sort(allGrammars, RoundoffGrammar.class, "roundoffCode");
            textGrammars = GrammarUtils.sort(allGrammars, TextGrammar.class, "processText");
            commentGrammars = GrammarUtils.sort(allGrammars, CommentGrammar.class, "processComment");
            elementGrammars = GrammarUtils.sort(allGrammars, ElementGrammar.class, "processElement");
            attrGrammars = GrammarUtils.group(grammars);
            classified = false;
        }
    }

    @Override
    public Result compile(String templatePath, String templateCode) throws CortileException {

        Result result = new Result();

        CompileContext ctx = CompileContext.open(this, templatePath, result);
        {
            classifyGrammars();

            for (PretreatGrammar pg : pretreatGrammars) {
                if (pg.acceptPretreat(templateCode)) {
                    templateCode = pg.pretreatCode(templateCode);
                }
            }

            Document doc = parseTemplate(templatePath, templateCode);
            DOMUtils.mergeTexts(doc);
            compile(doc);

            ctx.getResult().getTargets().put(mapTargetPath(templatePath, null), buildTarget(doc));

            for (Map.Entry<String, String> e : ctx.getResult().getTargets().entrySet()) {
                for (RoundoffGrammar rg : roundoffGrammars) {
                    if (rg.acceptRoundoff(e.getValue())) {
                        ctx.getResult().getTargets().put(e.getKey(), rg.roundoffCode(e.getValue()));
                    }
                }
            }
        }
        CompileContext.close();

        return result;
    }

    @SuppressWarnings("unchecked")
    protected void compile(Node node) throws CortileException {

        CompileContext ctx = CompileContext.get();

        if (node instanceof Text) {

            Text text = (Text) node;
            for (TextGrammar tg : textGrammars) {
                if (isDetached(text)) return;
                if (tg.acceptText(text)) {
                    tg.processText(text);
                }
            }

        } else if (node instanceof Comment) {

            Comment comment = (Comment) node;
            for (CommentGrammar cg : commentGrammars) {
                if (isDetached(comment)) return;
                if (cg.acceptComment(comment)) {
                    cg.processComment(comment);
                }
            }

        } else if (node instanceof Branch) {

            ctx.openScope();

            inScope:
            {
                if (node instanceof Element) {

                    Element element = (Element) node;
                    for (ElementGrammar eg : elementGrammars) {
                        if (isDetached(element)) break inScope;
                        if (eg.acceptElement(element)) {
                            eg.processElement(element);
                        }
                    }

                    for (Map.Entry<Integer, Map<String, List<AttrGrammar>>> entry : attrGrammars.entrySet()) {
                        Map<String, List<AttrGrammar>> ags = entry.getValue();
                        List<Attribute> attrs = new ArrayList<Attribute>((List<Attribute>) element.attributes());
                        for (Attribute attr : attrs) {
                            if (isDetached(element)) break inScope;
                            if (isDetached(attr)) continue;
                            Set<AttrGrammar> nsAgs = new LinkedHashSet<AttrGrammar>();
                            {
                                nsAgs.addAll(ags.get("*"));
                                nsAgs.addAll(ags.get(attr.getNamespacePrefix()));
                            }
                            for (AttrGrammar ag : nsAgs) {
                                if (isDetached(element)) break inScope;
                                if (isDetached(attr)) break;
                                if (ag.acceptAttr(attr)) {
                                    ag.processAttr(attr);
                                }
                            }
                        }
                    }
                }

                Branch branch = (Branch) node;
                List<Node> cnodes = new ArrayList<Node>((List<Node>) branch.content());
                for (Node cnode : cnodes) {
                    if (isDetached(branch)) break inScope;
                    if (isDetached(cnode)) continue;
                    compile(cnode);
                }
            }

            ctx.closeScope();
        }
    }

    protected boolean isDetached(Node node) {
        return (node.getParent() == null && node.getDocument() == null);
    }
}