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

package org.withinsea.izayoi.cortile.core.compile;

import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;
import org.withinsea.izayoi.core.conf.CodeManager;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 2:21:15
 */
public class WebappCompileManager implements CompileManager {

    protected String encoding;
    protected CodeManager codeManager;
    protected Map<String, Compilr> compilers;

    @Override
    public String getEncoding() {
        return encoding;
    }

    @Override
    public Set<String> getSupportedTypes() {
        return compilers.keySet();
    }

    @Override
    public boolean exist(String templatePath) {
        return codeManager.exist(templatePath);
    }

    @Override
    public boolean isUpdated(String templatePath, String asType) {
        return caches.get(asType).cached(templatePath) && checkUpdated(templatePath, asType, true);
    }

    @Override
    public String update(String templatePath, String asType) throws CortileException {
        return update(templatePath, asType, false);
    }

    @Override
    public String update(String templatePath, String asType, boolean focus) throws CortileException {
        if (focus || !isUpdated(templatePath, asType)) {
            Set<String> done = new HashSet<String>();
            Set<String> todo = new HashSet<String>();
            todo.add(templatePath);
            while (!todo.isEmpty()) {
                String todoTemplatePath = todo.iterator().next();
                if (!codeManager.exist(todoTemplatePath)) {
                    codeManager.delete(compilers.get(asType).mapEntrancePath(templatePath));
                    caches.get(asType).remove(templatePath);
                    throw new CortileException(todoTemplatePath + " not exist.");
                } else if (focus || !checkUpdated(todoTemplatePath, asType, false)) {
                    Compilr.Result result = compilers.get(asType).compile(todoTemplatePath, codeManager.get(todoTemplatePath).getCode());
                    caches.get(asType).cache(todoTemplatePath, result);
                    for (Map.Entry<String, String> target : result.getTargets().entrySet()) {
                        codeManager.update(target.getKey(), target.getValue());
                    }
                }
                done.add(todoTemplatePath);
                todo.addAll(caches.get(asType).relatives(todoTemplatePath));
                todo.removeAll(done);
            }
        }
        return compilers.get(asType).mapEntrancePath(templatePath);
    }

    protected boolean checkUpdated(String templatePath, String asType, boolean checkRelatives) {
        Set<String> toChecks = new HashSet<String>();
        toChecks.add(templatePath);
        if (checkRelatives) {
            toChecks.addAll(caches.get(asType).relatives(templatePath));
        }
        for (String toCheck : toChecks) {
            if (!caches.get(asType).cached(toCheck)) {
                return false;
            }
            for (String targetPath : caches.get(asType).targets(toCheck)) {
                if (!codeManager.exist(targetPath) || (
                        codeManager.get(targetPath).getLastModified() < codeManager.get(templatePath).getLastModified())) {
                    return false;
                }
            }
        }
        return true;
    }

    // cache

    protected final Map<String, Cache> caches = new LazyLinkedHashMap<String, Cache>() {
        @Override
        protected Cache createValue(String type) {
            return new Cache();
        }
    };

    protected static class Cache {

        private final Map<String, Set<String>> relatives = new HashMap<String, Set<String>>();
        private final Map<String, Set<String>> targets = new HashMap<String, Set<String>>();

        public boolean cached(String templatePath) {
            return (relatives.containsKey(templatePath)
                    && targets.containsKey(templatePath));
        }

        public void cache(String templatePath, Compilr.Result result) {
            relatives.put(templatePath, result.getRelativeTemplatePaths());
            targets.put(templatePath, result.getTargets().keySet());
        }

        public void remove(String templatePath) {
            relatives.remove(templatePath);
            targets.remove(templatePath);
        }

        public Set<String> relatives(String templatePath) {
            return relatives.get(templatePath);
        }

        public Set<String> targets(String templatePath) {
            return targets.get(templatePath);
        }
    }

    // dependency

    @SuppressWarnings("unused")
    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    @SuppressWarnings("unused")
    public void setCompilers(Map<String, Compilr> compilers) {
        this.compilers = compilers;
    }

    @SuppressWarnings("unused")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}