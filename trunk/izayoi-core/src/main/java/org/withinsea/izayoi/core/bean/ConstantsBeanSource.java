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
 * The Original Code is the @PROJECT_NAME
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.core.bean;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-21
 * Time: 14:54:38
 */
public class ConstantsBeanSource implements BeanSource {

    protected Map<String, Object> map;

    public ConstantsBeanSource(Object... keypairs) {
        this.map = new LinkedHashMap<String, Object>();
        for (int i = 0; i < keypairs.length - 1; i += 2) {
            map.put((String) keypairs[i], keypairs[i + 1]);
        }
    }

    public ConstantsBeanSource(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public boolean exist(Object bean) {
        return map.containsValue(bean);
    }

    @Override
    public boolean exist(String name) {
        return map.containsKey(name);
    }

    @Override
    public boolean exist(Class<?> claz) {
        for (Object v : map.values()) {
            if (v != null && claz.isInstance(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) map.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> claz) {
        for (Object v : map.values()) {
            if (v != null && claz.isInstance(v)) {
                return (T) v;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> list(String name) {
        Object bean = get(name);
        return Arrays.asList((T) bean);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<T> claz) {
        List<T> beans = new ArrayList<T>();
        for (Object v : map.values()) {
            if (v != null && claz.isInstance(v)) {
                beans.add((T) v);
            }
        }
        return beans;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public void setMap(Map<String, Object> map) {
        this.map = map;
    }
}
