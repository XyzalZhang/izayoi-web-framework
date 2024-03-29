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

package org.withinsea.izayoi.core.scope;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:16:18
 */
public class Singleton extends SimpleScope {

    protected static Map<String, Object> SINGLETONS = new HashMap<String, Object>();

    @Override
    public Set<String> getContantNames() {
        return Collections.emptySet();
    }

    @Override
    public Set<String> getAttributeNames() {
        return SINGLETONS.keySet();
    }

    @Override
    public boolean containsConstant(String name) {
        return false;
    }

    @Override
    public boolean containsAttribute(String name) {
        return SINGLETONS.containsKey(name);
    }

    @Override
    public <T> T getConstant(String name) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return (T) SINGLETONS.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        SINGLETONS.put(name, value);
    }

    public static Map<String, Object> getSingletons() {
        return SINGLETONS;
    }
}