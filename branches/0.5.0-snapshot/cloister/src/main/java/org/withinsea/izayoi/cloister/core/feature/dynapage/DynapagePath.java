package org.withinsea.izayoi.cloister.core.feature.dynapage;

import org.withinsea.izayoi.cloister.core.kernal.CodefilePath;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-13
 * Time: 下午4:32
 */
public class DynapagePath extends CodefilePath {

    protected String extname = "";

    public DynapagePath(String path) {
        super(path);
        int point = fullname.lastIndexOf(".");
        extname = (point < 0) ? "" : fullname.substring(point + 1);
    }

    public String getExtname() {
        return extname;
    }
}
