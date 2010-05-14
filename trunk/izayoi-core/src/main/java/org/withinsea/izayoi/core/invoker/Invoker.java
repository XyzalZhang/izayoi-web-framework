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

package org.withinsea.izayoi.core.invoker;

import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.scope.Scope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:34:56
 */
public interface Invoker<S extends Scope> {

    /**
     * Invoke a appendant script file
     *
     * @param codePath
     * @param scope
     * @return Return false if invokation has failed or processing should be stoped
     * @throws IzayoiException
     */
    boolean invoke(String codePath, S scope) throws IzayoiException;
}
