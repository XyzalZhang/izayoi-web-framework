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
 * Date: 2010-8-20
 * Time: 11:31:11
 */
public class ConcatList<E> implements List<E> {

    protected List<List<E>> lists;

    public ConcatList(List<E>... lists) {
        this(Arrays.asList(lists));
    }

    public ConcatList(List<List<E>> lists) {
        this.lists = lists;
    }

    @Override
    public boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        int size = 0;
        for (List<E> list : lists) {
            size += list.size();
        }
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (List<E> list : lists) {
            if (!list.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean contains(Object o) {
        for (List<E> list : lists) {
            if (list.contains(o)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        List<Iterator<E>> iterators = new ArrayList<Iterator<E>>();
        for (List<E> list : lists) {
            iterators.add(list.iterator());
        }
        return new ConcatIterator<E>(iterators);
    }

    @Override
    public Object[] toArray() {
        int size = size();
        Object[] arr = new Object[size];
        for (int i = 0; i < size; i++) {
            arr[i] = get(i);
        }
        return arr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = Math.min(a.length, size());
        for (int i = 0; i < size; i++) {
            a[i] = (T) get(i);
        }
        return a;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        int idx = index;
        for (List<E> list : lists) {
            if (idx < list.size()) {
                return list.get(idx);
            } else {
                idx -= list.size();
            }
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        int len = 0;
        for (List<E> list : lists) {
            int idx = list.indexOf(o);
            if (idx >= 0) {
                return idx + len;
            } else {
                len += list.size();
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object o) {
        int len = size();
        for (int i = lists.size() - 1; i >= 0; i--) {
            List<E> list = lists.get(i);
            len -= list.size();
            int idx = list.lastIndexOf(o);
            if (idx >= 0) {
                return idx + len;
            }
        }
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        List<ListIterator<E>> iterators = new ArrayList<ListIterator<E>>();
        for (List<E> list : lists) {
            iterators.add(list.listIterator());
        }
        return new ConcatListIterator<E>(iterators, 0);
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        List<ListIterator<E>> iterators = new ArrayList<ListIterator<E>>();
        for (List<E> list : lists) {
            iterators.add(list.listIterator());
        }
        return new ConcatListIterator<E>(iterators, index);
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    protected static class ConcatIterator<E> implements Iterator<E> {

        protected List<Iterator<E>> iterators;
        protected int iIter;

        public ConcatIterator(List<Iterator<E>> iterators) {
            this.iterators = iterators;
            this.iIter = 0;
        }

        @Override
        public boolean hasNext() {
            if (iIter >= iterators.size()) {
                return false;
            }
            if (!iterators.get(iIter).hasNext()) {
                iIter++;
            }
            return (iIter < iterators.size()) && iterators.get(iIter).hasNext();
        }

        @Override
        public E next() {
            return !hasNext() ? null : iterators.get(iIter).next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    protected static class ConcatListIterator<E> implements ListIterator<E> {

        protected List<ListIterator<E>> iterators;
        protected int iIter;
        protected int i;

        public ConcatListIterator(List<ListIterator<E>> iterators, int index) {
            this.iterators = iterators;
            this.iIter = 0;
            this.i = 0;
            for (int i = 0; i < index; i++) {
                next();
            }
        }

        @Override
        public void add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasNext() {
            if (iIter >= iterators.size()) {
                return false;
            }
            if (!iterators.get(iIter).hasNext()) {
                iIter++;
            }
            return (iIter < iterators.size()) && iterators.get(iIter).hasNext();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                return null;
            } else {
                i++;
                return iterators.get(iIter).next();
            }
        }

        @Override
        public boolean hasPrevious() {
            if (iIter < 0) {
                return false;
            }
            if (!iterators.get(iIter).hasPrevious()) {
                iIter--;
            }
            return (iIter >= 0) && iterators.get(iIter).hasPrevious();
        }

        @Override
        public E previous() {
            if (!hasPrevious()) {
                return null;
            } else {
                i--;
                return iterators.get(iIter).previous();
            }
        }

        @Override
        public int nextIndex() {
            return i + 1;
        }

        @Override
        public int previousIndex() {
            return i - 1;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void set(E e) {
            if (iIter >= 0 && iIter < iterators.size()) {
                iterators.get(iIter).set(e);
            }
        }
    }
}
