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

import javax.script.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-4
 * Time: 13:59:24
 */
public class JSR223 implements Interpreter, CompilableInterpreter, MultiTypeInterpreter {

    @Override
    public boolean supportType(String type) {
        return ScriptHelper.supportType(type);
    }

    @Override
    public CompiledInterpreter compile(Code code, String... importedClasses) throws IzayoiException {
        try {
            return ScriptHelper.compile(code.getCode(), code.getType());
        } catch (ScriptException e) {
            throw new IzayoiException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {
        try {
            return (T) ScriptHelper.eval(code.getCode(), code.getType(), bindings);
        } catch (ScriptException e) {
            throw new IzayoiException(e);
        }
    }

    // lazy load Script Engine

    protected static class ScriptHelper {

        protected static class JSR233CompilableInterpreter implements CompiledInterpreter {

            protected final CompiledScript script;

            public JSR233CompilableInterpreter(CompiledScript script) {
                this.script = script;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <T> T interpret(Bindings bindings) throws IzayoiException {
                try {
                    return (T) script.eval(bindings);
                } catch (ScriptException e) {
                    throw new IzayoiException(e);
                }
            }
        }

        public static boolean supportType(String type) {
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension(type);
            return (engine != null);
        }

        public static Object eval(String script, String asType, Bindings bindings) throws ScriptException {
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension(asType);
            return (engine == null) ? null : engine.eval(script, bindings);
        }

        public static CompiledInterpreter compile(String script, String asType) throws ScriptException {
            ScriptEngine engine = new ScriptEngineManager().getEngineByExtension(asType);
            return (engine == null || !(engine instanceof Compilable)) ? null :
                    new JSR233CompilableInterpreter(((Compilable) engine).compile(script));
        }
    }
}