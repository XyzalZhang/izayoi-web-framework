package org.withinsea.izayoi.cloister.core.kernal;

import org.withinsea.izayoi.cloister.core.exception.CloisterRuntimeException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-27
 * Time: 上午9:31
 */
public class CodefilePath {

    protected String path;
    protected String folder = "";
    protected String fullname = "";
    protected boolean isFolder;

    public CodefilePath(String path) {

        if (!path.startsWith("/")) {
            throw new CloisterRuntimeException("invalid path (must be start with /): " + path);
        }

        this.path = path.replaceAll("/+", "/");

        isFolder = path.endsWith("/");
        if (isFolder) {
            folder = path;
            fullname = path.equals("/") ? "" : path.substring(path.lastIndexOf("/", path.length() - 2) + 1, path.length() - 1);
        } else {
            folder = path.substring(0, path.lastIndexOf("/") + 1);
            fullname = path.substring(folder.length());
        }
    }

    public String getFolder() {
        return folder;
    }

    public String getFullname() {
        return fullname;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String getPath() {
        return path;
    }
}
