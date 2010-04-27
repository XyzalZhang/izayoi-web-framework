package org.withinsea.izayoi.core.bindings.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-22
 * Time: 12:55:47
 */
public class Singleton extends AttributeScope {

    protected static Map<String, Object> SINGLETONS_HOLDER = new HashMap<String, Object>();

    @Override
    public <T> void setBean(HttpServletRequest request, HttpServletResponse response, String name, T object) {
        SINGLETONS_HOLDER.put(name, object);
    }

    @Override
    protected Object lookupConstant(HttpServletRequest request, HttpServletResponse response, String name) {
        return null;
    }

    @Override
    protected Object lookupAttribute(HttpServletRequest request, HttpServletResponse response, String name) {
        return SINGLETONS_HOLDER.get(name);
    }
}
