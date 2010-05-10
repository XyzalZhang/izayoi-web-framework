package org.withinsea.izayoi.glowworm.core.decorate;

import org.withinsea.izayoi.core.invoke.InvokeManager;
import org.withinsea.izayoi.core.scope.Scope;

import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:56:10
 */
public interface DecorateManager extends InvokeManager {

    boolean isDecorator(String codePath);

    List<String> findScopedDecoratorPaths(String scopeName, Scope scope);

    List<String> findRequestDecoratorPaths(String requestPath);
}
