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
 * The Original Code is the @PROJECT_NAME
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

package org.withinsea.izayoi.rosace.core.impl.template.dom.parser;

import org.dom4j.QName;
import org.dom4j.tree.DefaultAttribute;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-13
 * Time: 3:59:00
 */
public class DomSurroundableAttr extends DefaultAttribute {

    private static final long serialVersionUID = -2186580031967041242L;

    protected String prefix = "";
    protected String suffix = "";

    public DomSurroundableAttr(QName qname, String value) {
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
