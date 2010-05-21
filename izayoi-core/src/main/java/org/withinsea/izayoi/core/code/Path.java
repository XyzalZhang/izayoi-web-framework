/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF
 *
 * ANY KIND, either express or implied. See the License for the specific language governing rights and
 *
 * limitations under the License.
 *
 * The Original Code is the IZAYOI web framework.
 *
 * The Initial Developer of the Original Code is
 *
 *   Mo Chen <withinsea@gmail.com>
 *
 * Portions created by the Initial Developer are Copyright (C) 2009-2010
 * the Initial Developer. All Rights Reserved.
 */

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
    protected final String type;

    protected boolean appendant;
    protected final String mainName;
    protected final String mainType;
    protected final String appendantRole;
    protected final String appendantType;

    public Path(String path) {

        path = ("/" + path.trim()).replaceAll("/+", "/").trim();
        this.path = (!path.equals("/") && path.endsWith("/")) ? path.substring(0, path.length() - 1) : path;
        this.folder = path.replaceAll("/[^/]*$", "/");
        this.name = path.replaceAll(".*/", "");

        String[] split = name.split("\\.");
        this.type = split.length > 1 ? split[split.length - 1] : "";
        this.appendant = split.length > 2;
        this.appendantRole = appendant ? split[split.length - 2] : "";
        this.appendantType = appendant ? split[split.length - 1] : "";
        this.mainName = appendant ? name.substring(0, name.length() - appendantRole.length() - appendantType.length() - 2) : name;
        this.mainType = appendant ? (split.length > 3 ? split[split.length - 3] : "") : (split.length > 1 ? split[split.length - 1] : "");
    }

    public boolean isAppendant() {
        return appendant;
    }

    public String getAppendantRole() {
        return appendantRole;
    }

    public String getAppendantType() {
        return appendantType;
    }

    public String getFolder() {
        return folder;
    }

    public String getMainName() {
        return mainName;
    }

    public String getMainType() {
        return mainType;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }
}