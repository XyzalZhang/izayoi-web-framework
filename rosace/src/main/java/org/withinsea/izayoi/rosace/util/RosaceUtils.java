package org.withinsea.izayoi.rosace.util;

import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.kernel.Renderer;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-7
 * Time: 下午7:04
 */
public class RosaceUtils {

    public static final String TEMP_PACKAGE_NAME = "org.withinsea.izayoi.rosace.temp";
    public static final String DEFAULT_TYPE = "dom";

    public static String render(String template, Map<String, Object> context) throws RosaceException {
        return render(template, DEFAULT_TYPE, context);
    }

    public static void render(Writer writer, String template, Map<String, Object> context) throws RosaceException {
        render(writer, template, DEFAULT_TYPE, context);
    }

    public static String render(String template, String type, Map<String, Object> context) throws RosaceException {
        StringWriter writer = new StringWriter();
        render(writer, template, type, context);
        return writer.getBuffer().toString();
    }

    public static void render(Writer writer, String template, String type, Map<String, Object> context) throws RosaceException {
        Rosaces.newDefaultTemplateCompiler().compile(TEMP_PACKAGE_NAME + ".TempRenderer", type, template).render(writer, context);
    }

    public static Renderer compile(String template) throws RosaceException {
        return compile(template, DEFAULT_TYPE);
    }

    public static Renderer compile(String template, String type) throws RosaceException {
        return Rosaces.newDefaultTemplateCompiler().compile(TEMP_PACKAGE_NAME + ".TempRenderer", type, template);
    }
}
