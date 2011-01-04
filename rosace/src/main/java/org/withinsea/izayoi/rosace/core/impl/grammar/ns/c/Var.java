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

package org.withinsea.izayoi.rosace.core.impl.grammar.ns.c;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.common.util.StringUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 23:09:11
 */
public class Var implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        String attrname = attr.getName();
        return attrname.startsWith("var.");
    }

    @Override
    public void processAttr(Attribute attr) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();

        Element elem = attr.getParent();
        String attrname = attr.getName();
        String attrvalue = attr.getValue();

        String varname = attrname.substring("var.".length());

        String value = attrvalue.trim();
        boolean embedded = !(attrvalue.startsWith("${") && attrvalue.endsWith("}")) || attrvalue.indexOf("${", 1) > 0;
        String compiledValue = embedded
                ? precompileEmbeddedELs(engine, attrvalue)
                : engine.precompileEl(value.substring(2, value.length() - 1).trim());

        String preScriptlet = engine.precompileOpenScope() + engine.precompilePut(varname, compiledValue) + engine.precompileOpenScope();
        String sufScriptlet = engine.precompileCloseScope() + engine.precompileCloseScope();

        try {
            DomUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new RosaceException(e);
        }

        attr.detach();
    }

    protected String precompileEmbeddedELs(final DomTemplateEngine engine, String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + engine.precompileEl(groups[1].replace("\\}", "}"), true) + "+ \"";
            }
        }) + "\"";
    }
}