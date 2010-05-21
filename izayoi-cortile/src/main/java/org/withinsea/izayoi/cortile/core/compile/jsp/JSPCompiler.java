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

package org.withinsea.izayoi.cortile.core.compile.jsp;

import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.cortile.core.compile.el.ELHelper;
import org.withinsea.izayoi.cortile.core.compile.el.JavaELSupportedCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-7
 * Time: 1:05:30
 */
public abstract class JSPCompiler extends JavaELSupportedCompiler {

    protected String encoding;
    protected String outputFolder;
    protected String outputSuffix;
    protected String izayoiContainerRetrievalKey;

    @Override
    protected String compileELHelperBuilding() {
        return IzayoiContainer.class.getCanonicalName() +
                ".retrieval(request.getSession().getServletContext(), \"" + izayoiContainerRetrievalKey + "\")" +
                ".getComponent(" + ELHelper.class.getCanonicalName() + ".class)" +
                ".getHelper(request, response)";
    }

    @Override
    public String mapEntrancePath(String templatePath) {
        String folder = "/" + outputFolder.trim().replaceAll("^/|/$", "");
        return folder + templatePath + "." + outputSuffix + ".jsp";
    }

    @Override
    public Result compile(String templatePath, String templateCode) throws CortileException {
        Result result = new Result(templatePath);
        result.getTargets().put(mapEntrancePath(templatePath), compileJSP(templateCode));
        return result;
    }

    public String compileJSP(String jspContent) throws CortileException {
        return "<%@ page contentType=\"text/html; charset=" + encoding + "\" pageEncoding=\"" + encoding + "\" %>" +
                "<%" + elInit() + "%>" +
                jspContent;
    }

    // dependency

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void setOutputSuffix(String outputSuffix) {
        this.outputSuffix = outputSuffix;
    }

    public void setIzayoiContainerRetrievalKey(String izayoiContainerRetrievalKey) {
        this.izayoiContainerRetrievalKey = izayoiContainerRetrievalKey;
    }
}