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

package org.withinsea.izayoi.cortile.jsp;

import org.withinsea.izayoi.cortile.core.compiler.CompilerUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.Grammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.jsp.grammar.core.el.EL;

import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-24
 * Time: 14:11:10
 */
public class ELTextCompiler implements Compilr {

    protected String encoding;
    protected String targetPath;

    protected Map<String, Set<Grammar>> grammars;

    @Override
    public String mapEntrancePath(String templatePath) {
        String folder = "/" + targetPath.trim().replaceAll("^/|/$", "");
        return folder + templatePath + ".jsp";
    }

    @Override
    public Compilr.Result compile(String templatePath, String templateCode) throws CortileException {
        String jspHeader = "<%@ page pageEncoding=\"" + encoding + "\" %>";
        Result result = new Result(templatePath);
        for (EL g : CompilerUtils.sort(grammars, EL.class, "acceptString")) {
            if (g.acceptString(templateCode)) {
                templateCode = g.processString(this, result, templateCode);
            }
        }
        result.getTargets().put(mapEntrancePath(templatePath), jspHeader + templateCode);
        return result;
    }

    @SuppressWarnings("unused")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @SuppressWarnings("unused")
    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    @SuppressWarnings("unused")
    public void setGrammars(Map<String, Set<Grammar>> grammars) {
        this.grammars = grammars;
    }
}