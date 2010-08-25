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

package org.withinsea.izayoi.cortile.template.html.grammar.attr.core;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.commons.util.BeanMap;
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-4
 * Time: 19:41:58
 */
public class With implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("with");
    }

    @Override
    public void processAttr(Attribute attr) throws CortileException {

        CompileContext ctx = CompileContext.get();
        HTMLCompiler compiler = ctx.getCompiler();

        Element elem = attr.getParent();
        String attrvalue = attr.getValue();

        String value = attrvalue.trim();
        String el = value.startsWith("${") ? value.substring(2, value.length() - 1).trim() : value;
        String beanMap = "new " + BeanMap.class.getCanonicalName() + "(" + compiler.el(el) + ")";

        String preScriptlet = compiler.openScope(beanMap) + compiler.openScope();
        String sufScriptlet = compiler.closeScope() + compiler.closeScope();

        try {
            DOMUtils.surroundBy(elem, "<%" + preScriptlet + "%>", "<%" + sufScriptlet + "%>");
        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }
}
