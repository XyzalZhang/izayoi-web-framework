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
import org.withinsea.izayoi.commons.xml.DOM4JUtils;
import org.withinsea.izayoi.commons.xml.JSPWriter;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:04:16
 */
public class Def implements AttrGrammar {

    @Override
    public boolean acceptAttr(Element elem, Attribute attr) {
        return attr.getName().equals("def");
    }

    @Override
    public void processAttr(DOMCompiler compiler, Compilr.Result result, Element elem, Attribute attr) throws CortileException {
        try {
            String funcPath = compiler.mapTargetPath(result.getTemplatePath(), attr.getValue());
            attr.detach();
            Element range = DOM4JUtils.surroundBy(elem, JSPWriter.ANONYMOUS_TAG_NAME);
            compiler.compileTo(result, funcPath, range);
            DOM4JUtils.replaceBy(range, "<% varstack.push(); %><jsp:include page=\"" + funcPath + "\" flush=\"true\" /><% varstack.pop(); %>");
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }
}
