package org.withinsea.izayoi.glowworm.core.invoke;

import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-21
 * Time: 21:39:47
 */
public interface InvokeManager {

    boolean isScript(String scriptPath);

    boolean invoke(HttpServletRequest request, HttpServletResponse response, Collection<String> scriptPaths) throws GlowwormException;
}
