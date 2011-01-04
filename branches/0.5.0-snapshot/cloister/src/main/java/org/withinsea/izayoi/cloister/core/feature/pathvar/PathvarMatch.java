package org.withinsea.izayoi.cloister.core.feature.pathvar;

import org.withinsea.izayoi.cloister.core.kernal.Environment;

import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-15
 * Time: 下午1:15
 */
public interface PathvarMatch {

    Environment.Codefile getCodefile();

    Map<String, String> getVars();
}
