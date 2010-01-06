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

package org.withinsea.izayoi.cortile.core.compiler;

import org.withinsea.izayoi.commons.collection.CreationLinkedHashMap;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:28:19
 */
public class CompilerUtils {

    public static int getPriority(Grammar grammar, String methodName) {
        for (Method m : grammar.getClass().getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                if (m.isAnnotationPresent(Grammar.Priority.class)) {
                    return m.getAnnotation(Grammar.Priority.class).value();
                } else {
                    return Grammar.Priority.DEFAULT_PRIORITY;
                }
            }
        }
        return Grammar.Priority.DEFAULT_PRIORITY;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Grammar> Map<String, List<T>> sort(
            Map<String, Set<Grammar>> grammars, Class<T> claz, final String sortMethod) {
        Map<String, List<T>> sorted = new LinkedHashMap<String, List<T>>();
        for (Map.Entry<String, Set<Grammar>> e : grammars.entrySet()) {
            List<T> gs = new ArrayList<T>();
            for (Grammar g : e.getValue()) {
                if (claz.isInstance(g)) {
                    gs.add((T) g);
                }
            }
            Collections.sort(gs, new Comparator<Grammar>() {
                public int compare(Grammar g1, Grammar g2) {
                    return getPriority(g2, sortMethod) - getPriority(g1, sortMethod);
                }
            });
            sorted.put(e.getKey(), gs);
        }
        return sorted;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Grammar> Map<Integer, Map<String, List<T>>> sortAsPriorityGroups(
            Map<String, Set<Grammar>> grammars, Class<T> claz, final String sortMethod) {
        Map<Integer, Map<String, List<T>>> groups = new CreationLinkedHashMap<Integer, Map<String, List<T>>>() {
            @Override
            protected Map<String, List<T>> createValue() {
                return new CreationLinkedHashMap<String, List<T>>() {
                    @Override
                    protected List<T> createValue() {
                        return new ArrayList<T>();
                    }
                };
            }
        };
        for (Map.Entry<String, List<T>> e : sort(grammars, claz, sortMethod).entrySet()) {
            for (T grammar : e.getValue()) {
                groups.get(getPriority(grammar, sortMethod)).get(e.getKey()).add(grammar);
            }
        }
        return groups;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Grammar> List<T> sortAll(
            Map<String, Set<Grammar>> grammars, Class<T> claz, final String sortMethod) {
        Set<T> all = new LinkedHashSet<T>();
        for (Set<Grammar> gs : grammars.values()) {
            for (Grammar g : gs) {
                if (claz.isInstance(g)) {
                    all.add((T) g);
                }
            }
        }
        List<T> sorted = new ArrayList<T>(all);
        Collections.sort(sorted, new Comparator<Grammar>() {
            public int compare(Grammar g1, Grammar g2) {
                return getPriority(g2, sortMethod) - getPriority(g1, sortMethod);
            }
        });
        return sorted;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Grammar> Map<Integer, List<T>> sortAllAsPriorityGroups(
            Map<String, Set<Grammar>> grammars, Class<T> claz, final String sortMethod) {
        Map<Integer, List<T>> groups = new CreationLinkedHashMap<Integer, List<T>>() {
            @Override
            protected List<T> createValue() {
                return new ArrayList<T>();
            }
        };
        for (T grammar : sortAll(grammars, claz, sortMethod)) {
            groups.get(getPriority(grammar, sortMethod)).add(grammar);
        }
        return groups;
    }
}
