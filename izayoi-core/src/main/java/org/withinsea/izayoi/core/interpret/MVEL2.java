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

import org.mvel2.MVEL;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.exception.IzayoiException;

import javax.script.Bindings;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-4
 * Time: 13:59:24
 */
public class MVEL2 implements Interpreter {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {
        String importsEl = "";
        for (String claz : importedClasses) {
            importsEl += "import " + claz + ";";
        }
        return (T) MVEL.eval(importsEl + code.getCode(), bindings);
    }
}