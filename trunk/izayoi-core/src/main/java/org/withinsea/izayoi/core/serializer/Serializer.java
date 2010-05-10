package org.withinsea.izayoi.core.serializer;

import org.withinsea.izayoi.core.exception.IzayoiException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-7
 * Time: 16:52:23
 */
public interface Serializer {

    public void serialize(Class<?> claz, Object obj, OutputStream os, String encoding) throws IzayoiException;

    public void serialize(Class<?> claz, Object obj, Writer writer) throws IzayoiException;

    public <T> T deserialize(Class<T> claz, InputStream is, String encoding) throws IzayoiException;

    public <T> T deserialize(Class<T> claz, Reader reader) throws IzayoiException;
}
