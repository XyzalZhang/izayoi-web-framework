package org.withinsea.izayoi.core.serialize;

import org.withinsea.izayoi.core.exception.IzayoiException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-8
 * Time: 14:27:05
 */
public interface SerializeManager {

    public String findType(String contentType);

    public void serialize(Class<?> claz, Object obj, String asType, OutputStream os, String encoding) throws IzayoiException;

    public void serialize(Class<?> claz, Object obj, String asType, Writer writer) throws IzayoiException;

    public <T> T deserialize(Class<T> claz, String asType, InputStream is, String encoding) throws IzayoiException;

    public <T> T deserialize(Class<T> claz, String asType, Reader reader) throws IzayoiException;
}
