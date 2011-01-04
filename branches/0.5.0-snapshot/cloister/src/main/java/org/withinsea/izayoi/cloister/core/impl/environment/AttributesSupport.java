package org.withinsea.izayoi.cloister.core.impl.environment;

import org.withinsea.izayoi.common.util.Vars;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-24
 * Time: 下午3:36
 */
public abstract class AttributesSupport {

    protected Vars attributes = new Vars();

    public Vars getAttributes() {
        return attributes;
    }
}
