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

package org.withinsea.izayoi.rosace.core.impl.grammar.ns.c;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.common.util.BeanMap;
import org.withinsea.izayoi.common.util.StringUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;

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
    public void processAttr(Attribute attr) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();

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
            throw new RosaceException("\"" + attr.getValue() + "\" is not a valid cycle expression.");
        }

        String preScriptlet = "{ " + engine.precompileOpenScope();
        String helperScriptlet = "";
        String sufScriptlet = engine.precompileCloseScope() + " }";

        if (varname != null) {
            boolean after = false;
            for (Attribute statusAttr : (List<Attribute>) elem.attributes()) {
                if (attr == statusAttr) {
                    after = true;
                } else if (after) {
                    if (acceptAttr(statusAttr)) break;
                    String aname = statusAttr.getName();
                    if (aname.equals(ctype + "-status") || aname.equals(ctype + "-status." + varname)
                            || aname.equals(ctype + "-st") || aname.equals(ctype + "-st." + varname)) {
                        String iStatus = statusAttr.getValue();
                        preScriptlet += Status.class.getCanonicalName() + " " + iStatus + " = " +
                                "new " + Status.class.getCanonicalName() + "();" +
                                engine.precompilePut(iStatus, iStatus);
                        helperScriptlet += iStatus + ".inc();";
                        statusAttr.detach();
                        break;
                    }
                }
            }
        }

        if (varname != null) {
            boolean after = false;
            for (Attribute splitterAttr : (List<Attribute>) elem.attributes()) {
                if (attr == splitterAttr) {
                    after = true;
                } else if (after) {
                    if (acceptAttr(splitterAttr)) break;
                    String aname = splitterAttr.getName();
                    if (aname.equals(ctype + "-splitter") || aname.equals(ctype + "-splitter." + varname) ||
                            aname.equals(ctype + "-sp") || aname.equals(ctype + "-sp." + varname)) {
                        String splitter = precompileEmbeddedELs(engine, splitterAttr.getValue());
                        preScriptlet += "boolean " + varname + "_isfirst = true;";
                        helperScriptlet += "if (!" + varname + "_isfirst) {%><%=" + splitter + "%><%}" +
                                varname + "_isfirst = false;";
                        splitterAttr.detach();
                        break;
                    }
                }
            }
        }

        if (attrname.startsWith("while") && vtype.equals("el")) {
            preScriptlet = preScriptlet + "while ((Boolean)" + engine.precompileEl(value) + ") {";
            sufScriptlet = "}" + sufScriptlet;
        } else if (attrname.startsWith("until") && vtype.equals("el")) {
            preScriptlet = preScriptlet + "do {";
            sufScriptlet = "} while (!((Boolean)" + engine.precompileEl(value) + "));" + sufScriptlet;
        } else if (attrname.startsWith("repeat") || attrname.startsWith("for")) {
            String itername = varname + "_iter";
            if (vtype.equals("for3")) {
                String[] split = el.split("\\s*;\\s*");
                preScriptlet = preScriptlet + "for (" +
                        engine.precompileEl(varname + "=(" + split[0] + ")") + ";" +
                        "(Boolean) " + engine.precompileEl(split[1]) + ";" +
                        engine.precompileEl(split[2]) + ") {";
            } else if (vtype.equals("range")) {
                String iterable = Cycle.class.getCanonicalName() + ".asIterable(" + value.replace("..", ",") + ")";
                preScriptlet = preScriptlet +
                        "java.util.Iterator " + itername + " = ((Iterable)" + iterable + ").iterator();" +
                        "while (" + itername + ".hasNext()) { Object " + varname + " = " + itername + ".next();";
            } else {
                String iterable = Cycle.class.getCanonicalName() + ".asIterable(" + engine.precompileEl(el) + ")";
                preScriptlet = preScriptlet +
                        "java.util.Iterator " + itername + " = ((Iterable)" + iterable + ").iterator();" +
                        "while (" + itername + ".hasNext()) { Object " + varname + " = " + itername + ".next();";
            }
            helperScriptlet = helperScriptlet + engine.precompilePut(varname, varname);
            helperScriptlet = helperScriptlet + engine.precompileOpenScope();
            sufScriptlet = engine.precompileCloseScope() + " }" + sufScriptlet;
        }

        try {
            DomUtils.surroundBy(elem, "<%" + preScriptlet + helperScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new RosaceException(e);
        }

        attr.detach();
    }

    protected String precompileEmbeddedELs(final DomTemplateEngine engine, String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + engine.precompileEl(groups[1].replace("\\}", "}"), true) + "+ \"";
            }
        }) + "\"";
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
