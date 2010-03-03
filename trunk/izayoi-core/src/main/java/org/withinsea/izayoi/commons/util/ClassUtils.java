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

package org.withinsea.izayoi.commons.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-20
 * Time: 11:36:03
 */
public class ClassUtils {

    public static Set<String> getPackageClassNames(String packageName) throws IOException {

        Set<String> classNames = new LinkedHashSet<String>();

        String packagePath = packageName.replace('.', '/');

        Enumeration<URL> resourceEnum = Thread.currentThread().getContextClassLoader().getResources(packagePath);
        while (resourceEnum.hasMoreElements()) {
            URL url = resourceEnum.nextElement();
            String protocol = url.getProtocol();
            if ("jar".equals(protocol)) {
                JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                Enumeration<JarEntry> entries = jar.entries();
                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    String name = entry.getName();
                    if (name.startsWith(packagePath) && name.endsWith(".class") && name.indexOf("/", packagePath.length() + 1) < 0) {
                        String className = name.substring(packageName.length() + 1, name.length() - 6);
                        classNames.add(packageName + '.' + className);
                    }
                }
            } else if ("file".equals(protocol)) {
                File folder = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
                if (!folder.exists() || !folder.isDirectory()) {
                    break;
                }
                for (File file : folder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".class");
                    }
                })) {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    classNames.add(packageName + '.' + className);
                }
            }
        }

        return classNames;
    }
}