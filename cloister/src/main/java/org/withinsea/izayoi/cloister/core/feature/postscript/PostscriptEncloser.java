package org.withinsea.izayoi.cloister.core.feature.postscript;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.kernal.CloisterConstants;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.TimestampCache;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-24
 * Time: 下午1:01
 */
public class PostscriptEncloser {

    protected RoleEngineManager roleEngineManager;
    protected ScriptEngineManager scriptEngineManager;

    protected boolean ignoreUnsupported = true;

    @SuppressWarnings("unchecked")
    public void enclose(Environment.Codefile postscript, String entrance, Map<String, Object> context) throws CloisterException {

        PostscriptPath postscriptPath = new PostscriptPath(postscript.getPath());
        if (postscriptPath.isFolder() || !postscriptPath.isPostscript()) {
            throw new CloisterException("not a postscript: " + postscriptPath.getPath());
        }

        RoleEngine roleEngine = roleEngineManager.lookupRoleEngine(postscriptPath.getRoleTypename());
        if (roleEngine == null) {
            if (ignoreUnsupported) {
                return;
            } else {
                throw new CloisterException("unsupported role type: " + postscriptPath.getPath());
            }
        }

        ScriptEngine scriptEngine = scriptEngineManager.lookupScriptEngine(postscriptPath.getScriptTypename());
        if (scriptEngine == null) {
            if (ignoreUnsupported) {
                return;
            } else {
                throw new CloisterException("unsupported script type: " + postscriptPath.getScriptTypename());
            }
        }

        ScriptEngine.CompiledScript compiledScript;
        {
            Environment environment = postscript.getEnvironment();
            TimestampCache<ScriptEngine.CompiledScript> modifiedCache = TimestampCache.getCache(
                    environment.getAttributes(), CloisterConstants.ATTR_COMPILED_SCRIPT_CACHE);
            String cacheKey = postscriptPath.getPath();

            if (modifiedCache.isModified(cacheKey, postscript.getLastModified())) {
                compiledScript = scriptEngine.compile(postscript);
                modifiedCache.put(cacheKey, compiledScript, postscript.getLastModified());
            } else {
                compiledScript = modifiedCache.get(cacheKey);
            }
        }

        Object data = (entrance == null)
                ? compiledScript.run(context)
                : compiledScript.run(context, entrance);

        roleEngine.process(data, context);
    }

    public boolean isIgnoreUnsupported() {
        return ignoreUnsupported;
    }

    public void setIgnoreUnsupported(boolean ignoreUnsupported) {
        this.ignoreUnsupported = ignoreUnsupported;
    }

    public RoleEngineManager getRoleEngineManager() {
        return roleEngineManager;
    }

    public void setRoleEngineManager(RoleEngineManager roleEngineManager) {
        this.roleEngineManager = roleEngineManager;
    }

    public ScriptEngineManager getScriptEngineManager() {
        return scriptEngineManager;
    }

    public void setScriptEngineManager(ScriptEngineManager scriptEngineManager) {
        this.scriptEngineManager = scriptEngineManager;
    }
}
