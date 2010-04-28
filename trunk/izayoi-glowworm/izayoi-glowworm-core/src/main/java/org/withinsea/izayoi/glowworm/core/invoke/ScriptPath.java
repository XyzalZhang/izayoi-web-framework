package org.withinsea.izayoi.glowworm.core.invoke;

import org.withinsea.izayoi.core.code.Path;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-23
 * Time: 15:25:56
 */
public class ScriptPath extends Path {

    protected final String invokerType;
    protected final String scopeType;

    public ScriptPath(String path) {
        super(path);
        String[] split = this.role.split("@");
        this.invokerType = split[0].equals("") ? "default" : split[0];
        this.scopeType = (split.length < 2) ? "default" : split[1];
    }

    public String getInvokerType() {
        return invokerType;
    }

    public String getScopeType() {
        return scopeType;
    }
}
