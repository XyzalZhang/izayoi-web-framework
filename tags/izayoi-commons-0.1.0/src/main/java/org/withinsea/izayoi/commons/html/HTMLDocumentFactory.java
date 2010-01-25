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

package org.withinsea.izayoi.commons.html;

import org.dom4j.*;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultComment;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-5
 * Time: 17:36:38
 */
public class HTMLDocumentFactory extends DocumentFactory {

    @Override
    public Comment createComment(String text) {
        return new DocumentHoldingComment(text);
    }

    @Override
    public Attribute createAttribute(Element owner, QName qname, String value) {
        return new SurroundableAttr(qname, value);
    }

    public static class DocumentHoldingComment extends DefaultComment {

        protected Document doc;

        public DocumentHoldingComment(String text) {
            super(text);
        }

        @Override
        public void setDocument(Document doc) {
            this.doc = doc;
        }

        @Override
        public Document getDocument() {
            return doc;
        }
    }

    public static class SurroundableAttr extends DefaultAttribute {

        protected String prefix = "";
        protected String suffix = "";

        public SurroundableAttr(QName qname, String value) {
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
}
