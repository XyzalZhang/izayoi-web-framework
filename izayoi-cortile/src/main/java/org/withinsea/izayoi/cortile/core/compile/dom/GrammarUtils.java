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

import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;
import org.withinsea.izayoi.commons.util.LazyTreeMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:28:19
 */
public class GrammarUtils {

    public static int getPriority(Grammar grammar, String methodName) {
        Class<?> claz = grammar.getClass();
        for (Method m : claz.getDeclaredMethods()) {
            if (!Modifier.isVolatile(m.getModifiers()) && m.getDeclaringClass() == claz
                    && m.getName().equals(methodName)) {
                if (m.isAnnotationPresent(Grammar.Priority.class)) {
                    return m.getAnnotation(Grammar.Priority.class).value();
                } else {
                    return Grammar.Priority.DEFAULT;
                }
            }
        }
        return Grammar.Priority.DEFAULT;
    }

    public static <T extends Grammar> Set<T> filte(List<Grammar> grammars, Class<T> grammarClass) {
        if (grammars == null || grammars.isEmpty()) {
            return Collections.emptySet();
        } else {
            Set<T> filted = new LinkedHashSet<T>();
            for (Grammar grammar : grammars) {
                if (grammarClass.isInstance(grammar)) {
                    @SuppressWarnings("unchecked")
                    T g = (T) grammar;
                    filted.add(g);
                }
            }
            return filted;
        }
    }

    public static <T extends Grammar> List<T> sort(List<Grammar> grammars, Class<T> grammarClass, final String sortMethod) {
        List<T> sorted = new ArrayList<T>(filte(grammars, grammarClass));
        Collections.sort(sorted, new Comparator<Grammar>() {
            public int compare(Grammar g1, Grammar g2) {
                return getPriority(g2, sortMethod) - getPriority(g1, sortMethod);
            }
        });
        return sorted;
    }

    public static Map<Integer, Map<String, List<AttrGrammar>>> group(Map<String, List<Grammar>> grammarMap) {
        Map<Integer, Map<String, List<AttrGrammar>>> grouped = new LazyTreeMap<Integer, Map<String, List<AttrGrammar>>>(
                new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2 - o1;
                    }
                }
        ) {
            @Override
            protected Map<String, List<AttrGrammar>> createValue(Integer integer) {
                return new LazyLinkedHashMap<String, List<AttrGrammar>>() {
                    @Override
                    protected List<AttrGrammar> createValue(String s) {
                        return new ArrayList<AttrGrammar>();
                    }
                };
            }
        };
        for (String ns : grammarMap.keySet()) {
            List<AttrGrammar> filted = new ArrayList<AttrGrammar>(filte(grammarMap.get(ns), AttrGrammar.class));
            for (AttrGrammar ag : filted) {
                int priority = getPriority(ag, "processAttr");
                grouped.get(priority).get(ns).add(ag);
            }
        }
        return grouped;
    }

    public static Map<String, List<AttrGrammar>> sortNamespaced(Map<String, List<Grammar>> grammars) {
        Map<String, List<AttrGrammar>> sorted = new LazyLinkedHashMap<String, List<AttrGrammar>>() {
            @Override
            protected List<AttrGrammar> createValue(String s) {
                return new ArrayList<AttrGrammar>();
            }
        };
        for (String ns : grammars.keySet()) {
            sorted.put(ns, sort(grammars.get(ns), AttrGrammar.class, "processAttr"));
        }
        return sorted;
    }
}