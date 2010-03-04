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

import org.dom4j.Branch;
import org.dom4j.Document;
import org.withinsea.izayoi.commons.html.HTMLReader;
import org.withinsea.izayoi.commons.html.HTMLWriter;
import org.withinsea.izayoi.commons.util.Varstack;
import org.withinsea.izayoi.core.conf.IzayoiConfig;
import org.withinsea.izayoi.cortile.core.compiler.ELHelper;
import org.withinsea.izayoi.cortile.core.compiler.dom.DOMCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 17:39:51
 */
public class HTMLCompiler extends DOMCompiler {

    protected String encoding;
    protected String targetPath;
    protected String retrievalKey;

    // el compiler

    @Override
    public String compileELInit(String classes) {
        String elHelperInit = ELHelper.class.getCanonicalName() + ".Helper elHelper = " +
                IzayoiConfig.class.getCanonicalName() +
                ".retrieval(request.getServletContext(), \"" + retrievalKey + "\")" +
                ".getComponent(" + ELHelper.class.getCanonicalName() + ".class)" +
                ".getHelper(request);";
        String varstackInit = Varstack.class.getCanonicalName() + " varstack = " +
                "elHelper.getVarstack();";
        String importsInit = "elHelper.imports(\"" + classes + "\");";
        return elHelperInit + varstackInit + importsInit;
    }

    @Override
    public String compileEL(String el) {
        return "elHelper.eval(\"" + el.replace("\n", "").replace("\r", "") + "\")";
    }

    // dom compiler

    @Override
    public String mapEntrancePath(String templatePath) {
        return mapTargetPath(templatePath);
    }

    @Override
    public String mapTargetPath(String path, String suffix) {
        String folder = "/" + targetPath.trim().replaceAll("^/|/$", "");
        suffix = (suffix == null || "".equals(suffix)) ? "" : "$" + suffix;
        return folder + path + suffix + ".jsp";
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
        String jspHeader = "<%@ page " +
                "contentType=\"text/html; charset=" + encoding + "\" " +
                "pageEncoding=\"" + encoding + "\" %>";
        StringWriter buf = new StringWriter();
        try {
            new HTMLWriter(buf).write(root);
        } catch (IOException e) {
            throw new CortileException(e);
        }
        return jspHeader + buf.toString();
    }

    // dependency

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public void setRetrievalKey(String retrievalKey) {
        this.retrievalKey = retrievalKey;
    }
}
