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

package org.withinsea.izayoi.cortile.template.html;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.withinsea.izayoi.cortile.core.compile.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.compile.dom.Grammar;
import org.withinsea.izayoi.cortile.core.compile.el.ELSupportedCompiler;
import org.withinsea.izayoi.cortile.core.compile.jsp.ELJSPCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.parser.HTMLReader;
import org.withinsea.izayoi.cortile.template.html.parser.HTMLWriter;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:39:51
 */
public class HTMLCompiler extends DOMCompiler implements ELSupportedCompiler {

    @Resource
    String encoding;

    @Resource
    String outputFolder;

    @Resource
    String outputSuffix;

    @Resource
    String izayoiContainerRetrievalKey;

    @Resource
    Map<String, List<Grammar>> htmlGrammars;

    @Override
    protected Map<String, List<Grammar>> getGrammars() {
        return htmlGrammars;
    }

    // combine jsp compiler

    @Resource
    protected ELJSPCompiler elJspCompiler;

    // implement compiler

    @Override
    public String mapEntrancePath(String templatePath) {
        return elJspCompiler.mapEntrancePath(templatePath);
    }

    // implement dom compiler

    @Override
    protected String mapTargetPath(String path, String suffix) {
        suffix = (suffix == null) ? "" : "\\$" + suffix;
        return mapEntrancePath(path).replaceAll("\\.[^\\.]+$", suffix + "$0");
    }

    @Override
    protected Document parseTemplate(String templatePath, String templateCode) throws CortileException {
        try {
            return new HTMLReader().read(new StringReader(templateCode));
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }

    @Override
    public Result compile(String templatePath, String templateCode) throws CortileException {
        Result result = super.compile(templatePath, templateCode);
        for (Map.Entry<String, String> e : result.getTargets().entrySet()) {
            if (e.getKey().endsWith(".jsp")) {
                result.getTargets().put(e.getKey(), elJspCompiler.compileJSP(e.getValue()));
            }
        }
        return result;
    }

    @Override
    protected String buildTarget(Branch root) throws CortileException {
        StringWriter buf = new StringWriter();
        try {
            new HTMLWriter(buf).write(root);
        } catch (IOException e) {
            throw new CortileException(e);
        }
        return buf.toString();
    }

    // implement el supported compiler

    public String el(String el) {
        return el(el, false);
    }

    public String el(String el, boolean forOutput) {
        return el(el, forOutput, HTMLCompileContextUtils.getContextELType(), HTMLCompileContextUtils.getContextImports());
    }

    @Override
    public String el(String el, boolean forOutput, String elType, String... imports) {
        return elJspCompiler.el(el, forOutput, elType, imports);
    }

    @Override
    public String elInit() {
        return elJspCompiler.elInit();
    }

    @Override
    public String elBind(String key, String valueCode) {
        return elJspCompiler.elBind(key, valueCode);
    }

    @Override
    public String openScope() {
        return elJspCompiler.openScope();
    }

    @Override
    public String openScope(String bindingsCode) {
        return elJspCompiler.openScope(bindingsCode);
    }

    @Override
    public String closeScope() {
        return elJspCompiler.closeScope();
    }
}
