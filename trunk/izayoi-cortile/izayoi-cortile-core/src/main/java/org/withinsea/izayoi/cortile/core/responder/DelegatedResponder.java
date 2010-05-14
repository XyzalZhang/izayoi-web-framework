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

package org.withinsea.izayoi.cortile.core.responder;

import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.DelegateInvoker;
import org.withinsea.izayoi.core.scope.custom.Request;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-14
 * Time: 5:57:03
 */
public class DelegatedResponder extends DelegateInvoker<Request> {

    @Override
    public boolean invoke(String codePath, Request scope) throws IzayoiException {
        super.invoke(codePath, scope);
        return true;
    }
}
