package org.withinsea.izayoi.cloister.core.feature.dynapage;

import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-24
 * Time: 下午5:25
 */
public class DynapageManager {

    protected DynapageEngineManager dynapageEngineManager;

    public Environment.Codefile lookupDynapage(Environment environment, String path) {
        Set<String> supportedTypes = dynapageEngineManager.getDynapageEngineNames();
        String type = new DynapagePath(path).getExtname();
        return (supportedTypes.contains(type) && environment.exist(path)) ? environment.getCodefile(path) : null;
    }

    public DynapageEngineManager getDynapageEngineManager() {
        return dynapageEngineManager;
    }

    public void setDynapageEngineManager(DynapageEngineManager dynapageEngineManager) {
        this.dynapageEngineManager = dynapageEngineManager;
    }
}
