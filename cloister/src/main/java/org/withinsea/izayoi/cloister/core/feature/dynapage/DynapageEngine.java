package org.withinsea.izayoi.cloister.core.feature.dynapage;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.io.Writer;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-24
 * Time: 下午5:25
 */
public interface DynapageEngine {

    CompiledDynapage compile(Environment.Codefile dynapage) throws CloisterException;

    public static interface CompiledDynapage {

        void render(Writer writer, Map<String, Object> context) throws CloisterException;
    }
}
