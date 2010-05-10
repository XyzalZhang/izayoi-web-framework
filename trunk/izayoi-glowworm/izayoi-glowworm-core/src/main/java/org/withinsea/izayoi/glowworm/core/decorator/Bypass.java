package org.withinsea.izayoi.glowworm.core.decorator;

import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.Scope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:54:20
 */
public class Bypass implements Invoker<Scope> {

    @Override
    public boolean invoke(String codePath, Scope scope) throws IzayoiException {
        return true;
    }
}