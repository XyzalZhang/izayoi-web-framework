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
import org.withinsea.izayoi.commons.collection.NumRange;
import org.withinsea.izayoi.commons.lang.BeanMap;
import org.withinsea.izayoi.commons.xml.DOM4JUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.ELInterpreter;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:14:05
 */
public class Iters implements AttrGrammar {

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

    protected ELInterpreter elInterpreter;

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return attr.getName().matches("while|until|^(while|until|for)\\.\\w+");
    }

    @Override
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {

        String el = attr.getValue().trim();
        el = (el.startsWith("${") && el.endsWith("}")) ? el.substring(2, el.length() - 3).trim() : el;

        if (!el.equals("")) {

            String preScriptlet = "{ varstack.push();";
            String helperScriptlet = "";
            String sufScriptlet = "varstack.pop(); }";

            String i = attr.getName().matches("while|until") ? null :
                    attr.getName().replaceFirst("(while|until|for)\\.", "");
            if (i != null) {
                String iStatus = i + "_status";
                preScriptlet = preScriptlet + "" +
                        Status.class.getCanonicalName() + " " + iStatus + " = new " + Status.class.getCanonicalName() + "();" +
                        "varstack.put(\"" + iStatus + "\", " + iStatus + ");";
                helperScriptlet = helperScriptlet + iStatus + ".inc();";
            }

            if (attr.getName().startsWith("while")) {
                preScriptlet = preScriptlet + "while ((Boolean)" + elInterpreter.compileEL(el) + ") {";
                sufScriptlet = "}" + sufScriptlet;
            } else if (attr.getName().startsWith("until")) {
                preScriptlet = preScriptlet + "do {";
                sufScriptlet = "} while (!((Boolean)" + elInterpreter.compileEL(el) + "));" + sufScriptlet;
            } else if (attr.getName().startsWith("for")) {
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
                DOM4JUtils.surroundBy(elem, "<%" + preScriptlet + helperScriptlet + "%>", "<%" + sufScriptlet + "%>");
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
}
