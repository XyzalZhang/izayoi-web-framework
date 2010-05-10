package org.withinsea.izayoi.core.serializer;

import com.google.gson.*;
import org.withinsea.izayoi.core.exception.IzayoiException;

import java.io.*;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 21:04:27
 */
public class GsonJsonSerializer implements Serializer {

    protected Gson gson = createGson();

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
        gson.toJson(obj, claz, writer);
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
    public <T> T deserialize(Class<T> claz, Reader reader) throws IzayoiException {
        return gson.fromJson(reader, claz);
    }

    protected Gson createGson() {
        return new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.STATIC, Modifier.TRANSIENT, Modifier.VOLATILE)
                .registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
                    @Override
                    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(date.getTime());
                    }
                })
                .registerTypeAdapter(Calendar.class, new JsonSerializer<Calendar>() {
                    @Override
                    public JsonElement serialize(Calendar calendar, Type type, JsonSerializationContext jsonSerializationContext) {
                        return new JsonPrimitive(calendar.getTime().getTime());
                    }
                })
                .create();
    }
}
