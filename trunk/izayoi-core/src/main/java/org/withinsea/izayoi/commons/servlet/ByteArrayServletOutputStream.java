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

package org.withinsea.izayoi.commons.servlet;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public class ByteArrayServletOutputStream extends ServletOutputStream {

    protected byte buf[];

    protected int count;

    public ByteArrayServletOutputStream() {
        this(32);
    }

    public ByteArrayServletOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        buf = new byte[size];
    }

    public synchronized byte toByteArray()[] {
        return copyOf(buf, count);
    }

    public synchronized void reset() {
        count = 0;
    }

    public synchronized int size() {
        return count;
    }

    public void enlarge(int size) {
        if (size > buf.length) {
            buf = copyOf(buf, Math.max(buf.length << 1, size));
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        int newcount = count + 1;
        enlarge(newcount);
        buf[count] = (byte) b;
        count = newcount;
    }

    private static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
        return copy;
    }
}
