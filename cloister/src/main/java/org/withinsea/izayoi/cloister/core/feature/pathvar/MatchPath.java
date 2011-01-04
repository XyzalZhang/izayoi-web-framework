package org.withinsea.izayoi.cloister.core.feature.pathvar;

import org.withinsea.izayoi.cloister.core.kernal.CodefilePath;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-27
 * Time: 上午8:50
 */
public class MatchPath extends CodefilePath {

    protected String basicname = "";

    public MatchPath(String path) {
        super(path);
        int point = fullname.indexOf(".");
        basicname = (point < 0) ? fullname : fullname.substring(0, point);
    }

    public String getBasicname() {
        return basicname;
    }
}
