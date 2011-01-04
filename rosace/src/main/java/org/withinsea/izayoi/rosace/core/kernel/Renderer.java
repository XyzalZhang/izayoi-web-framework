package org.withinsea.izayoi.rosace.core.kernel;

import org.withinsea.izayoi.rosace.core.exception.RosaceException;

import java.io.Writer;
import java.util.Map;

/**
* Created by Mo Chen <withinsea@gmail.com>
* Date: 10-12-8
* Time: 上午8:14
*/
public interface Renderer {

    void render(Writer writer, Map<String, Object> context) throws RosaceException;

    void render(Writer writer, Map<String, Object> context, IncludeSupport includeSupport, String path) throws RosaceException;
}
