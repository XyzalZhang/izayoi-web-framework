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

package org.withinsea.izayoi.cortile.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-22
 * Time: 8:52:15
 */
public class IterUtils {

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
            this.start = start;
            this.end = end;
            step = (start <= end) ? 1 : -1;
        }
    }

    public static Iterable<Integer> iter(int start, int end) {
        return new NumRange(start, end);
    }

    public static Iterable<?> iter(Object obj) {
        if (obj == null) {
            return Collections.emptyList();
        } else if (obj.getClass().isArray()) {
            return Arrays.asList((Object[]) obj);
        } else if (obj instanceof Map) {
            return ((Map<?, ?>) obj).entrySet();
        } else if (obj instanceof Iterable) {
            return (Iterable) obj;
        } else {
            return new BeanMap(obj).entrySet();
        }
    }
}