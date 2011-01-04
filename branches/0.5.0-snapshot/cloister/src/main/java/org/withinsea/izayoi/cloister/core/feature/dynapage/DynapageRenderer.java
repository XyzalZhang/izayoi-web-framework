package org.withinsea.izayoi.cloister.core.feature.dynapage;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.kernal.CloisterConstants;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.TimestampCache;

import java.io.Writer;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-26
 * Time: 下午3:25
 */
public class DynapageRenderer {

    protected DynapageEngineManager dynapageEngineManager;

    public void render(Writer writer, Environment.Codefile dynapage, Map<String, Object> context) throws CloisterException {

        DynapagePath dynapagePath = new DynapagePath(dynapage.getPath());
        if (dynapagePath.isFolder()) {
            throw new CloisterException("not a dynapage: " + dynapagePath.getPath());
        }

        DynapageEngine dynapageEngine = dynapageEngineManager.lookupDynapageEngine(dynapagePath.getExtname());
        if (dynapageEngine == null) {
            throw new CloisterException("unsupported dynapage type: " + dynapagePath.getPath());
        }

        DynapageEngine.CompiledDynapage compiledDynapage;
        {
            Environment environment = dynapage.getEnvironment();
            TimestampCache<DynapageEngine.CompiledDynapage> modifiedCache = TimestampCache.getCache(
                    environment.getAttributes(), CloisterConstants.ATTR_COMPILED_DYNAPAGE_CACHE);
            String cacheKey = dynapagePath.getPath();

            if (modifiedCache.isModified(cacheKey, dynapage.getLastModified())) {
                compiledDynapage = dynapageEngine.compile(dynapage);
                modifiedCache.put(cacheKey, compiledDynapage, dynapage.getLastModified());
            } else {
                compiledDynapage = modifiedCache.get(cacheKey);
            }
        }

        compiledDynapage.render(writer, context);
    }

    public DynapageEngineManager getDynapageEngineManager() {
        return dynapageEngineManager;
    }

    public void setDynapageEngineManager(DynapageEngineManager dynapageEngineManager) {
        this.dynapageEngineManager = dynapageEngineManager;
    }
}
