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

import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.interpreter.ImportableInterpreter;
import org.withinsea.izayoi.core.interpreter.Interpreter;

import javax.script.Bindings;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-9
 * Time: 3:57:24
 */
public class DefaultInterpretManager implements InterpretManager {

    protected Map<String, Interpreter> interpreters;

    @Override
    public Object interpret(String script, Bindings bindings, String asType, String... importedClasses) throws IzayoiException {

        Interpreter interpreter = interpreters.get(interpreters.containsKey(asType) ? asType : "default");

        if (asType == null || interpreter == null) {
            return null;
        } else if (interpreter instanceof ImportableInterpreter) {
            return ((ImportableInterpreter) interpreter).interpret(script, bindings, asType, importedClasses);
        } else if (importedClasses.length == 0) {
            return interpreter.interpret(script, bindings, asType);
        } else {
            return null;
        }
    }

    public void setInterpreters(Map<String, Interpreter> interpreters) {
        this.interpreters = interpreters;
    }
}
