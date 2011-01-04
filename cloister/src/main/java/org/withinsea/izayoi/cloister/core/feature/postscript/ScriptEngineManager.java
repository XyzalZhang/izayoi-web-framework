package org.withinsea.izayoi.cloister.core.feature.postscript;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-24
 * Time: 15:32:07
 */
public class ScriptEngineManager {

    protected Map<String, ScriptEngine> scriptEngines  = new HashMap<String, ScriptEngine>();

    public ScriptEngine lookupScriptEngine(String type) {
        return scriptEngines.get(type);
    }

    public void registerScriptEngine(String type, ScriptEngine scriptEngine) {
        scriptEngines.put(type, scriptEngine);
    }

    public Set<String> getScriptEngineNames() {
        return scriptEngines.keySet();
    }
}
