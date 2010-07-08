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

package org.withinsea.izayoi.commons.util;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-7-6
 * Time: 10:24:18
 */
public class FP {

    public static interface Mapper<S, D> {
        D map(S s);
    }

    public static <S, D> List<D> map(Collection<S> src, Mapper<S, D> mapper) {
        List<D> dest = new ArrayList<D>(src.size());
        for (S s : src) {
            dest.add(mapper.map(s));
        }
        return dest;
    }

    public static <K, S, D> Map<K, D> map(Map<K, S> src, Mapper<S, D> mapper) {
        Map<K, D> dest = new LinkedHashMap<K, D>();
        for (Map.Entry<K, S> e : src.entrySet()) {
            dest.put(e.getKey(), mapper.map(e.getValue()));
        }
        return dest;
    }

    public static interface Filter<T> {
        boolean filte(T t);
    }

    public static <T> List<T> filte(Collection<T> src, Filter<T> filter) {
        List<T> dest = new ArrayList<T>();
        for (T t : src) {
            if (filter.filte(t)) {
                dest.add(t);
            }
        }
        return dest;
    }

    public static <K, T> Map<K, T> filte(Map<K, T> src, Filter<Map.Entry<K, T>> filter) {
        Map<K, T> dest = new LinkedHashMap<K, T>();
        for (Map.Entry<K, T> e : src.entrySet()) {
            if (filter.filte(e)) {
                dest.put(e.getKey(), e.getValue());
            }
        }
        return dest;
    }
}
