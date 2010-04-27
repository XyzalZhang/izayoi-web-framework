package org.withinsea.izayoi.core.bindings.scope;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-22
 * Time: 11:40:07
 */
public interface Scope {

    <T> void setBean(HttpServletRequest request, HttpServletResponse response, String name, T object);

    <T> T getBean(HttpServletRequest request, HttpServletResponse response, String name);
}
