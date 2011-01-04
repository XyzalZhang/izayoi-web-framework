package org.withinsea.izayoi.rosace.core.impl.template.dom;

import org.dom4j.*;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.HostlangIntermediatelyEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.*;
import org.withinsea.izayoi.rosace.core.impl.template.dom.parser.DomReader;
import org.withinsea.izayoi.rosace.core.impl.template.dom.parser.DomWriter;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 21:27:24
 */
public class DomTemplateEngine extends HostlangIntermediatelyEngine {

    @Override
    public String getName() {
        return "IzayoiRosace.Dom";
    }

    protected Collection<Grammar> globalGrammars = Collections.emptySet();
    protected Map<String, ? extends Collection<Grammar>> namespacedGrammars = Collections.emptyMap();

    protected boolean classified = false;
    protected List<PretreatGrammar> pretreatGrammars = Collections.emptyList();
    protected List<RoundoffGrammar> roundoffGrammars = Collections.emptyList();
    protected List<TextGrammar> textGrammars = Collections.emptyList();
    protected List<CommentGrammar> commentGrammars = Collections.emptyList();
    protected List<ElementGrammar> elementGrammars = Collections.emptyList();
    protected Map<Integer, Map<String, List<AttrGrammar>>> attrGrammars = Collections.emptyMap();

    protected void classify() {
        if (!classified) {
            List<Grammar> allGrammars = new ArrayList<Grammar>();
            allGrammars.addAll(globalGrammars);
            for (Map.Entry<String, ? extends Collection<Grammar>> entry : namespacedGrammars.entrySet()) {
                allGrammars.addAll(entry.getValue());
            }
            pretreatGrammars = GrammarUtils.sort(allGrammars, PretreatGrammar.class, "pretreatCode");
            roundoffGrammars = GrammarUtils.sort(allGrammars, RoundoffGrammar.class, "roundoffCode");
            textGrammars = GrammarUtils.sort(allGrammars, TextGrammar.class, "processText");
            commentGrammars = GrammarUtils.sort(allGrammars, CommentGrammar.class, "processComment");
            elementGrammars = GrammarUtils.sort(allGrammars, ElementGrammar.class, "processElement");
            attrGrammars = GrammarUtils.group(namespacedGrammars);
            classified = true;
        }
    }

    public String precompileTemplateToHostlang(String template) throws RosaceException {

        classify();

        String jsp = "";
        {
            for (PretreatGrammar pg : pretreatGrammars) {
                if (pg.acceptPretreat(template)) {
                    template = pg.pretreatCode(template);
                }
            }

            Document doc = null;
            try {
                doc = new DomReader().read(new StringReader(template));
            } catch (DocumentException e) {
                throw new RosaceException("failed in parsing template, not a valid dom text.", e);
            } catch (SAXException e) {
                throw new RosaceException("failed in parsing template, not a valid dom text.", e);
            }

            DomUtils.mergeTexts(doc);
            compile(doc);

            StringWriter buf = new StringWriter();
            try {
                new DomWriter(buf).write(doc);
            } catch (IOException e) {
                throw new RosaceException("failed in writing dom text.", e);
            }
            jsp = buf.toString();

            for (RoundoffGrammar rg : roundoffGrammars) {
                if (rg.acceptRoundoff(jsp)) {
                    jsp = rg.roundoffCode(jsp);
                }
            }
        }

        return jsp;
    }

    @SuppressWarnings("unchecked")
    protected void compile(Node node) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();

        if (node instanceof Text) {

            Text text = (Text) node;
            for (TextGrammar tg : textGrammars) {
                if (DomUtils.isDetached(text)) return;
                if (tg.acceptText(text)) {
                    tg.processText(text);
                }
            }

        } else if (node instanceof Comment) {

            Comment comment = (Comment) node;
            for (CommentGrammar cg : commentGrammars) {
                if (DomUtils.isDetached(comment)) return;
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
                        if (DomUtils.isDetached(element)) break inScope;
                        if (eg.acceptElement(element)) {
                            eg.processElement(element);
                        }
                    }

                    for (Map.Entry<Integer, Map<String, List<AttrGrammar>>> entry : attrGrammars.entrySet()) {
                        Map<String, List<AttrGrammar>> ags = entry.getValue();
                        List<Attribute> attrs = new ArrayList<Attribute>((List<Attribute>) element.attributes());
                        for (Attribute attr : attrs) {
                            if (DomUtils.isDetached(element)) break inScope;
                            if (DomUtils.isDetached(attr)) continue;
                            Set<AttrGrammar> nsAgs = new LinkedHashSet<AttrGrammar>();
                            {
                                nsAgs.addAll(ags.get("*"));
                                nsAgs.addAll(ags.get(attr.getNamespacePrefix()));
                            }
                            for (AttrGrammar ag : nsAgs) {
                                if (DomUtils.isDetached(element)) break inScope;
                                if (DomUtils.isDetached(attr)) break;
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
                    if (DomUtils.isDetached(branch)) break inScope;
                    if (DomUtils.isDetached(cnode)) continue;
                    compile(cnode);
                }
            }

            ctx.closeScope();
        }
    }

    public Map<String, ? extends Collection<Grammar>> getNamespacedGrammars() {
        return namespacedGrammars;
    }

    public void setNamespacedGrammars(Map<String, ? extends Collection<Grammar>> namespacedGrammars) {
        classified = false;
        this.namespacedGrammars = namespacedGrammars;
    }

    public Collection<Grammar> getGlobalGrammars() {
        return globalGrammars;
    }

    public void setGlobalGrammars(Collection<Grammar> globalGrammars) {
        classified = false;
        this.globalGrammars = globalGrammars;
    }
}
