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

import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.impl.XMLEntityScanner;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-5
 * Time: 14:52:55
 */
public class HTMLDocumentScannerImpl extends XMLDocumentScannerImpl {

    public static class SpaceBufferEntityScanner extends XMLEntityScanner {

        protected boolean spaceBuffered = false;

        public void bufferOneSpace() throws IOException {
            spaceBuffered = true;
        }

        @Override
        public synchronized boolean skipSpaces() throws IOException {
            if (spaceBuffered) {
                super.skipSpaces();
                spaceBuffered = false;
                return true;
            } else {
                return super.skipSpaces();
            }
        }
    }

    @Override
    protected void reportFatalError(String msgId, Object[] args)
            throws XNIException {
        if (msgId.equals("EqRequiredInAttribute")) {
            throw new NoValueException();
        } else if (msgId.equals("LessthanInAttValue")) {
        } else {
            super.reportFatalError(msgId, args);
        }
    }

    @Override
    protected void scanAttribute(XMLAttributes attributes)
            throws IOException, XNIException {
        try {
            super.scanAttribute(attributes);
        } catch (NoValueException e) {
            int attrIndex = attributes.addAttribute(fAttributeQName, XMLSymbols.fCDATASymbol, null);
            String value = fAttributeQName.rawname.replaceAll("^.*?:", "");
            attributes.setValue(attrIndex, value);
            attributes.setNonNormalizedValue(attrIndex, value);
            attributes.setSpecified(attrIndex, true);
            ((SpaceBufferEntityScanner) fEntityScanner).bufferOneSpace();
        }
    }

    @Override
    protected void scanAttributeValue(XMLString value,
                                      XMLString nonNormalizedValue,
                                      String atName,
                                      boolean checkEntities, String eleName)
            throws IOException, XNIException {
        super.scanAttributeValue(value, nonNormalizedValue, atName, checkEntities, eleName);
        value.setValues(nonNormalizedValue);
    }

    protected static class NoValueException extends RuntimeException {
    }
}
