package org.withinsea.izayoi.core.scope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 9:59:26
 */
public interface Scope {

    <T> void setBean(String name, T object);

    <T> T getBean(String name);
}
