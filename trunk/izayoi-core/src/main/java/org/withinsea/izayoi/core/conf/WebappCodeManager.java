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

package org.withinsea.izayoi.core.conf;

import java.io.File;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-15
 * Time: 10:24:09
 */
public class WebappCodeManager implements CodeManager {

    private String encoding;

    private File webroot;

    public boolean exist(String path) {
        return new FileCode(webroot, path, encoding).getFile().exists();
    }

    public Code get(String path) {
        return new FileCode(webroot, path, encoding);
    }

    @Override
    public void update(String path, String code) {
        new FileCode(webroot, path, encoding).setCode(code);
    }

    @Override
    public boolean delete(String path) {
        return new FileCode(webroot, path, encoding).getFile().delete();
    }

    @SuppressWarnings("unused")
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    @SuppressWarnings("unused")
    public void setWebroot(File webroot) {
        this.webroot = webroot;
    }
}