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

package org.withinsea.izayoi.demo.sample;

import org.dom4j.Attribute;
import org.withinsea.izayoi.commons.dom.DOMUtils;
import org.withinsea.izayoi.cortile.core.compile.dom.AttrGrammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-25
 * Time: 23:54:56
 */
public class SpringTestGrammar implements AttrGrammar {

    @Override
    public boolean acceptAttr(Attribute attr) {
        return attr.getName().equals("test");
    }

    @Override
    public void processAttr(Attribute attr) throws CortileException {
        try {
            DOMUtils.replaceBy(attr.getParent(), "SPRING INJECTED GRAMMAR");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
