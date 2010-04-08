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
 * Date: 2010-3-5
 * Time: 1:02:24
 */
public class PathUtils {

    public static String normalizePath(String path) {
        path = path.replaceAll("/+", "/").trim();
        return path.endsWith("/") ? path.substring(path.length() - 1) : path;
    }

    public static String getFolderPath(String path) {
        path = normalizePath(path);
        return (path.indexOf("/") < 0) ? "" : path.replaceAll("/[^/]*$", "");
    }

    public static String getName(String path) {
        return normalizePath(path).replaceAll(".*/", "");
    }

    public static String getMainName(String path) {
        return getName(path).replaceAll("\\.[^\\.]*$", "");
    }

    public static String getExtName(String path) {
        String name = getName(path);
        return (name.indexOf(".") < 0) ? "" : name.replaceAll(".*\\.", "");
    }
}
