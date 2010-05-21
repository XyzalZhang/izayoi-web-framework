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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ByteArrayBufferedHttpServletResponseWrapper extends HttpServletResponseWrapper implements BufferedHttpServletResponse {

    protected ByteArrayServletOutputStream bufferStream;
    protected PrintWriter bufferWriter;
    protected boolean committed = false;

    public ByteArrayBufferedHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public byte[] getBuffer() {
        return bufferStream.toByteArray();
    }

    @Override
    public synchronized void commit() throws IOException {
        if (bufferWriter != null) {
            bufferWriter.close();
            bufferWriter = null;
        }
        bufferStream.close();
        bufferStream = null;
        byte[] content = wrapBuffer(getBuffer());
        getResponse().getOutputStream().write(content);
        getResponse().flushBuffer();
        committed = true;
    }

    protected byte[] wrapBuffer(byte[] buffer) throws IOException {
        return buffer;
    }

    @Override
    public synchronized ByteArrayServletOutputStream getOutputStream() {
        if (bufferStream == null) {
            bufferStream = new ByteArrayServletOutputStream();
        }
        return bufferStream;
    }

    @Override
    public synchronized PrintWriter getWriter() throws IOException {
        if (bufferWriter == null) {
            bufferWriter = new PrintWriter(new OutputStreamWriter(getOutputStream(), this.getCharacterEncoding()));
        }
        return bufferWriter;
    }

    @Override
    public void setBufferSize(int size) {
        getOutputStream().enlarge(size);
    }

    @Override
    public int getBufferSize() {
        return getOutputStream().size();
    }

    @Override
    public void flushBuffer() throws IOException {
    }

    @Override
    public boolean isCommitted() {
        return committed;
    }

    @Override
    public void reset() {
        getResponse().reset();
        bufferStream = null;
        bufferWriter = null;
    }

    @Override
    public void resetBuffer() {
        getResponse().resetBuffer();
        bufferStream = null;
        bufferWriter = null;
    }
}