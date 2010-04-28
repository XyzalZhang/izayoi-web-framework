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

import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-21
 * Time: 1:30:39
 */
public interface CodeManager {

    List<String> listNames(String folderPath);

    List<String> listNames(String folderPath, String namePattern);

    boolean exist(String path);

    boolean isFolder(String path);

    Code get(String path);

    void update(String path, String code);

    boolean delete(String path);
}