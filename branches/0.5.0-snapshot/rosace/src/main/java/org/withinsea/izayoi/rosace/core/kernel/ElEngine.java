package org.withinsea.izayoi.rosace.core.kernel;

import org.withinsea.izayoi.rosace.core.exception.RosaceException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-28
 * Time: 12:35:22
 */
public interface ElEngine {

    String getName();

    String precompileEl(String el) throws RosaceException;
}
