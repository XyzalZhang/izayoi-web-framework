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

package org.withinsea.izayoi.commons.collection;

import java.util.Iterator;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-6
 * Time: 14:29:43
 */
public class NumRange implements Iterable<Integer> {

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
