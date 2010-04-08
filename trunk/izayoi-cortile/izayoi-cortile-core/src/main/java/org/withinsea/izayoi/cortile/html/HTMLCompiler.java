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

package org.withinsea.izayoi.cortile.html;

import org.dom4j.Branch;
import org.dom4j.Document;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.compiler.el.ELSupportedCompiler;
import org.withinsea.izayoi.cortile.core.compiler.java.JSPCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.html.parser.HTMLReader;
import org.withinsea.izayoi.cortile.html.parser.HTMLWriter;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:39:51
 */
public class HTMLCompiler extends DOMCompiler implements ELSupportedCompiler {

    // implement compiler

    @Override
    public String mapEntrancePath(String templatePath) {
        return jspCompiler.mapEntrancePath(templatePath);
    }

    // implement dom compiler

    @Override
    public String mapTargetPath(String path, String suffix) {
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
    protected String buildTarget(Branch root) throws CortileException {
        StringWriter buf = new StringWriter();
        try {
            new HTMLWriter(buf).write(root);
        } catch (IOException e) {
            throw new CortileException(e);
        }
        return jspCompiler.compileJSP(buf.toString());
    }

    // implement el supported compiler

    public String compileEL(String el) {
        return jspCompiler.compileEL(el);
    }

    @Override
    public String el(String el, boolean forOutput) {
        return jspCompiler.el(el, forOutput);
    }

    @Override
    public String elInit() {
        return jspCompiler.elInit();
    }

    @Override
    public String elImports(String classes) {
        return jspCompiler.elImports(classes);
    }

    public String elScope() {
        return jspCompiler.elScope();
    }

    public String elScope(String elType) {
        return jspCompiler.elScope(elType);
    }

    @Override
    public String elScope(String elType, String bindingsCode) {
        return jspCompiler.elScope(elType, bindingsCode);
    }

    @Override
    public String elBind(String key, String valueCode) {
        return jspCompiler.elBind(key, valueCode);
    }

    @Override
    public String elScopeEnd() {
        return jspCompiler.elScopeEnd();
    }

    // combine jsp compiler

    protected JSPCompiler jspCompiler = new JSPCompiler() {
        @Override
        public Result compile(String templatePath, String templateCode) throws CortileException {
            throw new UnsupportedOperationException();
        }
    };

    public void setComponentContainerRetrievalKey(String componentContainerRetrievalKey) {
        jspCompiler.setComponentContainerRetrievalKey(componentContainerRetrievalKey);
    }

    public void setEncoding(String encoding) {
        jspCompiler.setEncoding(encoding);
    }

    public void setTargetPath(String targetPath) {
        jspCompiler.setTargetPath(targetPath);
    }
}
