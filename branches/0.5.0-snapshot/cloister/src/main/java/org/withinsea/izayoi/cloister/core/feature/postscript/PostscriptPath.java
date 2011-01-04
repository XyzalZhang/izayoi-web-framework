package org.withinsea.izayoi.cloister.core.feature.postscript;

import org.withinsea.izayoi.cloister.core.kernal.CodefilePath;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-13
 * Time: 下午4:32
 */
public class PostscriptPath extends CodefilePath {

    protected String baseFullname = "";
    protected String baseMainname = "";
    protected String baseExtname = "";
    protected String roleTypename = "";
    protected String scriptTypename = "";
    protected boolean isPostscript;

    public PostscriptPath(String path) {

        super(path);

        if (isFolder) {
            baseFullname = fullname;
        } else {
            String[] split = fullname.split("\\.");
            if (split.length <= 2) {
                baseFullname = fullname;
            } else {
                scriptTypename = split[split.length - 1];
                roleTypename = split[split.length - 2];
                baseFullname = fullname.substring(0, fullname.length() - scriptTypename.length() - roleTypename.length() - 2);
            }
        }

        int point = baseFullname.indexOf(".");
        baseMainname = (point < 0) ? baseFullname : baseFullname.substring(0, point);
        baseExtname = (point < 0) ? "" : baseFullname.substring(point + 1);

        isPostscript = !roleTypename.equals("");
    }

    public String getBaseExtname() {
        return baseExtname;
    }

    public String getBaseFullname() {
        return baseFullname;
    }

    public String getBaseMainname() {
        return baseMainname;
    }

    public boolean isPostscript() {
        return isPostscript;
    }

    public String getRoleTypename() {
        return roleTypename;
    }

    public String getScriptTypename() {
        return scriptTypename;
    }
}
