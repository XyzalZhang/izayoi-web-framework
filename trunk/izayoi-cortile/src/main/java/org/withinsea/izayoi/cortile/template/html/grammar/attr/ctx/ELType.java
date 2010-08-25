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

package org.withinsea.izayoi.cortile.template.html.grammar.attr.ctx;

import org.dom4j.Attribute;
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.HTMLCompileContextUtils;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-6
 * Time: 22:48:37
 */
public class ELType implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("elType");
    }

    @Override
    public void processAttr(Attribute attr) throws CortileException {
        CompileContext ctx = CompileContext.get();
        String elType = attr.getValue();
        ctx.setScopeAttribute(HTMLCompileContextUtils.ELType_ATTR, elType);
        attr.detach();
    }

}
