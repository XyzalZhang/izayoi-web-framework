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

package org.withinsea.izayoi.rosace.core.impl.template.dom.parser;

import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.tree.DefaultComment;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-5
 * Time: 17:36:38
 */
class DomDocumentFactory extends org.dom4j.DocumentFactory {

    private static final long serialVersionUID = 4049687075570078147L;

    @Override
    public Comment createComment(String text) {
        return new DocumentHoldingComment(text);
    }

    @Override
    public org.dom4j.Attribute createAttribute(Element owner, QName qname, String value) {
        return new DomSurroundableAttr(qname, value);
    }

    protected static class DocumentHoldingComment extends DefaultComment {

        private static final long serialVersionUID = -3726388730591676351L;

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

}
