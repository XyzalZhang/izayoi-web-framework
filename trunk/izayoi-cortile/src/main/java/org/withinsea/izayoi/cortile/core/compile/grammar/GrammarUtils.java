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

package org.withinsea.izayoi.cortile.core.compile.grammar;

import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;

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
                    return Grammar.Priority.DEFAULT_PRIORITY;
                }
            }
        }
        return Grammar.Priority.DEFAULT_PRIORITY;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Grammar> List<T> sort(
            Map<String, Set<Grammar>> grammars, Class<T> grammarClass, final String sortMethod) {
        Set<T> all = new LinkedHashSet<T>();
        for (Set<Grammar> gs : grammars.values()) {
            for (Grammar g : gs) {
                if (grammarClass.isInstance(g)) {
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
    public static <T extends Grammar> Map<Integer, List<T>> sortAsGroups(
            Map<String, Set<Grammar>> grammars, Class<T> grammarClass, final String sortMethod) {
        Map<Integer, List<T>> groups = new LazyLinkedHashMap<Integer, List<T>>() {

            private static final long serialVersionUID = 1321468422570128699L;

            @Override
            protected List<T> createValue(Integer priority) {
                return new ArrayList<T>();
            }
        };
        for (T grammar : sort(grammars, grammarClass, sortMethod)) {
            groups.get(getPriority(grammar, sortMethod)).add(grammar);
        }
        return groups;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Grammar> Map<Integer, List<NamespacedWrapper<T>>> sortAsNamespacedGroups(
            Map<String, Set<Grammar>> grammars, Class<T> grammarClass, final String sortMethod) {
        List<NamespacedWrapper<T>> sortedWrappers = new LinkedList<NamespacedWrapper<T>>();
        for (String namespace : grammars.keySet()) {
            for (Grammar g : grammars.get(namespace)) {
                if (grammarClass.isInstance(g)) {
                    sortedWrappers.add(new NamespacedWrapper<T>((T) g, namespace));
                }
            }
        }
        Collections.sort(sortedWrappers, new Comparator<NamespacedWrapper<T>>() {
            public int compare(NamespacedWrapper<T> w1, NamespacedWrapper<T> w2) {
                return getPriority(w2.getGrammar(), sortMethod) - getPriority(w1.getGrammar(), sortMethod);
            }
        });
        Map<Integer, List<NamespacedWrapper<T>>> groups = new LazyLinkedHashMap<Integer, List<NamespacedWrapper<T>>>() {

            private static final long serialVersionUID = 7487773273694290974L;

            @Override
            protected List<NamespacedWrapper<T>> createValue(Integer priority) {
                return new ArrayList<NamespacedWrapper<T>>();
            }
        };
        for (NamespacedWrapper<T> ngw : sortedWrappers) {
            groups.get(getPriority(ngw.getGrammar(), sortMethod)).add(ngw);
        }
        return groups;
    }

    public static class NamespacedWrapper<T extends Grammar> {

        protected final T grammar;
        protected final String namespace;

        public NamespacedWrapper(T grammar, String namespace) {
            this.grammar = grammar;
            this.namespace = namespace;
        }

        public T getGrammar() {
            return grammar;
        }

        public String getNamespace() {
            return namespace;
        }
    }
}