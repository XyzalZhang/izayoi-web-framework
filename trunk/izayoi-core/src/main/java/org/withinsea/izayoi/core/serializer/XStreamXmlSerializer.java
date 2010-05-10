package org.withinsea.izayoi.core.serializer;

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
