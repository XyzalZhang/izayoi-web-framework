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

import java.io.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-11
 * Time: 11:51:11
 */
public class IOUtils {

    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static void write(String data, Writer writer) throws IOException {
        if (data != null) {
            writer.write(data);
        }
    }

    public static void write(String data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(encoding));
        }
    }

    public static void write(String data, File file, String encoding) throws IOException {
        File parentFile = file.getParentFile();
        if ((parentFile.exists() && parentFile.isDirectory()) || parentFile.mkdirs()) {
            FileOutputStream fos = new FileOutputStream(file);
            write(data, fos, encoding);
            fos.close();
        }
    }

    public static String toString(Reader reader) throws IOException {
        StringWriter writer = new StringWriter();
        copyLarge(reader, writer);
        return writer.toString();
    }

    public static String toString(InputStream is, String encoding) throws IOException {
        return toString(new InputStreamReader(is, encoding));
    }

    public static String toString(File file, String encoding) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        String str = toString(new InputStreamReader(fis, encoding));
        fis.close();
        return str;
    }

    public static long copyLarge(Reader input, Writer output) throws IOException {
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        long count = 0;
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}
