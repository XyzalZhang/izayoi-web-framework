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

import com.thoughtworks.xstream.XStream;
import org.withinsea.izayoi.core.exception.IzayoiException;

import java.io.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 1:16:08
 */
public class XStreamXmlSerializer implements Serializer {

    @Override
    public void serialize(Class<?> claz, Object obj, OutputStream os, String encoding) throws IzayoiException {
        try {
            serialize(claz, obj, new OutputStreamWriter(os, encoding));
        } catch (UnsupportedEncodingException e) {
            throw new IzayoiException(e);
        }
    }

    @Override
    public void serialize(Class<?> claz, Object obj, Writer writer) throws IzayoiException {
        XStreamHolder.XSTREAM.toXML(obj, writer);
        try {
            writer.flush();
        } catch (IOException e) {
            throw new IzayoiException(e);
        }
    }

    @Override
    public <T> T deserialize(Class<T> claz, InputStream is, String encoding) throws IzayoiException {
        try {
            return deserialize(claz, new InputStreamReader(is, encoding));
        } catch (UnsupportedEncodingException e) {
            throw new IzayoiException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialize(Class<T> claz, Reader reader) throws IzayoiException {
        return (T) XStreamHolder.XSTREAM.fromXML(reader);
    }

    protected static class XStreamHolder {

        public static XStream XSTREAM = new XStream();
    }
}