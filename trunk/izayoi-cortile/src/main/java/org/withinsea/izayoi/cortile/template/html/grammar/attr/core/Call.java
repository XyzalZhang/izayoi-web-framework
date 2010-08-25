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

package org.withinsea.izayoi.cortile.template.html.grammar.attr.core;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.cortile.core.compile.CompileContext;
import org.withinsea.izayoi.cortile.core.compile.CompileManager;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.template.html.HTMLCompiler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:07:55
 */
public class Call implements AttrGrammar {

    public static final String FUNC_ATTR = Call.class.getCanonicalName() + ".FUNC";

    @Resource
    String izayoiContainerRetrievalKey;

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("call");
    }

    @Override
    public void processAttr(Attribute attr) throws CortileException {
        String attrvalue = attr.getValue();
        processAttr(attr.getParent(), attrvalue);
        attr.detach();
    }

    protected void processAttr(Element range, String includePath) throws CortileException {

        CompileContext ctx = CompileContext.get();
        HTMLCompiler compiler = ctx.getCompiler();
        String templatePath = ctx.getTemplatePath();

        includePath = includePath.trim();
        boolean embedded = !(includePath.startsWith("${") && includePath.endsWith("}")) || includePath.indexOf("${", 1) > 0;
        String compiledValue = embedded
                ? compileEmbeddedELs(compiler, includePath)
                : compiler.el(includePath.substring(2, includePath.length() - 1).trim());

        try {
            DOMUtils.insertAfter("<% " + compiler.openScope() + " %>" +
                    "<% pageContext.getOut().flush(); %>" +
                    "<% " + Call.class.getCanonicalName() + ".include(\"" + izayoiContainerRetrievalKey + "\"," +
                    "request, response, \"" + templatePath + "\", " + compiledValue + "); %>" +
                    "<% " + compiler.closeScope() + " %>", range);
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }

    protected String compileEmbeddedELs(final HTMLCompiler compiler, String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + compiler.el(groups[1].replace("\\}", "}"), true) + "+ \"";
            }
        }) + "\"";
    }

    public static void include(String retrievalKey, HttpServletRequest request, HttpServletResponse response,
                               String templatePath, String includePath)
            throws IOException, ServletException {

        IzayoiContainer container = IzayoiContainer.retrieval(request.getSession().getServletContext(), retrievalKey);
        CompileManager compileManager = container.get("compileManager");

        String path = (includePath.indexOf(":") < 0) ? templatePath : includePath.split(":", 2)[0];
        String func = (includePath.indexOf(":") < 0) ? includePath : includePath.split(":", 2)[1];
        if (!path.startsWith("/")) {
            path = new Path(templatePath).getFolder() + path;
        }

        try {
            compileManager.update(path, false);
        } catch (CortileException e) {
            throw new ServletException(e);
        }

        String funcBackup = (String) request.getAttribute(FUNC_ATTR);
        request.setAttribute(FUNC_ATTR, func);
        {
            request.getRequestDispatcher(path).include(request, response);
        }
        if (funcBackup != null) request.setAttribute(FUNC_ATTR, funcBackup);
    }

    public static boolean isIncluded(HttpServletRequest request, String func) {
        return (func != null) && (!func.equals("")) && func.equals(request.getAttribute(FUNC_ATTR));
    }
}
