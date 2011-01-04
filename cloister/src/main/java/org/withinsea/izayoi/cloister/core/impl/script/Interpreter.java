package org.withinsea.izayoi.cloister.core.impl.script;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.feature.postscript.ScriptEngine;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.common.util.IOUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-3
 * Time: 上午8:44
 */
public abstract class Interpreter implements ScriptEngine {

    abstract protected Object eval(String script, Map<String, Object> context);

    abstract protected Object eval(String script, Map<String, Object> context, String entrance);

    protected String encoding;

    @Override
    public CompiledScript compile(Environment.Codefile postscript) throws CloisterException {
        try {
            return new LoadedScript(IOUtils.toString(postscript.getInputStream(), encoding));
        } catch (IOException e) {
            throw new CloisterException(e);
        }
    }

    protected class LoadedScript implements CompiledScript {

        protected String script;

        public LoadedScript(String script) {
            this.script = script;
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T run(Map<String, Object> context) {
            return (T) eval(script, context);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T run(Map<String, Object> context, String entrance) {
            return (T) eval(script, context, entrance);
        }
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
}
