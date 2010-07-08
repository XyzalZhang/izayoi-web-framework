/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF
 *
 * ANY KIND, either express or implied. See the License for the specific language governing rights and
 *
 * limitations under the License.
 *
 * The Original Code is the IZAYOI web framework.
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.core.interpret;

import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.exception.IzayoiException;

import javax.script.Bindings;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-9
 * Time: 3:57:24
 */
public class DefaultInterpretManager implements InterpretManager {

    protected static class Cache {
        public Map<String, Long> lastModified = new HashMap<String, Long>();
        public Map<String, CompilableInterpreter.CompiledInterpreter> interpreter = new HashMap<String, CompilableInterpreter.CompiledInterpreter>();
    }

    protected Cache cache = new Cache();

    protected Map<String, Interpreter> interpreters;
    protected List<MultiTypeInterpreter> defaultInterpreters;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {

        String type = code.getType();
        if (type == null) {
            return null;
        }

        Interpreter interpreter = interpreters.get(type);
        if (interpreter == null) {
            for (MultiTypeInterpreter defaultInterpreter : defaultInterpreters) {
                if (defaultInterpreter.supportType(type)) {
                    interpreter = defaultInterpreter;
                    break;
                }
            }
        }
        if (interpreter == null) {
            return null;
        }

        return (T) interpret(interpreter, code, bindings, importedClasses);
    }

    protected Object interpret(Interpreter interpreter, Code code, Bindings bindings, String... importedClasses) throws IzayoiException {

        if (code.getPath() == null || !(interpreter instanceof CompilableInterpreter)) {
            return interpreter.interpret(code, bindings, importedClasses);
        }

        String key = code.getPath().getPath();
        CompilableInterpreter.CompiledInterpreter compiledInterpreter = cache.interpreter.get(key);
        if (compiledInterpreter != null && cache.lastModified.get(key) >= code.getLastModified()) {
            return compiledInterpreter.interpret(bindings);
        }

        CompilableInterpreter compilableInterpreter = (CompilableInterpreter) interpreter;
        compiledInterpreter = compilableInterpreter.compile(code);
        if (compiledInterpreter != null) {
            cache.interpreter.put(key, compiledInterpreter);
            cache.lastModified.put(key, code.getLastModified());
            return compiledInterpreter.interpret(bindings);
        }

        return interpreter.interpret(code, bindings, importedClasses);
    }

    public void setInterpreters(Map<String, Interpreter> interpreters) {
        this.interpreters = interpreters;
    }

    public void setDefaultInterpreters(List<MultiTypeInterpreter> defaultInterpreters) {
        this.defaultInterpreters = defaultInterpreters;
    }
}