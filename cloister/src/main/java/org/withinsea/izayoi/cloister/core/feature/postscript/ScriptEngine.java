package org.withinsea.izayoi.cloister.core.feature.postscript;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-15
 * Time: 上午9:52
 */
public interface ScriptEngine {

    CompiledScript compile(Environment.Codefile postscript) throws CloisterException;

    public static interface CompiledScript {

        <T> T run(Map<String, Object> context) throws CloisterException;

        <T> T run(Map<String, Object> context, String entrance) throws CloisterException;
    }
}
