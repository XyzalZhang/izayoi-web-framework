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

package org.withinsea.izayoi.core.dependency;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-5
 * Time: 3:23:18
 */
public class DependencyUtils {

    protected static final String DEPENDENCY_MAP_ATTR = DependencyUtils.class.getCanonicalName() + ".DEPENDENCY_MAP";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(DependencyManager dependencyManager, HttpServletRequest request) {
        Map<DependencyManager, Map<String, Object>> map = (Map<DependencyManager, Map<String, Object>>) request.getAttribute(DEPENDENCY_MAP_ATTR);
        if (map == null) {
            map = new LinkedHashMap<DependencyManager, Map<String, Object>>();
            request.setAttribute(DEPENDENCY_MAP_ATTR, map);
        }
        Map<String, Object> dependencyMap = map.get(dependencyManager);
        if (dependencyMap == null) {
            dependencyMap = Collections.unmodifiableMap(new DependencyMap(dependencyManager, request));
            map.put(dependencyManager, dependencyMap);
        }
        return dependencyMap;
    }

    protected static class DependencyMap implements Map<String, Object> {

        protected final DependencyManager dependencyManager;
        protected final HttpServletRequest request;

        public DependencyMap(DependencyManager dependencyManager, HttpServletRequest request) {
            this.dependencyManager = dependencyManager;
            this.request = request;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        // mock methods

        @Override
        public Object put(String name, Object value) {
            return null;
        }

        @Override
        public void putAll(Map<? extends String, ?> toMerge) {

        }

        @Override
        public Object get(Object key) {
            return dependencyManager.getBean(request, key.toString());
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public void clear() {

        }

        @Override
        public Set<String> keySet() {
            return null;
        }

        @Override
        public Collection<Object> values() {
            return null;
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return null;
        }
    }
}
