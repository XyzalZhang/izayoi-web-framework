package org.withinsea.izayoi.cloister.core.feature.dynapage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-24
 * Time: 下午5:25
 */
public class DynapageEngineManager {

    protected Map<String, DynapageEngine> dynapageEngines = new HashMap<String, DynapageEngine>();

    public DynapageEngine lookupDynapageEngine(String type) {
        return dynapageEngines.get(type);
    }

    public void registerDynapageEngine(String type, DynapageEngine dynapageEngine) {
        dynapageEngines.put(type, dynapageEngine);
    }

    public Set<String> getDynapageEngineNames() {
        return dynapageEngines.keySet();
    }
}
