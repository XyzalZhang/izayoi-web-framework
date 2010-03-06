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
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.PathUtils;
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

    protected CodeManager codeManager;
    protected Map<String, Compilr> compilers;

    @Override
    public boolean isUpdated(String templatePath, String asType) throws CortileException {

        String type = checkType(templatePath, asType);
        Compilr compiler = getCompiler(type);
        Cache cache = caches.get(compiler);

        return cache.cached(templatePath) && checkUpdated(templatePath, type, true);
    }

    @Override
    public String update(String templatePath, String asType, boolean focus) throws CortileException {

        String type = checkType(templatePath, asType);
        Compilr compiler = getCompiler(type);
        Cache cache = caches.get(compiler);

        if (focus || !isUpdated(templatePath, asType)) {
            Set<String> done = new HashSet<String>();
            Set<String> todo = new HashSet<String>();
            todo.add(templatePath);
            while (!todo.isEmpty()) {
                String todoTemplatePath = todo.iterator().next();
                if (!codeManager.exist(todoTemplatePath)) {
                    codeManager.delete(compiler.mapEntrancePath(templatePath));
                    cache.remove(templatePath);
                    throw new CortileException(todoTemplatePath + " not exist.");
                } else if (focus || !checkUpdated(todoTemplatePath, asType, false)) {
                    Compilr.Result result = compiler.compile(todoTemplatePath, codeManager.get(todoTemplatePath).getCode());
                    cache.cache(todoTemplatePath, result);
                    for (Map.Entry<String, String> target : result.getTargets().entrySet()) {
                        codeManager.update(target.getKey(), target.getValue());
                    }
                }
                done.add(todoTemplatePath);
                todo.addAll(cache.relatives(todoTemplatePath));
                todo.removeAll(done);
            }
        }
        return compiler.mapEntrancePath(templatePath);
    }

    protected boolean checkUpdated(String templatePath, String asType, boolean checkRelatives) {

        String type = checkType(templatePath, asType);
        Compilr compiler = getCompiler(type);
        Cache cache = caches.get(compiler);

        Code templateCode = codeManager.get(templatePath);

        Set<String> toChecks = new HashSet<String>();
        toChecks.add(templatePath);
        if (checkRelatives) {
            toChecks.addAll(cache.relatives(templatePath));
        }
        for (String toCheck : toChecks) {
            if (!cache.cached(toCheck)) {
                return false;
            }
            for (String targetPath : cache.targets(toCheck)) {
                if (!codeManager.exist(targetPath) || (codeManager.get(targetPath).getLastModified() < templateCode.getLastModified())) {
                    return false;
                }
            }
        }
        return true;
    }

    protected String checkType(String path, String asType) {
        if (asType == null || asType.equals("")) asType = PathUtils.getExtName(path);
        return asType;
    }

    protected Compilr getCompiler(String type) {
        return compilers.get(compilers.containsKey(type) ? type : "default");
    }

    // cache

    protected final Map<Compilr, Cache> caches = new LazyLinkedHashMap<Compilr, Cache>() {
        @Override
        protected Cache createValue(Compilr type) {
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

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setCompilers(Map<String, Compilr> compilers) {
        this.compilers = compilers;
    }
}