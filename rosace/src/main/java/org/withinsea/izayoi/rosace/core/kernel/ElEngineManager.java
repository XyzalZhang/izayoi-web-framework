package org.withinsea.izayoi.rosace.core.kernel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-3
 * Time: 上午12:38
 */
public class ElEngineManager {

    protected Map<String, ElEngine> elEngines = new HashMap<String, ElEngine>();

    public ElEngine lookupElEngine(String type) {
        return elEngines.get(type);
    }

    public void registerElEngine(String type, ElEngine elEngine) {
        elEngines.put(type, elEngine);
    }
}
