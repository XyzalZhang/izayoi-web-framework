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

package org.withinsea.izayoi.glowworm.core.injector;

import org.withinsea.izayoi.glowworm.core.dependency.DependencyManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-3
 * Time: 1:23:47
 */
public class ScriptInjector implements Injector {

    protected DependencyManager dependencyManager;

    @Override
    public Object inject(HttpServletRequest request, String srcPath, String src) throws GlowwormException {
        try {
            String extName = srcPath.trim().replaceAll(".*\\/", "").replaceAll(".*\\.", "");
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension(extName);
            return engine.eval(src, new DependencyBindings(dependencyManager, request));
        } catch (Exception e) {
            throw new GlowwormException(e);
        }
    }

    public void setDependencyManager(DependencyManager dependencyManager) {
        this.dependencyManager = dependencyManager;
    }

    protected static class DependencyBindings implements Bindings {

        protected HttpServletRequest request;
        protected DependencyManager dependencyManager;

        public DependencyBindings(DependencyManager dependencyManager, HttpServletRequest request) {
            this.dependencyManager = dependencyManager;
            this.request = request;
        }

        @Override
        public void clear() {
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
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public boolean containsValue(Object value) {
            return false;
        }

        @Override
        public Object get(Object key) {
            return dependencyManager.getBean(request, key.toString());
        }

        @Override
        public Object put(String key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
        }

        @Override
        public Set<String> keySet() {
            return Collections.emptySet();
        }

        @Override
        public Collection<Object> values() {
            return Collections.emptySet();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            return Collections.emptySet();
        }
    }
}