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

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-15
 * Time: 10:24:09
 */
public class DefaultCodeContainer implements CodeContainer {

    @Resource
    ServletContext servletContext;

    @Resource
    String encoding;

    @Resource
    Map<String, String> mimeTypes;

    @Override
    public String getMimeType(String extName) {
        extName = (extName == null) ? "" : extName;
        String mimeType = servletContext.getMimeType("f." + extName);
        if (mimeType == null) mimeType = mimeTypes.get(extName);
        return mimeType;
    }

    @Override
    public List<String> listNames(String folderPath) {
        File folder = new File(getWebroot(), folderPath);
        return !folder.isDirectory() ? Collections.<String>emptyList() :
                Arrays.asList(new File(getWebroot(), folderPath).list());
    }

    @Override
    public List<String> listNames(String folderPath, final String regex) {
        File folder = new File(getWebroot(), folderPath);
        return !folder.isDirectory() ? Collections.<String>emptyList() : Arrays.asList(
                new File(getWebroot(), folderPath).list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.matches(regex);
                    }
                })
        );
    }

    @Override
    public boolean exist(String path) {
        return new File(getWebroot(), path).exists();
    }

    @Override
    public boolean isFolder(String path) {
        return new File(getWebroot(), path).isDirectory();
    }

    @Override
    public Code get(String path) {
        return new FileCode(getWebroot(), path, encoding);
    }

    @Override
    public void update(String path, String code, boolean protectLastModified) {
        FileCode fileCode = new FileCode(webroot, path, encoding);
        long lastModified = fileCode.getLastModified();
        fileCode.setCode(code);
        if (protectLastModified) {
            fileCode.setLastModified(lastModified);
        }
    }

    @Override
    public boolean delete(String path) {
        return new FileCode(getWebroot(), path, encoding).getFile().delete();
    }

    protected File webroot;

    protected File getWebroot() {
        if (webroot == null) {
            webroot = new File(servletContext.getRealPath("/").replace("%20", " "));
        }
        return webroot;
    }
}