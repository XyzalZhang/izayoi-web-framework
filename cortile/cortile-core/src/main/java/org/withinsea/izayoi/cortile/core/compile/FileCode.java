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

package org.withinsea.izayoi.cortile.core.compile;

import org.apache.commons.io.FileUtils;
import org.withinsea.izayoi.cortile.core.exception.CortileRuntimeException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-15
 * Time: 10:32:41
 */
public class FileCode implements Code {

    public static final String DEFAULT_ENCODING = "UTF-8";

    private final File base;
    private final String path;
    private final String encoding;

    public FileCode(File base, String path) {
        this(base, path, DEFAULT_ENCODING);
    }

    public FileCode(File base, String path, String encoding) {
        this.base = base;
        this.path = path;
        this.encoding = encoding;
    }

    public File getFile() {
        return new File(base, getPath());
    }

    public String getEncoding() {
        return encoding;
    }

    public String getPath() {
        return path;
    }

    public String getCode() {
        try {
            return FileUtils.readFileToString(getFile(), getEncoding());
        } catch (IOException e) {
            throw new CortileRuntimeException(e);
        }
    }

    public void setCode(String code) {
        try {
            FileUtils.writeStringToFile(getFile(), code, getEncoding());
        } catch (IOException e) {
            throw new CortileRuntimeException(e);
        }
    }

    public long getLastModified() {
        return getFile().lastModified();
    }
}