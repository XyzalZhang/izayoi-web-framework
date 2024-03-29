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

package org.withinsea.izayoi.cortile.template.html.parser;

import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.dom4j.*;
import org.dom4j.io.SAXContentHandler;
import org.dom4j.io.SAXReader;
import org.withinsea.izayoi.commons.util.IOUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-15
 * Time: 7:19:08
 */
public class HTMLReader extends SAXReader {

    public HTMLReader() throws SAXException {
        super(new HTMLDocumentFactory());
    }

    @Override
    protected XMLReader createXMLReader() throws SAXException {

        return new org.apache.xerces.parsers.SAXParser(new XML11Configuration() {

            @Override
            protected void addComponent(XMLComponent component) {
                if (component.getClass() == XMLDocumentScannerImpl.class) {
                    fNonNSScanner = new HTMLDocumentScannerImpl();
                    component = fNonNSScanner;
                }
                super.addComponent(component);
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void addCommonComponent(XMLComponent component) {
                if (component.getClass() == XMLEntityManager.class) {
                    fEntityManager = new XMLEntityManager() {
                        @Override
                        public void setScannerVersion(short version) {
                            if (version == Constants.XML_VERSION_1_0) {
                                if (fXML10EntityScanner == null) {
                                    fXML10EntityScanner = new HTMLDocumentScannerImpl.SpaceBufferEntityScanner();
                                }
                                fXML10EntityScanner.reset(fSymbolTable, this, fErrorReporter);
                                fEntityScanner = fXML10EntityScanner;
                                fEntityScanner.setCurrentEntity(fCurrentEntity);
                            } else {
                                super.setScannerVersion(version);
                            }
                        }
                    };
                    fProperties.put(ENTITY_MANAGER, fEntityManager);
                    component = fEntityManager;
                }
                super.addCommonComponent(component);
            }

        }) {

            @Override
            public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs)
                    throws XNIException {
                startElement(element, attributes, augs);
                endElement(new QName(element.prefix, element.localpart, element.rawname,
                        "{EMPTY}" + (element.uri != null ? element.uri : "")), augs);
            }
        };
    }

    @Override
    protected SAXContentHandler createContentHandler(XMLReader reader) {
        return new SAXContentHandler(getDocumentFactory(), getDispatchHandler()) {
            @Override
            public void endElement(String namespaceURI, String localName, String qName)
                    throws SAXException {
                if (namespaceURI.startsWith("{EMPTY}")) {
                    namespaceURI = namespaceURI.substring("{EMPTY}".length());
                } else {
                    try {
                        Field fTextInTextBuffer = SAXContentHandler.class.getDeclaredField("textInTextBuffer");
                        fTextInTextBuffer.setAccessible(true);
                        if (!(Boolean) fTextInTextBuffer.get(this)) {
                            Field fTextBuffer = SAXContentHandler.class.getDeclaredField("textBuffer");
                            fTextBuffer.setAccessible(true);
                            fTextBuffer.set(this, new StringBuffer());
                            completeCurrentTextNode();
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                super.endElement(namespaceURI, localName, qName);
            }
        };
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

    @Override
    public Document read(InputSource in) throws DocumentException {
        String html;
        try {
            html = IOUtils.toString(in.getCharacterStream());
        } catch (IOException e) {
            throw new DocumentException(e);
        }
        Tricker tricker = new Tricker();
        InputSource newIn = new InputSource(new StringReader(tricker.trickBefore(html)));
        {
            newIn.setEncoding(in.getEncoding());
            newIn.setPublicId(in.getPublicId());
            newIn.setSystemId(in.getSystemId());
        }
        return tricker.trickAfter(super.read(newIn));
    }

    protected static class Tricker {

        protected final Map<String, String> holders = new HashMap<String, String>();

        public String trickBefore(String html) {
            html = hold(html, "&", "＆", "_");
            html = hold(html, "< ", "《　", "_");
            html = hold(html, "<=", "《＝", "_");
            html = html.replaceAll("(<script>|<script[^>]*?[^/]>)\\s*(//\\s*<!--)?\\s*", "$1//<!--\n");
            html = html.replaceAll("\\s*(//\\s*-->)?\\s*(</script\\s*>)", "\n//-->$2");
            int start = (html.indexOf("<!DOCTYPE ") < 0) ? 0 : html.indexOf(">", html.indexOf("<!DOCTYPE ")) + 1;
            html = html.substring(0, start) + "<" + HTMLConstants.ANONYMOUS_TAG_NAME + ">" + html.substring(start) + "</" + HTMLConstants.ANONYMOUS_TAG_NAME + ">";
            return html;
        }

        public Document trickAfter(Document doc) {
            revert(doc);
            return doc;
        }

        protected String hold(String html, String toHold, String unit, String bolder) {
            String holder = unit;
            while (html.indexOf(holder) >= 0) {
                holder += unit;
            }
            holder = bolder + holder + bolder;
            holders.put(toHold, holder);
            return html.replace(toHold, holder);
        }

        @SuppressWarnings("unchecked")
        protected void revert(Branch branch) {
            for (Node node : (List<Node>) branch.content()) {
                for (Map.Entry<String, String> e : holders.entrySet()) {
                    if (node instanceof CharacterData) {
                        node.setText(node.getText().replace(e.getValue(), e.getKey()));
                    } else if (node instanceof Element) {
                        for (Attribute attr : (List<Attribute>) ((Element) node).attributes()) {
                            attr.setValue(attr.getValue().replace(e.getValue(), e.getKey()));
                        }
                    }
                }
                if (node instanceof Branch) {
                    revert((Branch) node);
                }
            }
        }
    }
}