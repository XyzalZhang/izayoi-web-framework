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

package org.withinsea.izayoi.cortile.template.compiler.dom;

import org.dom4j.Text;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.compiler.Compilr;
import org.withinsea.izayoi.cortile.template.compiler.grammar.Grammar;
import org.withinsea.izayoi.cortile.template.compiler.grammar.GrammarCompiler;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 15:29:06
 */
public interface TextGrammar<C extends GrammarCompiler> extends Grammar<C> {

    public boolean acceptText(Text text);

    public abstract void processText(C compiler, Compilr.Result result, Text text) throws CortileException;
}