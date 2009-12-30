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

package org.withinsea.izayoi.cortile.jsp.grammar.el;

import org.dom4j.Branch;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.dom.BranchGrammar;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.jsp.grammar.core.Imprts;
import org.withinsea.izayoi.cortile.util.DOMUtils;
import org.withinsea.izayoi.cortile.util.Varstack;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:52:10
 */
public class MVEL2 extends EL implements BranchGrammar {

    @Override
    public boolean acceptBranch(Branch branch) {
        return true;
    }

    @Override
    public void processBranch(DOMCompiler compiler, Compilr.Result result, Branch branch) throws CortileException {
        try {
            String intrpName = elInterpreter.getClass().getCanonicalName();
            String imports = Imprts.getImports(result);
            if (!imports.equals("")) {
                String script = "\"" + imports.replaceAll("\\\"", "\\\"") + "\"";
                DOMUtils.prepend(branch, "<% " + intrpName + ".imports(" + script + ", pageContext); %>");
            }
            DOMUtils.prepend(branch, "<% " + Varstack.class.getCanonicalName() + " varstack = " + intrpName + ".varstack(pageContext); %>");
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }
}
