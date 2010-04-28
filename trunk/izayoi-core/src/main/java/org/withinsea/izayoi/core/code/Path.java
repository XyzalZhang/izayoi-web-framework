package org.withinsea.izayoi.core.code;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-23
 * Time: 15:23:32
 */
public class Path {

    protected final String path;
    protected final String folder;
    protected final String name;
    protected final String role;
    protected final String type;

    public Path(String path) {

        path = ("/" + path.trim()).replaceAll("/+", "/").trim();
        this.path = (!path.equals("/") && path.endsWith("/")) ? path.substring(0, path.length() - 1) : path;
        this.folder = path.replaceAll("/[^/]*$", "/");
        this.name = path.replaceAll(".*/", "");

        String[] split = name.split("\\.");
        this.role = (split.length < 2) ? "" : split[split.length - 2];
        this.type = (split.length < 1) ? "" : split[split.length - 1];
    }

    public String getFolder() {
        return folder;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getRole() {
        return role;
    }

    public String getType() {
        return type;
    }
}
