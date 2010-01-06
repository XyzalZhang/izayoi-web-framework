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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 16:37:49
 */
public class NulHttpServletResponseWrapper extends HttpServletResponseWrapper {

    protected static ServletOutputStream NUL_OS = new ServletOutputStream() {
        @Override
        public void write(int b) throws IOException {
        }
    };

    protected static PrintWriter NUL_WRITER = new PrintWriter(NUL_OS);

    private int size = 32;

    public NulHttpServletResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return NUL_OS;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return NUL_WRITER;
    }

    @Override
    public void setBufferSize(int size) {
        this.size = size;
    }

    @Override
    public int getBufferSize() {
        return size;
    }

    @Override
    public void flushBuffer() throws IOException {
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetBuffer() {
    }

    @Override
    public void sendError(int sc) throws IOException {
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
    }

    @Override
    public void sendRedirect(String location) throws IOException {
    }

    @Override
    public void setDateHeader(String name, long date) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public void setIntHeader(String name, int value) {
    }

    @Override
    public void setStatus(int sc) {
    }

    @Override
    public void setStatus(int sc, String sm) {
    }

    @Override
    public void addIntHeader(String name, int value) {
    }

    @Override
    public void addHeader(String name, String value) {
    }

    @Override
    public void addDateHeader(String name, long date) {
    }

    @Override
    public void addCookie(Cookie cookie) {
    }

    @Override
    public void setCharacterEncoding(String charset) {
    }

    @Override
    public void setContentLength(int len) {
    }

    @Override
    public void setContentType(String type) {
    }

    @Override
    public void setLocale(Locale loc) {
    }
}
