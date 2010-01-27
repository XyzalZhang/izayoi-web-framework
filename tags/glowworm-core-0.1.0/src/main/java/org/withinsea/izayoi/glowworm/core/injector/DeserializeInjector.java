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

package org.withinsea.izayoi.glowworm.core.injector;

import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-27
 * Time: 10:25:15
 */
public abstract class DeserializeInjector implements Injector {

    protected abstract Object deserialize(String src) throws GlowwormException;

    @Override
    public Object inject(HttpServletRequest request, String srcPath, String src) throws GlowwormException {
        return deserialize(src);
    }
}
