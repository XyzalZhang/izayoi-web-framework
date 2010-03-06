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

package org.withinsea.izayoi.cortile.jsp.grammar.core.attr;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.html.DOMUtils;
import org.withinsea.izayoi.commons.util.BeanMap;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.jsp.HTMLCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-4
 * Time: 19:41:58
 */
public class With implements AttrGrammar<HTMLCompiler> {

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return attr.getName().equals("with");
    }

    @Override
    public void processAttr(HTMLCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {


        String el = attr.getValue().trim();
        el = (el.startsWith("${") && el.endsWith("}")) ? el.substring(2, el.length() - 1).trim() : el;
        if (el.equals("") || el.indexOf("${") > 0 || el.matches(".*[^\\\\]}.*")) {
            throw new CortileException("\"" + attr.getValue() + "\" is not a valid EL script.");
        }

        String scopeBegin = compiler.elScope(),
                scopeEnd = compiler.elScopeEnd(),
                beanScopeBegin = compiler.elScope(null, "new " + BeanMap.class.getCanonicalName() + "(" + compiler.compileEL(el) + ")");
        try {
            DOMUtils.surroundBy(elem, "<%" + beanScopeBegin + scopeBegin + "%>", "<%" + scopeEnd + scopeEnd + "%>");
        } catch (Exception e) {
            throw new CortileException(e);
        }

        attr.detach();
    }
}