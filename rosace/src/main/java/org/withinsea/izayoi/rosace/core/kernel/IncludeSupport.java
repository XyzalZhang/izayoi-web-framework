package org.withinsea.izayoi.rosace.core.kernel;

import org.withinsea.izayoi.rosace.core.exception.RosaceException;

import java.io.Writer;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 18:11:36
 */
public interface IncludeSupport {

    void include(Writer writer, String path, Map<String, Object> context) throws RosaceException;
}
