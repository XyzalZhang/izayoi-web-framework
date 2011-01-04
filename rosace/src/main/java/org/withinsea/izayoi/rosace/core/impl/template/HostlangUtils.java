package org.withinsea.izayoi.rosace.core.impl.template;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-2
 * Time: 下午4:17
 */
public class HostlangUtils {

    public static String javaString(String str) {
        return (str == null) ? "null" : str
                .replace("\t", "\\t")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                .replace("\t", "\\t")
                .replace("\"", "\\\"");
    }

    public static String jspString(String str) {
        return (str == null) ? "null" : javaString(str)
                .replace("<%", "\\<\\%")
                .replace("%>", "\\%\\>");
    }

    public static Object checkNull(Object obj, Object defaultValue) {
        return (obj == null) ? defaultValue : obj;
    }
}
