package org.withinsea.izayoi.core.invoke;

import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.Scope;

import java.util.HashMap;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 6:03:55
 */
public abstract class ScopeInvokeManager implements InvokeManager {

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

    @Override
    @SuppressWarnings("unchecked")
    public boolean invoke(String codePath, Scope scope) throws IzayoiException {

        if (!codeManager.exist(codePath)) {
            throw new IzayoiException("code " + codePath + " does not exist.");
        }

        if (!isInvoked(codePath, scope)) {
            Code code = codeManager.get(codePath);
            Invoker invoker = getInvoker(codePath);
            if (invoker == null) {
                throw new IzayoiException("invoker for " + codePath + " does not exist.");
            }
            boolean toContinue = invoker.invoke(codePath, scope);
            Cache.get(scope).cache(code);
            if (!toContinue) {
                return false;
            }
        }

        return true;
    }

    protected boolean isInvoked(String codePath, Scope scope) throws IzayoiException {
        return Cache.get(scope).cached(codeManager.get(codePath));
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }
}
