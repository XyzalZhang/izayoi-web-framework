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

package org.withinsea.izayoi.cortile.core.compiler.java;

import org.withinsea.izayoi.core.conf.ComponentContainer;
import org.withinsea.izayoi.cortile.core.compiler.el.ELHelper;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-7
 * Time: 1:05:30
 */
public abstract class JSPCompiler extends JavaELSupportedCompiler {

    protected String encoding;
    protected String targetPath;
    protected String componentContainerRetrievalKey;

    @Override
    protected String compileELHelperBuilding() {
        return ComponentContainer.class.getCanonicalName() +
                ".retrieval(request.getSession().getServletContext(), \"" + componentContainerRetrievalKey + "\")" +
                ".getComponent(" + ELHelper.class.getCanonicalName() + ".class)" +
                ".getHelper(request)";
    }

    @Override
    public String mapEntrancePath(String templatePath) {
        String folder = "/" + targetPath.trim().replaceAll("^/|/$", "");
        return folder + templatePath + ".jsp";
    }

    public String jspHeader() throws CortileException {
        return "<%@ page contentType=\"text/html; charset=" + encoding + "\" pageEncoding=\"" + encoding + "\" %>";
    }

    // dependency

    public void setComponentContainerRetrievalKey(String componentContainerRetrievalKey) {
        this.componentContainerRetrievalKey = componentContainerRetrievalKey;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }
}
