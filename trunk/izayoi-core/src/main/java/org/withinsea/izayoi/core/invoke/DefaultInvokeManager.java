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

package org.withinsea.izayoi.core.invoke;

import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.Scope;
import org.withinsea.izayoi.core.scope.ScopeUtils;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 6:03:55
 */
public abstract class DefaultInvokeManager implements InvokeManager {

    protected static class Cache extends HashMap<String, Long> {

        protected static final String LAST_MODIFIED_ATTR = Cache.class.getCanonicalName() + ".LAST_MODIFIED";

        public static synchronized Cache get(Scope scope) throws IzayoiException {
            Cache lastModifieds = scope.getBean(LAST_MODIFIED_ATTR);
            if (lastModifieds == null) {
                lastModifieds = new Cache();
                scope.setBean(LAST_MODIFIED_ATTR, lastModifieds);
            }
            return lastModifieds;
        }

        public boolean cached(Code code) {
            return containsKey(code.getPath()) && (get(code.getPath()) >= code.getLastModified());
        }

        public void cache(Code code) {
            put(code.getPath(), code.getLastModified());
        }
    }

    protected CodeManager codeManager;

    protected abstract Invoker getInvoker(String path);

    protected abstract List<String> getInvokersOrder();

    @Override
    @SuppressWarnings("unchecked")
    public boolean invoke(String codePath, Scope scope) throws IzayoiException {

        if (!codeManager.exist(codePath)) {
            throw new IzayoiException("code " + codePath + " does not exist.");
        }

        Code code = codeManager.get(codePath);
        if (Cache.get(scope).cached(code)) {
            return true;
        } else {
            Cache.get(scope).cache(code);
        }

        Invoker invoker = getInvoker(codePath);
        if (invoker == null) {
            throw new IzayoiException("invoker for " + codePath + " does not exist.");
        }

        try {
            scope.setBean(ScopeUtils.CONTEXT_ATTR, scope);
        } catch (UnsupportedOperationException e) {
        }

        return invoker.invoke(codePath, scope);
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
        List<String> invokersOrder = getInvokersOrder();
        String type = new Path(name).getAppendantRole();
        return invokersOrder.contains(type) ? invokersOrder.indexOf(type) : Integer.MIN_VALUE;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }
}
