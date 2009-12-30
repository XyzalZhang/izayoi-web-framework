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

package org.withinsea.izayoi.cortile.jsp.io;

import org.apache.xerces.impl.Constants;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultComment;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-15
 * Time: 7:19:08
 */
public class HTMLReader extends SAXReader {

    public static class DocComment extends DefaultComment {

        private Document doc;

        public DocComment(String text) {
            super(text);
        }

        public void setDocument(Document doc) {
            this.doc = doc;
        }

        public Document getDocument() {
            Document doc = super.getDocument();
            return (doc != null) ? doc : this.doc;
        }
    }

    public static class SurroundAttr extends DefaultAttribute {

        private String prefix = "";
        private String suffix = "";

        public SurroundAttr(QName qname, String value) {
            super(qname, value);
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getSuffix() {
            return suffix;
        }

        public void setSuffix(String suffix) {
            this.suffix = suffix;
        }
    }

    public HTMLReader() throws SAXException {
        super(new DocumentFactory() {
            @Override
            public Comment createComment(String text) {
                return new DocComment(text);
            }

            @Override
            public Attribute createAttribute(Element owner, QName qname, String value) {
                return new SurroundAttr(qname, value);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Document read(InputSource in) throws DocumentException {
        Document doc = super.read(in);
        if (doc != null) {
            for (Node node : (List<Node>) doc.content()) {
                if (node instanceof DocComment) {
                    node.setDocument(doc);
                }
            }
        }
        return doc;
    }

    @Override
    protected void configureReader(XMLReader reader, DefaultHandler handler)
            throws DocumentException {
        super.configureReader(reader, handler);
        try {
            reader.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACES_FEATURE, false);
            reader.setFeature(Constants.SAX_FEATURE_PREFIX + Constants.NAMESPACE_PREFIXES_FEATURE, true);
            reader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.CONTINUE_AFTER_FATAL_ERROR_FEATURE, true);
            reader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
        } catch (Exception e) {
            throw new DocumentException(e);
        }
    }
}