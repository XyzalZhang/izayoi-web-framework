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

package org.withinsea.izayoi.cortile.core.compile.el;

import org.withinsea.izayoi.commons.util.StringUtils;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-7
 * Time: 2:38:06
 */
public abstract class JavaELSupportedCompiler implements ELSupportedCompiler {

    protected abstract String elHelper();

    @Override
    public String elInit() {
        return ELHelper.class.getCanonicalName() + " elHelper = " + elHelper() + ";";
    }

    @Override
    public String el(String el, boolean forOutput, String elType, String... imports) {
        String elCode = "\"" + el.replace("\n", "").replace("\r", "") + "\"";
        String typeCode = (elType == null) ? "null" : "\"" + elType + "\"";
        String importsCode = StringUtils.join(",", imports);
        return "elHelper.eval(" + elCode + "," + forOutput + "," + typeCode +
                (imports.length == 0 ? "" : "," + importsCode) + ")";
    }

    @Override
    public String elBind(String key, String valueCode) {
        return "elHelper.bind(\"" + key + "\", " + valueCode + ");";
    }

    @Override
    public String openScope() {
        return openScope(null);
    }

    @Override
    public String openScope(String bindingsCode) {
        bindingsCode = (bindingsCode == null) ? "" : bindingsCode;
        return "elHelper.openScope(" + bindingsCode + ");";
    }

    @Override
    public String closeScope() {
        return "elHelper.closeScope();";
    }
}