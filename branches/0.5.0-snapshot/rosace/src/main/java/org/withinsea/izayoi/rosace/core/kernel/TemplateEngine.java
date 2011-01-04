package org.withinsea.izayoi.rosace.core.kernel;

import org.withinsea.izayoi.rosace.core.exception.RosaceException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 20:32:00
 */
public interface TemplateEngine {

    String getName();

    String precompileTemplate(String template) throws RosaceException;
}
