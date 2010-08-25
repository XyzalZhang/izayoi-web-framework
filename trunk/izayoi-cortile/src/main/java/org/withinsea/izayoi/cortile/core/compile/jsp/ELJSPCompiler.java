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

import org.withinsea.izayoi.cortile.core.compile.el.ELHelper;
import org.withinsea.izayoi.cortile.core.compile.el.JavaELSupportedCompiler;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

import javax.annotation.Resource;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-7
 * Time: 1:05:30
 */
public class ELJSPCompiler extends JavaELSupportedCompiler {

    @Resource
    String encoding;

    @Resource
    String outputFolder;

    @Resource
    String outputSuffix;

    @Resource
    String izayoiContainerRetrievalKey;

    @Override
    protected String elHelper() {
        return ELHelper.class.getCanonicalName() + ".get(\"" + izayoiContainerRetrievalKey + "\", request, response)";
    }

    @Override
    public String mapEntrancePath(String templatePath) {
        String folder = "/" + outputFolder.trim().replaceAll("^/|/$", "");
        return folder + templatePath + "." + outputSuffix + ".jsp";
    }

    @Override
    public Result compile(String templatePath, String templateCode) throws CortileException {
        Result result = new Result();
        result.getTargets().put(mapEntrancePath(templatePath), compileJSP(templateCode));
        return result;
    }

    public String compileJSP(String jspContent) throws CortileException {
        return "<%@ page contentType=\"text/html; charset=" + encoding + "\" pageEncoding=\"" + encoding + "\" %>" +
                "<%" + elInit() + "%>" +
                jspContent;
    }
}