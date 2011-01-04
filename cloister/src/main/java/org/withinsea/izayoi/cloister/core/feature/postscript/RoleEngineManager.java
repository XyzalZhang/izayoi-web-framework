package org.withinsea.izayoi.cloister.core.feature.postscript;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-24
 * Time: 15:32:07
 */
public class RoleEngineManager {

    protected Map<String, RoleEngine> roleEngines = new HashMap<String, RoleEngine>();

    public RoleEngine lookupRoleEngine(String type) {
        return roleEngines.get(type);
    }

    public void registerRoleEngine(String type, RoleEngine roleEngine) {
        roleEngines.put(type, roleEngine);
    }

    public Set<String> getRoleEngineNames() {
        return roleEngines.keySet();
    }
}
