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

package org.withinsea.izayoi.core.interpret;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-7-6
 * Time: 4:42:06
 */
public class Vars extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 3958395296622881312L;

    public Vars(Object... pairs) {
        putAll(pairs);
    }

    public Vars(Map<? extends String, ?> m, Object... pairs) {
        super(m);
        for (int i = 0; i < pairs.length - 1; i += 2) {
            put((String) pairs[i], pairs[i + 1]);
        }
    }

    public void putAll(Object... pairs) {
        for (int i = 0; i < pairs.length - 1; i += 2) {
            put((String) pairs[i], pairs[i + 1]);
        }
    }
}