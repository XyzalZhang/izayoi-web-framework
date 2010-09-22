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
import org.withinsea.izayoi.core.code.CodeContainer;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 6:03:55
 */
public class DefaultInvokeManager implements InvokeManager {


    @Resource
    CodeContainer codeContainer;

    @Resource
    String appendantFolder;

    @Resource
    String folderPrefix;

    @Resource
    Map<String, Invoker> invokers;

    @Resource
    List<String> invokersOrder;


    @Override
    public boolean isAppendant(String path) {
        Path parsedPath = new Path(path);
        return parsedPath.isAppendant() && invokers.containsKey(parsedPath.getAppendantRole());
    }

    @Override
    public List<String> findScopedAppendantPaths(String scopeName, Scope scope) {

        String scopeRegex = Pattern.quote("@" + scopeName) + "(-[^\\.]+)?";
        String suffixRegex = "\\.(" + StringUtils.join("|", invokers.keySet()) + ")(-mock)?" + "\\.[^\\.]+$";

        List<String> appendantPaths = new ArrayList<String>();

        String folder = appendantFolder;
        for (String appendantName : sort(codeContainer.listNames(folder, scopeRegex + suffixRegex))) {
            String appendantPath = folder + "/" + appendantName;
            appendantPaths.add(appendantPath);
        }

        return cleanPaths(appendantPaths);
    }

    @Override
    public List<String> findRequestAppendantPaths(String requestPath) {

        Path parsedPath = new Path(requestPath);

        String globalNameRegex = Pattern.quote(folderPrefix) + "(-[^\\.]+)?";
        String requestNameRegex = Pattern.quote(parsedPath.getName());
        String suffixRegex = "\\.(" + StringUtils.join("|", invokers.keySet()) + ")(-mock)?" + "\\.[^\\.]+$";

        List<String> appendantPaths = new ArrayList<String>();

        String requestFolder = parsedPath.getFolder();
        String folder = appendantFolder;
        for (String folderItem : requestFolder.equals("/") ? new String[]{""} : requestFolder.split("/")) {
            folder = folder + "/" + folderItem;
            for (String appendantName : sort(codeContainer.listNames(folder, globalNameRegex + suffixRegex))) {
                appendantPaths.add(folder + "/" + appendantName);
            }
        }
        for (String appendantName : sort(codeContainer.listNames(folder, requestNameRegex + suffixRegex))) {
            appendantPaths.add(folder + "/" + appendantName);
        }

        return cleanPaths(appendantPaths);
    }

    protected List<String> cleanPaths(List<String> appendantPaths) {

        if (appendantPaths.isEmpty()) return appendantPaths;

        List<Path> parsedPaths = new ArrayList<Path>(appendantPaths.size());
        for (String appendantPath : appendantPaths) {
            parsedPaths.add(new Path(appendantPath));
        }

        for (int i = 0; i < parsedPaths.size() - 1; i++) {
            Path pathI = parsedPaths.get(i);
            if (pathI != null) {
                for (int j = i + 1; j < parsedPaths.size(); j++) {
                    Path pathJ = parsedPaths.get(j);
                    if (pathJ != null && pathI.getMainName().equals(pathJ.getMainName())) {
                        if (pathJ.getAppendantRole().equals(pathI.getAppendantRole() + "-mock")) {
                            parsedPaths.set(j, null);
                        } else if (pathI.getAppendantRole().equals(pathJ.getAppendantRole() + "-mock")) {
                            parsedPaths.set(i, null);
                            break;
                        }
                    }
                }
            }
        }

        List<String> cleanPaths = new ArrayList<String>();
        for (Path parsedPath : parsedPaths) {
            if (parsedPath != null) {
                cleanPaths.add(parsedPath.getPath());
            }
        }

        return cleanPaths;
    }


    @Override
    @SuppressWarnings("unchecked")
    public boolean invoke(HttpServletRequest request, HttpServletResponse response, String codePath, Scope scope) throws GlowwormException {

        if (!codeContainer.exist(codePath)) {
            throw new GlowwormException("code " + codePath + " does not exist.");
        }

        Code code = codeContainer.get(codePath);
        if (Cache.get(scope).cached(code)) {
            return true;
        } else {
            Cache.get(scope).cache(code);
        }

        Invoker invoker = getInvoker(codePath);
        if (invoker == null) {
            throw new GlowwormException("invoker for " + codePath + " does not exist.");
        }

        return invoker.invoke(request, response, codePath, scope);
    }

    protected Invoker getInvoker(String path) {
        String type = new Path(path).getAppendantRole().replaceAll("-mock$", "");
        return invokers.get(invokers.containsKey(type) ? type : "default");
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


    protected static class Cache extends HashMap<String, Long> {

        private static final long serialVersionUID = 3613991425340677036L;

        protected static final String LAST_MODIFIED_ATTR = Cache.class.getCanonicalName() + ".LAST_MODIFIED";

        public static synchronized Cache get(Scope scope) throws GlowwormException {
            Cache lastModifieds = scope.getDeclaredScope().getAttribute(LAST_MODIFIED_ATTR);
            if (lastModifieds == null) {
                lastModifieds = new Cache();
                scope.setAttribute(LAST_MODIFIED_ATTR, lastModifieds);
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
}
