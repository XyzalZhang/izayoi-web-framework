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

import org.withinsea.izayoi.commons.util.IOUtils;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-15
 * Time: 10:32:41
 */
public class FileCode implements Code {

    public static final String DEFAULT_ENCODING = "UTF-8";

    protected final File file;
    protected final Path path;
    protected final String encoding;

    public FileCode(File base, String path) {
        this(base, path, DEFAULT_ENCODING);
    }

    public FileCode(File base, String path, String encoding) {
        this.path = new Path(path);
        this.file = new File(base, this.path.getPath());
        this.encoding = encoding;
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public String getType() {
        return path.getType();
    }

    @Override
    public String getCode() {
        if (file.isDirectory()) {
            return null;
        } else {
            try {
                return IOUtils.toString(getFile(), getEncoding());
            } catch (IOException e) {
                throw new IzayoiRuntimeException(e);
            }
        }
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    public File getFile() {
        return file;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setCode(String code) {
        try {
            IOUtils.write(code, file, encoding);
        } catch (IOException e) {
            throw new IzayoiRuntimeException(e);
        }
    }

    public void setLastModified(long lastModified) {
        file.setLastModified(lastModified);
    }
}