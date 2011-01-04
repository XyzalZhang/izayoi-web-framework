package org.withinsea.izayoi.rosace.test;

import org.withinsea.izayoi.common.util.IOUtils;
import org.withinsea.izayoi.common.util.Vars;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-8
 * Time: 下午5:44
 */
public class ResourceUtils {

    public static String text(String name) throws IOException {
        return IOUtils.toString(caller().getResourceAsStream(name), "UTF-8");
    }

    @SuppressWarnings("unchecked")
    public static Vars props(String name) throws IOException {
        Properties props = new Properties();
        props.load(new InputStreamReader(caller().getResourceAsStream(name), "UTF-8"));
        return new Vars((Map) props);
    }

    protected static Class<?> caller() {
        String selfname = ResourceUtils.class.getCanonicalName();
        for (StackTraceElement ste : new Throwable().getStackTrace()) {
            if (!selfname.equals(ste.getClassName())) {
                try {
                    return Class.forName(ste.getClassName());
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
