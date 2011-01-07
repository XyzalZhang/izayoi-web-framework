package org.withinsea.izayoi.cloister.core.impl.scope;

import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.common.util.Vars;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 上午9:26
 */
public class SimpleScope implements Scope {

    protected Vars attributes = new Vars();

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Map<String, Object> getScopeAttributes() {
        return attributes;
    }

    @Override
    public Scope getParentScope() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }
}
