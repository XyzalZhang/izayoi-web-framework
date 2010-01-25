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

package org.withinsea.izayoi.commons.test.util;

import org.junit.Test;
import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-17
 * Time: 15:58:18
 */
public class LazyLinkedHashMapTest {

    @Test
    public void nop() {

    }

    @Test
    public void create() {
        Map<String, Object> map = new LazyLinkedHashMap<String, Object>() {
            @Override
            protected Object createValue(String s) {
                return new Object();
            }
        };
        Object a1 = map.get("a");
        assertNotNull(a1);
        Object a2 = map.get("a");
        assertNotNull(a2);
        assertSame(a1, a2);
        Object b1 = map.get("b");
        assertNotNull(b1);
        assertNotSame(a1, b1);
        Object b2 = map.get("b");
        assertNotNull(b2);
        assertSame(b1, b2);
        assertArrayEquals(new String[]{"a", "b"}, map.keySet().toArray(new String[map.keySet().size()]));
    }

    @Test
    public void transform() {
        Map<String, Object> map = new LazyLinkedHashMap<String, Object>() {
            @Override
            protected Object createValue(String s) {
                return "transform " + s.toLowerCase();
            }
        };
        Object a = map.get("a");
        Object A = map.get("A");
        assertEquals("transform a", a);
        assertEquals("transform a", A);
        assertEquals(a, A);
        assertNotSame(a, A);
    }

    @Test
    public void put() {
        Map<String, Object> map = new LazyLinkedHashMap<String, Object>() {
            @Override
            protected Object createValue(String s) {
                return new Object();
            }
        };
        Object a1 = map.get("a");
        Object a2 = new Object();
        map.put("a", a2);
        Object a3 = map.get("a");
        assertSame(a3, a2);
        assertNotSame(a1, a2);
    }
}
