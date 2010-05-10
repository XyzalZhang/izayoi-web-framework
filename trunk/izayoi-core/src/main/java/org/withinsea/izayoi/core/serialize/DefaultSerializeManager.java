package org.withinsea.izayoi.core.serialize;

import org.withinsea.izayoi.core.code.CodeManager;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.serializer.Serializer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 1:05:28
 */
public class DefaultSerializeManager implements SerializeManager {

    protected CodeManager codeManager;
    protected Map<String, Serializer> serializers;

    @Override
    public void serialize(Class<?> claz, Object obj, String asType, OutputStream os, String encoding) throws IzayoiException {
        getSerializer(asType).serialize(claz, obj, os, encoding);
    }

    @Override
    public void serialize(Class<?> claz, Object obj, String asType, Writer writer) throws IzayoiException {
        getSerializer(asType).serialize(claz, obj, writer);
    }

    @Override
    public <T> T deserialize(Class<T> claz, String asType, InputStream is, String encoding) throws IzayoiException {
        return getSerializer(asType).deserialize(claz, is, encoding);
    }

    @Override
    public <T> T deserialize(Class<T> claz, String asType, Reader reader) throws IzayoiException {
        return getSerializer(asType).deserialize(claz, reader);
    }

    protected Serializer getSerializer(String asType) throws IzayoiException {
        Serializer serializer = serializers.get(serializers.containsKey(asType) ? asType : "default");
        if (serializer == null) {
            throw new IzayoiException("serizalizer type " + asType + " does not exist.");
        }
        return serializer;
    }

    @Override
    public String findType(String contentType) {
        if (contentType == null || contentType.trim().equals("")) {
            return null;
        } else {
            List<String> secondaryChoices = new ArrayList<String>();
            for (String mimeType : contentType.trim().split("[,;\\s]+")) {
                if (mimeType.indexOf("/") >= 0) {
                    for (String type : serializers.keySet()) {
                        String serializerMimeType = codeManager.getMimeType(type);
                        if (serializerMimeType == null || serializerMimeType.equals("")) {
                            secondaryChoices.add(type);
                        } else if (serializerMimeType.matches(mimeType.replace("*", ".+"))) {
                            return type;
                        }
                    }
                }
            }
            if (!secondaryChoices.isEmpty()) {
                return secondaryChoices.get(0);
            }
        }
        return null;
    }

    public void setSerializers(Map<String, Serializer> serializers) {
        this.serializers = serializers;
    }

    public void setCodeManager(CodeManager codeManager) {
        this.codeManager = codeManager;
    }
}
