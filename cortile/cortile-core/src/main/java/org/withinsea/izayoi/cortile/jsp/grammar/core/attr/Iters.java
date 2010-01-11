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

package org.withinsea.izayoi.cortile.jsp.grammar.core.attr;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.html.DOMUtils;
import org.withinsea.izayoi.commons.util.BeanMap;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:14:05
 */
public class Iters implements AttrGrammar {

    protected ELInterpreter elInterpreter;

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        String attrname = attr.getName().replaceAll("[:_-]", ".");
        return attrname.matches("while|until|^(while|until|for)\\.[\\w\\.]+");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {

        String attrname = attr.getName().replaceAll("[:_-]", ".");

        String el = attr.getValue().trim();
        el = (el.startsWith("${") && el.endsWith("}")) ? el.substring(2, el.length() - 1).trim() : el;

        if (!el.equals("")) {

            String preScriptlet = "{ varstack.push();";
            String helperScriptlet = "";
            String sufScriptlet = "varstack.pop(); }";

            String type = attrname.startsWith("while") ? "while" :
                    attrname.startsWith("until") ? "until" : "for";
            String subname = attrname.substring(type.length());
            String subtype = subname.startsWith(".content") ? ".content" :
                    subname.startsWith(".status") ? ".status" : "";
            String i = subname.substring(subtype.length()).replaceFirst("\\.", "");

            if (subtype.equals(".status")) {
                return;
            }

            if (i != null) {
                for (Attribute statusAttr : (List<Attribute>) elem.attributes()) {
                    String aname = statusAttr.getName().replaceAll("[:_-]", ".");
                    if (aname.equals(type + ".status." + i)) {
                        String iStatus = statusAttr.getValue();
                        preScriptlet = preScriptlet + "" +
                                Status.class.getCanonicalName() + " " + iStatus + " = new " + Status.class.getCanonicalName() + "();" +
                                "varstack.put(\"" + iStatus + "\", " + iStatus + ");";
                        helperScriptlet = helperScriptlet + iStatus + ".inc();";
                        statusAttr.detach();
                        break;
                    }
                }
            }

            if (attrname.startsWith("while")) {
                preScriptlet = preScriptlet + "while ((Boolean)" + elInterpreter.compileEL(el) + ") {";
                sufScriptlet = "}" + sufScriptlet;
            } else if (attrname.startsWith("until")) {
                preScriptlet = preScriptlet + "do {";
                sufScriptlet = "} while (!((Boolean)" + elInterpreter.compileEL(el) + "));" + sufScriptlet;
            } else if (attrname.startsWith("for")) {
                if (el.split("\\s*;\\s*").length == 3) {
                    String[] split = el.split("\\s*;\\s*");
                    preScriptlet = preScriptlet + "for (" +
                            elInterpreter.compileEL(i + "=" + split[0]) + ";" +
                            elInterpreter.compileEL(split[1]) + ";" +
                            elInterpreter.compileEL(split[2]) + ") {";
                } else if (el.matches("-?\\d+\\s*\\.\\.\\s*-?\\d+")) {
                    preScriptlet = preScriptlet + "for (Object " + i + ":" + "(Iterable)" + Iters.class.getCanonicalName() +
                            ".asIterable(" + el.replace("..", ",") + ")) {";
                } else {
                    preScriptlet = preScriptlet + "for (Object " + i + ":" + "(Iterable)" + Iters.class.getCanonicalName() +
                            ".asIterable(" + elInterpreter.compileEL(el) + ")) {";
                }
                helperScriptlet = helperScriptlet + "varstack.put(\"" + i + "\", " + i + ");varstack.push();";
                sufScriptlet = "varstack.pop(); }" + sufScriptlet;
            }

            try {
                if (subtype.equals(".content")) {
                    DOMUtils.surroundInside(elem, "<%" + preScriptlet + helperScriptlet + "%>", "<%" + sufScriptlet + "%>");
                } else {
                    DOMUtils.surroundBy(elem, "<%" + preScriptlet + helperScriptlet + "%>", "<%" + sufScriptlet + "%>");
                }
            } catch (Exception e) {
                throw new CortileException(e);
            }
        }

        attr.detach();
    }

    @SuppressWarnings("unused")
    public void setElInterpreter(ELInterpreter elInterpreter) {
        this.elInterpreter = elInterpreter;
    }

    @SuppressWarnings("unused")
    public static Iterable<Integer> asIterable(int start, int end) {
        return new NumRange(start, end);
    }

    @SuppressWarnings("unused")
    public static Iterable<Integer> asIterable(int start, int end, int step) {
        return new NumRange(start, end, step);
    }

    @SuppressWarnings("unused")
    public static Iterable<?> asIterable(Object obj) {
        if (obj == null) {
            return Collections.emptyList();
        } else if (obj.getClass().isArray()) {
            return Arrays.asList((Object[]) obj);
        } else if (obj instanceof Map) {
            return ((Map<?, ?>) obj).entrySet();
        } else if (obj instanceof Iterable) {
            return (Iterable) obj;
        } else {
            return Collections.unmodifiableMap(new BeanMap(obj)).entrySet();
        }
    }

    public static class Status {

        protected int idx = -1;

        public void inc() {
            idx++;
        }

        public int getIdx() {
            return idx;
        }

        public boolean getOdd() {
            return idx % 2 != 0;
        }

        public boolean getEven() {
            return idx % 2 == 0;
        }
    }

    public static class NumRange implements Iterable<Integer> {

        public class NumRangeIterator implements Iterator<Integer> {

            private int count = start;

            public boolean hasNext() {
                return (step > 0) ? count <= end : count >= end;
            }

            public Integer next() {
                int c = count;
                count += step;
                return c;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        }

        public Iterator<Integer> iterator() {
            return new NumRangeIterator();
        }

        private final int start;
        private final int end;
        private final int step;

        public NumRange(int start, int end) {
            this(start, end, (start <= end) ? 1 : -1);
        }

        public NumRange(int start, int end, int step) {
            this.start = start;
            this.end = end;
            this.step = step;
        }
    }
}
