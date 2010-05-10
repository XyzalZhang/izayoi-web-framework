package org.withinsea.izayoi.core.invoke;

import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.scope.Scope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:34:56
 */
public interface InvokeManager {

    boolean invoke(String codePath, Scope scope) throws IzayoiException;
}