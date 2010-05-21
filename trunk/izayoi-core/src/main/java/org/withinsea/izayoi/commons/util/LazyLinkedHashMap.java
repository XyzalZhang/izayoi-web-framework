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

import java.util.LinkedHashMap;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 14:13:14
 */
public abstract class LazyLinkedHashMap<K, V> extends LinkedHashMap<K, V> {

    protected abstract V createValue(K k);

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        if (!containsKey(key)) {
            synchronized (this) {
                put((K) key, createValue((K) key));
            }
        }
        return super.get(key);
    }
}