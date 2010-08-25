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

package org.withinsea.izayoi.cortile.template.html.grammar.attr.core;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.commons.util.BeanMap;
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:14:05
 */
public class Cycle implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        String attrname = attr.getName();
        return attrname.matches("while|until|^(while|until|repeat|for)\\.[\\w\\.]+");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void processAttr(Attribute attr) throws CortileException {

        CompileContext ctx = CompileContext.get();
        HTMLCompiler compiler = ctx.getCompiler();

        Element elem = attr.getParent();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        String ctype = attrname.startsWith("while") ? "while"
                : attrname.startsWith("until") ? "until"
                : attrname.startsWith("repeat") ? "repeat" : "for";
        String subname = attrname.substring(ctype.length());
        String subtype = subname.startsWith("-status") ? "-status" : "";
        String varname = subname.substring(subtype.length()).replaceFirst("\\.", "");
        if (subtype.equals("-status")) {
            return;
        }

        String value = attrvalue.trim();
        String el = value.startsWith("${") ? value.substring(2, value.length() - 1).trim() : value;
        String vtype = (value.startsWith("${") && value.endsWith("}")) ? "el"
                : (value.split("\\s*;\\s*").length == 3) ? "for3"
                : (value.matches("-?\\d+\\s*\\.\\.\\s*-?\\d+")) ? "range"
                : null;
        if (vtype == null) {
            throw new CortileException("\"" + attr.getValue() + "\" is not a valid cycle expression.");
        }

        String preScriptlet = "{ " + compiler.openScope();
        String helperScriptlet = "";
        String sufScriptlet = compiler.closeScope() + " }";

        if (varname != null) {
            for (Attribute statusAttr : (List<Attribute>) elem.attributes()) {
                String aname = statusAttr.getName();
                if (aname.equals(ctype + "-status." + varname)) {
                    String iStatus = statusAttr.getValue();
                    preScriptlet += Status.class.getCanonicalName() + " " + iStatus + " = new " + Status.class.getCanonicalName() + "();" +
                            compiler.elBind(iStatus, iStatus);
                    helperScriptlet += iStatus + ".inc();";
                    statusAttr.detach();
                    break;
                }
            }
        }

        if (attrname.startsWith("while") && vtype.equals("el")) {
            preScriptlet = preScriptlet + "while ((Boolean)" + compiler.el(value) + ") {";
            sufScriptlet = "}" + sufScriptlet;
        } else if (attrname.startsWith("until") && vtype.equals("el")) {
            preScriptlet = preScriptlet + "do {";
            sufScriptlet = "} while (!((Boolean)" + compiler.el(value) + "));" + sufScriptlet;
        } else if (attrname.startsWith("repeat") || attrname.startsWith("for")) {
            if (vtype.equals("for3")) {
                String[] split = el.split("\\s*;\\s*");
                preScriptlet = preScriptlet + "for (" +
                        compiler.el(varname + "=(" + split[0] + ")") + ";" +
                        "(Boolean) " + compiler.el(split[1]) + ";" +
                        compiler.el(split[2]) + ") {";
            } else if (vtype.equals("range")) {
                preScriptlet = preScriptlet + "for (" +
                        "Object " + varname + ":" + "(Iterable)" +
                        Cycle.class.getCanonicalName() + ".asIterable(" + value.replace("..", ",") + ")) {";
                helperScriptlet = helperScriptlet + compiler.elBind(varname, varname);
            } else {
                preScriptlet = preScriptlet + "for (" +
                        "Object " + varname + ":" + "(Iterable)" +
                        Cycle.class.getCanonicalName() + ".asIterable(" + compiler.el(el) + ")) {";
                helperScriptlet = helperScriptlet + compiler.elBind(varname, varname);
            }
            helperScriptlet = helperScriptlet + compiler.openScope();
            sufScriptlet = compiler.closeScope() + " }" + sufScriptlet;
        }

        try {
            DOMUtils.surroundBy(elem, "<%" + preScriptlet + helperScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }

    public static Iterable<Integer> asIterable(int start, int end) {
        return new NumRange(start, end);
    }

    public static Iterable<Integer> asIterable(int start, int end, int step) {
        return new NumRange(start, end, step);
    }

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
