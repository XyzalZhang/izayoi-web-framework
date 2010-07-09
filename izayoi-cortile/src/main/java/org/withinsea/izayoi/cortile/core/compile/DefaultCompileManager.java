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

import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.Path;
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
public class DefaultCompileManager implements CompileManager {

    protected class Cache {

        protected final Map<String, Set<String>> relatives = new HashMap<String, Set<String>>();
        protected final Map<String, Set<String>> targets = new HashMap<String, Set<String>>();

        public boolean cached(String templatePath) {
            return (relatives.containsKey(templatePath) && targets.containsKey(templatePath));
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

    protected final Cache cache = new Cache();

    protected CodeManager codeManager;
    protected Map<String, Compilr> compilers;

    @Override
    public String findTemplatePath(String path) {
        Path parsedPath = new Path(path);
        if (compilers.containsKey(parsedPath.getType()) && codeManager.exist(path)) {
            return path;
        } else {
            return null;
        }
    }

    @Override
    public String update(String templatePath, boolean focus) throws CortileException {

        Compilr compiler = getCompiler(templatePath);

        if (focus || !isUpdated(templatePath)) {
            Set<String> done = new HashSet<String>();
            Set<String> todo = new HashSet<String>();
            todo.add(templatePath);
            while (!todo.isEmpty()) {
                String todoTemplatePath = todo.iterator().next();
                if (!codeManager.exist(todoTemplatePath)) {
                    codeManager.delete(compiler.mapEntrancePath(templatePath));
                    cache.remove(templatePath);
                    throw new CortileException(todoTemplatePath + " not exist.");
                } else if (focus || !checkUpdated(todoTemplatePath, false)) {
                    Compilr.Result result = compiler.compile(todoTemplatePath, codeManager.get(todoTemplatePath).getCode());
                    cache.cache(todoTemplatePath, result);
                    for (Map.Entry<String, String> target : result.getTargets().entrySet()) {
                        codeManager.update(target.getKey(), target.getValue(), false);
                    }
                }
                done.add(todoTemplatePath);
                todo.addAll(cache.relatives(todoTemplatePath));
                todo.removeAll(done);
            }
        }

        return compiler.mapEntrancePath(templatePath);
    }

    protected boolean isUpdated(String templatePath) throws CortileException {
        return cache.cached(templatePath) && checkUpdated(templatePath, true);
    }

    protected boolean checkUpdated(String templatePath, boolean checkRelatives) {

        Set<String> toChecks = new HashSet<String>();
        toChecks.add(templatePath);
        if (checkRelatives) {
            toChecks.addAll(cache.relatives(templatePath));
        }
        for (String toCheck : toChecks) {
            if (!cache.cached(toCheck)) {
                return false;
            }
            Code toCheckCode = codeManager.get(toCheck);
            for (String targetPath : cache.targets(toCheck)) {
                if (!codeManager.exist(targetPath) || (codeManager.get(targetPath).getLastModified() < toCheckCode.getLastModified())) {
                    return false;
                }
            }
        }
        return true;
    }

    protected Compilr getCompiler(String templatePath) throws CortileException {
        String type = new Path(templatePath).getType();
        Compilr compiler = compilers.get(compilers.containsKey(type) ? type : "default");
        if (compiler == null) {
            throw new CortileException("compile type " + type + " does not exist.");
        }
        return compiler;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setCompilers(Map<String, Compilr> compilers) {
        this.compilers = compilers;
    }
}