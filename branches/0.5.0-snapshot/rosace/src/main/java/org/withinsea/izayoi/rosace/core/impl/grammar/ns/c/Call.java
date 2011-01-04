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

package org.withinsea.izayoi.rosace.core.impl.grammar.ns.c;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.withinsea.izayoi.common.dom4j.DomUtils;
import org.withinsea.izayoi.common.util.StringUtils;
import org.withinsea.izayoi.common.util.Varstack;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.exception.RosaceRuntimeException;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.AttrGrammar;
import org.withinsea.izayoi.rosace.core.kernel.*;

import java.io.PrintWriter;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-28
 * Time: 22:07:55
 */
public class Call implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("call");
    }

    @Override
    public void processAttr(Attribute attr) throws RosaceException {
        Element elem = attr.getParent();
        String attrvalue = attr.getValue();
        processAttr(elem, attrvalue);
        elem.detach();
    }

    protected void processAttr(Element range, String includePath) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();

        includePath = includePath.trim();
        boolean embedded = !(includePath.startsWith("${") && includePath.endsWith("}")) || includePath.indexOf("${", 1) > 0;
        String includePathCode = embedded
                ? precompileEmbeddedELs(engine, includePath)
                : engine.precompileEl(includePath.substring(2, includePath.length() - 1).trim());

        try {
            DomUtils.insertAfter("<%" + Call.class.getCanonicalName() + ".doInclude(this," +
                    RosaceConstants.VARIABLE_WRITER + ", " +
                    RosaceConstants.VARIABLE_VARSTACK + ", " +
                    includePathCode + "); %>", range);
        } catch (Exception e) {
            throw new RosaceException(e);
        }
    }

    protected String precompileEmbeddedELs(final DomTemplateEngine engine, String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + engine.precompileEl(groups[1].replace("\\}", "}"), true) + "+ \"";
            }
        }) + "\"";
    }

    public static void doInclude(Renderer renderer, PrintWriter writer, Varstack varstack, String includePath) throws Exception {

        String path = (includePath.indexOf(":") < 0) ? null : includePath.split(":", 2)[0];
        String section = (includePath.indexOf(":") < 0) ? includePath : includePath.split(":", 2)[1];

        if ((path == null) && (renderer instanceof TemplateCompiler.CompiledTemplate)) {

            varstack.push(RosaceConstants.ATTR_INCLUDE_SECTION, section);
            ((TemplateCompiler.CompiledTemplate) renderer).renderTo(writer, varstack);
            varstack.pop();

        } else {

            IncludeSupport includeSupport = (IncludeSupport) varstack.get(RosaceConstants.ATTR_INCLUDE_SUPPORT);
            if (includeSupport == null) {
                throw new RosaceRuntimeException("missing IncludeSupport for multi-templates including.");
            }

            varstack.push(
                    RosaceConstants.ATTR_INCLUDE_SECTION, section,
                    RosaceConstants.ATTR_INCLUDED_CONTEXT, varstack);
            includeSupport.include(writer, path, varstack);
            varstack.pop();
        }
    }

    public static boolean isSection(Varstack varstack, String section) {
        return (section != null) && (!section.equals("")) && section.equals(varstack.get(RosaceConstants.ATTR_INCLUDE_SECTION));
    }
}
