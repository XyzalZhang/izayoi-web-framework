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

package org.withinsea.izayoi.cortile.template.compiler.java;

import org.withinsea.izayoi.cortile.template.compiler.el.ELHelper;
import org.withinsea.izayoi.cortile.template.compiler.el.ELSupportedCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-7
 * Time: 2:38:06
 */
public abstract class JavaELSupportedCompiler implements ELSupportedCompiler {

    protected abstract String compileELHelperBuilding();

    @Override
    public String elInit() {
        return ELHelper.class.getCanonicalName() + ".Helper elHelper = " + compileELHelperBuilding() + ";";
    }

    @Override
    public String elImports(String classes) {
        return "elHelper.imports(\"" + classes + "\");";
    }

    public String compileEL(String el) {
        return el(el, false);
    }

    @Override
    public String el(String el, boolean forOutput) {
        return "elHelper.eval(\"" + el.replace("\n", "").replace("\r", "") + "\", " + forOutput + ")";
    }

    @Override
    public String elBind(String key, String valueCode) {
        return "elHelper.bind(\"" + key + "\", " + valueCode + ");";
    }

    public String elScope() {
        return elScope(null, null);
    }

    public String elScope(String elType) {
        return elScope(elType, null);
    }

    @Override
    public String elScope(String elType, String bindingsCode) {
        elType = (elType == null) ? "null" : "\"" + elType + "\"";
        bindingsCode = (bindingsCode == null) ? "null" : bindingsCode;
        return "elHelper.scope(" + elType + ", " + bindingsCode + ");";
    }

    @Override
    public String elScopeEnd() {
        return "elHelper.scopeEnd();";
    }
}
