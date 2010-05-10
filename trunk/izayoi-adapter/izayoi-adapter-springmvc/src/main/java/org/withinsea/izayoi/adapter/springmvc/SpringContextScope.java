package org.withinsea.izayoi.adapter.springmvc;

import org.springframework.context.ApplicationContext;
import org.withinsea.izayoi.core.scope.context.DefaultContextScope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-23
 * Time: 16:26:37
 */
public class SpringContextScope extends DefaultContextScope {

    protected ApplicationContext applicationContext;

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        Object obj = lookupSpring(name);
        if (obj == null) obj = super.getBean(name);
        return (T) obj;
    }

    protected Object lookupSpring(String name) {
        try {
            return name.equals("applicationContext") ? applicationContext : applicationContext.getBean(name);
        } catch (Exception e) {
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
