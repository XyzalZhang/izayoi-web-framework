package org.withinsea.izayoi.cloister.core.impl.script;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.feature.postscript.ScriptEngine;
import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-3
 * Time: 上午9:53
 */
public abstract class StaticData implements ScriptEngine {

    abstract protected Object load(Environment.Codefile postscript) throws IOException;

    @Override
    public CompiledScript compile(Environment.Codefile postscript) throws CloisterException {
        try {
            return new LoadedData(load(postscript));
        } catch (IOException e) {
            throw new CloisterException(e);
        }
    }

    protected static class LoadedData implements CompiledScript {

        protected Object data;

        public LoadedData(Object data) {
            this.data = data;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T run(Map<String, Object> context) throws CloisterException {
            return (T) data;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T run(Map<String, Object> context, String entrance) throws CloisterException {
            return (T) data;
        }
    }
}
