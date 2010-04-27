package org.withinsea.izayoi.glowworm.core.invoke;

import org.withinsea.izayoi.core.bindings.scope.Scope;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.invoker.Invoker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 22:48:27
 */
public class DefaultInvokeManager implements InvokeManager {

    protected static final String LAST_MODIFIED_ATTR = Cache.class.getCanonicalName() + ".LAST_MODIFIED";

    protected class Cache {

        protected boolean cached(HttpServletRequest request, HttpServletResponse response, ScriptPath scriptPath) throws GlowwormException {
            Long lastModified = getScope(scriptPath).getBean(request, response, LAST_MODIFIED_ATTR);
            return (lastModified != null && lastModified.equals(codeManager.get(scriptPath.getPath()).getLastModified()));
        }

        protected void cache(HttpServletRequest request, HttpServletResponse response, ScriptPath scriptPath) throws GlowwormException {
            getScope(scriptPath).setBean(request, response, LAST_MODIFIED_ATTR, codeManager.get(scriptPath.getPath()).getLastModified());
        }
    }

    protected Cache cache = new Cache();

    protected CodeManager codeManager;
    protected Map<String, Invoker> invokers;
    protected List<String> invokersOrder;
    protected Map<String, Scope> scopes;
    protected List<String> scopesOrder;

    @Override
    public boolean isScript(String scriptPath) {
        ScriptPath parsedPath = new ScriptPath(scriptPath);
        return !parsedPath.getRole().equals("")
                && invokers.containsKey(parsedPath.getInvokerType())
                && scopes.containsKey(parsedPath.getScopeType());
    }

    @Override
    public boolean invoke(HttpServletRequest request, HttpServletResponse response, Collection<String> scriptPaths) throws GlowwormException {

        for (ScriptPath parsedPath : sort(scriptPaths)) {

            if (!codeManager.exist(parsedPath.getPath())) {
                throw new GlowwormException("script " + parsedPath.getPath() + " does not exist.");
            }

            if (!cache.cached(request, response, parsedPath)) {
                Invoker invoker = getInvoker(parsedPath);
                Scope scope = getScope(parsedPath);
                Code code = codeManager.get(parsedPath.getPath());
                boolean toContinue = invoker.process(request, response, code, parsedPath.getType(), scope);
                cache.cache(request, response, parsedPath);
                if (!toContinue) {
                    return false;
                }
            }
        }

        return true;
    }

    protected List<ScriptPath> sort(Collection<String> scriptPaths) {
        List<ScriptPath> sorted = new ArrayList<ScriptPath>();
        for (String scriptPath : scriptPaths) {
            sorted.add(new ScriptPath(scriptPath));
        }
        Collections.sort(sorted, new Comparator<ScriptPath>() {
            @Override
            public int compare(ScriptPath p1, ScriptPath p2) {
                int ret = getPriority(p2.getScopeType(), scopesOrder) - getPriority(p1.getScopeType(), scopesOrder);
                if (ret == 0)
                    ret = getPriority(p2.getInvokerType(), invokersOrder) - getPriority(p1.getInvokerType(), invokersOrder);
                return ret;
            }
        });
        return sorted;
    }

    protected int getPriority(String name, List<String> order) {
        return order.contains(name) ? order.indexOf(name) : Integer.MIN_VALUE;
    }

    protected Invoker getInvoker(ScriptPath parsedPath) throws GlowwormException {
        Invoker invoker = invokers.get(invokers.containsKey(parsedPath.getInvokerType()) ? parsedPath.getInvokerType() : "default");
        if (invoker == null) {
            throw new GlowwormException("invoker type " + parsedPath.getInvokerType() + " does not exist.");
        }
        return invoker;
    }

    protected Scope getScope(ScriptPath parsedPath) throws GlowwormException {
        Scope scope = scopes.get(scopes.containsKey(parsedPath.getScopeType()) ? parsedPath.getScopeType() : "default");
        if (scope == null) {
            throw new GlowwormException("scope type " + parsedPath.getScopeType() + " does not exist.");
        }
        return scope;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }

    public void setInvokers(Map<String, Invoker> invokers) {
        this.invokers = invokers;
    }

    public void setInvokersOrder(List<String> invokersOrder) {
        this.invokersOrder = invokersOrder;
    }

    public void setScopes(Map<String, Scope> scopes) {
        this.scopes = scopes;
    }

    public void setScopesOrder(List<String> scopesOrder) {
        this.scopesOrder = scopesOrder;
    }
}
