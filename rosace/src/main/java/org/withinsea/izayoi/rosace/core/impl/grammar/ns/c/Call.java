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
        attr.detach();
    }

    protected void processAttr(Element range, String target) throws RosaceException {

        PrecompiletimeContext ctx = PrecompiletimeContext.get();
        DomTemplateEngine engine = ctx.getEngine();

        target = target.trim();
        boolean embedded = !(target.startsWith("${") && target.endsWith("}")) || target.indexOf("${", 1) > 0;
        String targetCode = embedded
                ? precompileEmbeddedELs(engine, target)
                : engine.precompileEl(target.substring(2, target.length() - 1).trim());

        try {
            DomUtils.surroundBy(range, "<% if (!" + precompileCall(targetCode) + ") { %>", "<% } %>");
        } catch (Exception e) {
            throw new RosaceException(e);
        }
    }

    protected String precompileCall(String targetCode) throws RosaceException {
        return Call.class.getCanonicalName() + ".call(this," +
                RosaceConstants.VARIABLE_WRITER + ", " +
                RosaceConstants.VARIABLE_VARSTACK + ", " +
                targetCode + ")";
    }

    protected String precompileEmbeddedELs(final DomTemplateEngine engine, String text) {
        return "\"" + StringUtils.replaceAll(text, "\\$\\{([\\s\\S]*?[^\\\\])\\}", new StringUtils.Replace() {
            public String replace(String... groups) {
                return "\"+" + engine.precompileEl(groups[1].replace("\\}", "}"), true) + "+ \"";
            }
        }) + "\"";
    }

    public static boolean call(Renderer renderer, PrintWriter writer, Varstack varstack, String target) throws Exception {

        target = target.endsWith(":") ? target.substring(0, target.length() - 1) : target;

        String path = (target.indexOf(":") < 0) ? target : target.split(":", 2)[0];
        String section = (target.indexOf(":") < 0) ? null : target.split(":", 2)[1];

        if ((path.equals("")) && (renderer instanceof TemplateCompiler.CompiledTemplate)) {

            varstack.push(RosaceConstants.ATTR_INCLUDE_SECTION, section);
            ((TemplateCompiler.CompiledTemplate) renderer).renderTo(writer, varstack);
            varstack.pop();

        } else {

            IncludeSupport includeSupport = (IncludeSupport) varstack.get(RosaceConstants.ATTR_INCLUDE_SUPPORT);
            if (includeSupport == null) {
                throw new RosaceRuntimeException("missing IncludeSupport for multi-templates including.");
            }

            varstack.push(RosaceConstants.ATTR_INCLUDE_SECTION, section);
            includeSupport.include(writer, path, varstack);
            varstack.pop();
        }

        return true;
    }

    public static boolean isSection(Varstack varstack, String section) {
        return (section != null) && (!section.equals("")) && section.equals(varstack.get(RosaceConstants.ATTR_INCLUDE_SECTION));
    }

    public static boolean isSection(Varstack varstack) {
        return varstack.get(RosaceConstants.ATTR_INCLUDE_SECTION) != null;
    }
}
