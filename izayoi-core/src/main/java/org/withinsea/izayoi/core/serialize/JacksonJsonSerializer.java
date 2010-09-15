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

package org.withinsea.izayoi.core.serialize;

import org.codehaus.jackson.map.ObjectMapper;
import org.withinsea.izayoi.core.exception.IzayoiException;

import java.io.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-26
 * Time: 15:29:53
 */
public class JacksonJsonSerializer implements Serializer {

    protected static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void serialize(Class<?> claz, Object obj, OutputStream os, String encoding) throws IzayoiException {
        try {
            MAPPER.writeValue(new OutputStreamWriter(os, encoding), obj);
        } catch (IOException e) {
            throw new IzayoiException(e);
        }
    }

    @Override
    public void serialize(Class<?> claz, Object obj, Writer writer) throws IzayoiException {
        try {
            MAPPER.writeValue(writer, obj);
        } catch (IOException e) {
            throw new IzayoiException(e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> claz, InputStream is, String encoding) throws IzayoiException {
        try {
            return MAPPER.readValue(new InputStreamReader(is, encoding), claz);
        } catch (IOException e) {
            throw new IzayoiException(e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> claz, Reader reader) throws IzayoiException {
        try {
            return MAPPER.readValue(reader, claz);
        } catch (IOException e) {
            throw new IzayoiException(e);
        }
    }
}
