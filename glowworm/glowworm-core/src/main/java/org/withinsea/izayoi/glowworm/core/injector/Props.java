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

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 16:25:41
 */
@SuppressWarnings("unused")
public class Props extends DeserializeInjector {

    @Override
    protected Object deserialize(String src) throws GlowwormException {
        try {
            java.util.Properties props = new java.util.Properties();
            props.load(new StringReader(src));
            Map<String, Object> data = new LinkedHashMap<String, Object>();
            for (String name : props.stringPropertyNames()) {
                data.put(name, props.getProperty(name));
            }
            return data;
        } catch (IOException e) {
            throw new GlowwormException(e);
        }
    }

}