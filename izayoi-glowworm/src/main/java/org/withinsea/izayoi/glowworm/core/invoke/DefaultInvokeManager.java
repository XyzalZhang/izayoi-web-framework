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

package org.withinsea.izayoi.glowworm.core.invoke;

import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 6:03:55
 */
public class DefaultInvokeManager implements InvokeManager {

    protected static class Cache extends HashMap<String, Long> {

        private static final long serialVersionUID = 3613991425340677036L;

        protected static final String LAST_MODIFIED_ATTR = Cache.class.getCanonicalName() + ".LAST_MODIFIED";

        public static synchronized Cache get(Scope scope) throws GlowwormException {
            Cache lastModifieds = scope.getBean(LAST_MODIFIED_ATTR);
            if (lastModifieds == null) {
                lastModifieds = new Cache();
                scope.setBean(LAST_MODIFIED_ATTR, lastModifieds);
            }
            return lastModifieds;
        }

        public boolean cached(Code code) {
            String path = code.getPath().getPath();
            return containsKey(path) && (get(path) >= code.getLastModified());
        }

        public void cache(Code code) {
            String path = code.getPath().getPath();
            put(path, code.getLastModified());
        }
    }

    protected CodeManager codeManager;
    protected String appendantFolder;
    protected String globalPrefix;

    protected Map<String, Invoker> invokers;
    protected List<String> invokersOrder;

    @Override
    public boolean isAppendant(String path) {
        Path parsedPath = new Path(path);
        return parsedPath.isAppendant() && invokers.containsKey(parsedPath.getAppendantRole());
    }

    @Override
    public List<String> findScopedAppendantPaths(String scopeName, Scope scope) {

        String globalNameRegex = Pattern.quote(globalPrefix) + "[^\\.]*";
        String scopeRegex = Pattern.quote("@" + scopeName);
        String suffixRegex = "\\.(" + StringUtils.join("|", invokers.keySet()) + ")" + "\\.[^\\.]+$";

        List<String> appendantPaths = new ArrayList<String>();

        String folder = appendantFolder;
        for (String appendantName : sort(codeManager.listNames(folder, globalNameRegex + scopeRegex + suffixRegex))) {
            String appendantPath = folder + "/" + appendantName;
            if (check(getInvoker(appendantPath), scope)) {
                appendantPaths.add(appendantPath);
            }
        }

        return appendantPaths;
    }

    @Override
    public List<String> findRequestAppendantPaths(String requestPath) {

        Path parsedPath = new Path(requestPath);

        String globalNameRegex = Pattern.quote(globalPrefix) + "[^\\.]*";
        String requestNameRegex = Pattern.quote(parsedPath.getName());
        String suffixRegex = "\\.(" + StringUtils.join("|", invokers.keySet()) + ")" + "\\.[^\\.]+$";

        List<String> appendantPaths = new ArrayList<String>();

        String requestFolder = parsedPath.getFolder();
        String folder = appendantFolder;
        for (String folderItem : requestFolder.equals("/") ? new String[]{""} : requestFolder.split("/")) {
            folder = folder + "/" + folderItem;
            for (String appendantName : sort(codeManager.listNames(folder, globalNameRegex + suffixRegex))) {
                appendantPaths.add(folder + "/" + appendantName);
            }
        }
        for (String appendantName : sort(codeManager.listNames(folder, requestNameRegex + suffixRegex))) {
            appendantPaths.add(folder + "/" + appendantName);
        }

        return appendantPaths;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean invoke(String codePath, Scope scope) throws GlowwormException {

        if (!codeManager.exist(codePath)) {
            throw new GlowwormException("code " + codePath + " does not exist.");
        }

        Code code = codeManager.get(codePath);
        if (Cache.get(scope).cached(code)) {
            return true;
        } else {
            Cache.get(scope).cache(code);
        }

        Invoker invoker = getInvoker(codePath);
        if (invoker == null) {
            throw new GlowwormException("invoker for " + codePath + " does not exist.");
        }

        return invoker.invoke(codePath, scope);
    }

    protected Invoker getInvoker(String path) {
        String type = new Path(path).getAppendantRole();
        return invokers.get(invokers.containsKey(type) ? type : "default");
    }

    protected boolean check(Invoker invoker, Scope scope) {
        for (Method m : invoker.getClass().getDeclaredMethods()) {
            if (m.getName().equals("invoker") && !Modifier.isVolatile(m.getModifiers())) {
                Class<?>[] pts = m.getParameterTypes();
                if (pts.length == 2 && pts[0] == String.class && pts[1].isAssignableFrom(scope.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected List<String> sort(Collection<String> names) {
        List<String> sorted = new ArrayList<String>(names);
        Collections.sort(sorted, new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                return getPriority(p2) - getPriority(p1);
            }
        });
        return sorted;
    }

    protected int getPriority(String name) {
        String type = new Path(name).getAppendantRole();
        return invokersOrder.contains(type) ? invokersOrder.indexOf(type) : Integer.MIN_VALUE;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setAppendantFolder(String appendantFolder) {
        this.appendantFolder = appendantFolder;
    }

    public void setInvokers(Map<String, Invoker> invokers) {
        this.invokers = invokers;
    }

    public void setInvokersOrder(List<String> invokersOrder) {
        this.invokersOrder = invokersOrder;
    }

    public void setGlobalPrefix(String globalPrefix) {
        this.globalPrefix = globalPrefix;
    }
}
