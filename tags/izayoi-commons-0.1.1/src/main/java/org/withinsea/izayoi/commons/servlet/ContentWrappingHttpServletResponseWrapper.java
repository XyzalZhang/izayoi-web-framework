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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public abstract class ContentWrappingHttpServletResponseWrapper extends HttpServletResponseWrapper {

    protected abstract byte[] wrap(byte[] content) throws IOException;

    protected ByteArrayServletOutputStream buffer;
    protected PrintWriter bufferWriter;
    protected boolean committed = false;

    public ContentWrappingHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
        buffer = new ByteArrayServletOutputStream();
    }

    public void flushWrapper() throws IOException {
        if (bufferWriter != null)
            bufferWriter.close();
        if (buffer != null)
            buffer.close();
        byte[] content = wrap(buffer.toByteArray());
        getResponse().setContentLength(content.length);
        getResponse().getOutputStream().write(content);
        getResponse().flushBuffer();
        committed = true;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return buffer;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (bufferWriter == null) {
            bufferWriter = new PrintWriter(new OutputStreamWriter(buffer, this.getCharacterEncoding()));
        }
        return bufferWriter;
    }

    @Override
    public void setBufferSize(int size) {
        buffer.enlarge(size);
    }

    @Override
    public int getBufferSize() {
        return buffer.size();
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
        buffer.reset();
    }

    @Override
    public void resetBuffer() {
        getResponse().resetBuffer();
        buffer.reset();
    }
}
