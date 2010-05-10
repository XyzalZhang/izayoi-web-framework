package org.withinsea.izayoi.core.scope.custom;

import org.withinsea.izayoi.core.scope.context.ContextScope;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:16:18
 */
public class Singleton extends CustomizedScope {

    protected static Map<String, Object> SINGLETONS_HOLDER = new HashMap<String, Object>();

    public Singleton(ContextScope contextScope) {
        super(contextScope);
    }

    @Override
    public <T> void setBean(String name, T object) {
        SINGLETONS_HOLDER.put(name, object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        return (T) super.getBean(name);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected Object lookupAttribute(String name) {
        return SINGLETONS_HOLDER.get(name);
    }
}
