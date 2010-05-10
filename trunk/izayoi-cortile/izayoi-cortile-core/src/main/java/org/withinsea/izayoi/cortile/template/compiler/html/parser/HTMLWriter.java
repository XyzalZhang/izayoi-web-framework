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

package org.withinsea.izayoi.cortile.template.compiler.html.parser;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.withinsea.izayoi.cortile.template.compiler.dom.DOMCompiler;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-20
 * Time: 4:18:05
 */
public class HTMLWriter extends org.dom4j.io.HTMLWriter {

    public HTMLWriter(Writer writer) {
        super(writer);
        setEscapeText(false);
        getOutputFormat().setXHTML(true);
    }

    @Override
    protected void writeElement(Element element) throws IOException {

        if (!DOMCompiler.ANONYMOUS_TAG_NAME.equals(element.getName())) {
            super.writeElement(element);
            return;
        }

        OutputFormat currentFormat = getOutputFormat();
        boolean saveTrimText = currentFormat.isTrimText();
        String currentIndent = currentFormat.getIndent();
        currentFormat.setNewlines(false);
        currentFormat.setTrimText(false);
        currentFormat.setIndent("");

        writePrintln();
        indent();
        writeElementContent(element);
        writePrintln();
        indent();

        currentFormat.setTrimText(saveTrimText);
        currentFormat.setIndent(currentIndent);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void writeAttributes(Element element) throws IOException {
        for (Attribute attribute : (List<Attribute>) element.attributes()) {
            String name = attribute.getName();
            if (!name.equals("xmlns") && !name.startsWith("xmlns:")) {
                if (attribute instanceof HTMLDocumentFactory.SurroundableAttr) {
                    HTMLDocumentFactory.SurroundableAttr sattr = (HTMLDocumentFactory.SurroundableAttr) attribute;
                    writeString(" " + sattr.getPrefix() + attribute.getQualifiedName() + "=\"");
                    writeEscapeAttributeEntities(attribute.getValue());
                    writer.write("\"");
                    writeString(sattr.getSuffix());
                } else {
                    writeString(" " + attribute.getQualifiedName() + "=\"");
                    writeEscapeAttributeEntities(attribute.getValue());
                    writer.write("\"");
                }
            }
        }
    }

    @Override
    protected void writeAttribute(Attribute attribute) throws IOException {
        if (attribute instanceof HTMLDocumentFactory.SurroundableAttr) {
            HTMLDocumentFactory.SurroundableAttr sattr = (HTMLDocumentFactory.SurroundableAttr) attribute;
            writeString(sattr.getPrefix());
            super.writeAttribute(attribute);
            writeString(sattr.getSuffix());
        } else {
            super.writeAttribute(attribute);
        }
    }

    @Override
    protected String escapeAttributeEntities(String text) {
        return text;
    }

    @Override
    public boolean isPreformattedTag(String qualifiedName) {
        return true;
    }
}