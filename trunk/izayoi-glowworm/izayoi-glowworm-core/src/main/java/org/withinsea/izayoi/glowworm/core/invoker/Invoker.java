package org.withinsea.izayoi.glowworm.core.invoker;

import org.withinsea.izayoi.core.bindings.scope.Scope;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:51:12
 */
public interface Invoker {

    boolean process(HttpServletRequest request, HttpServletResponse response, Code code, String asType, Scope scope) throws GlowwormException;
}
