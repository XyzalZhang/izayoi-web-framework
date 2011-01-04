package org.withinsea.izayoi.rosace.core.impl.el;

import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.kernel.ElEngine;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-15
 * Time: 上午11:34
 */
public class Java implements ElEngine {

    @Override
    public String getName() {
        return "Java";
    }

    @Override
    public String precompileEl(String el) throws RosaceException {
        return el;
    }
}
