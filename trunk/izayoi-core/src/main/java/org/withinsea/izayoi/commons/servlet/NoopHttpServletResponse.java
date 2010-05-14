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

import org.withinsea.izayoi.commons.util.LazyLinkedHashMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-25
 * Time: 16:37:49
 */
public class NoopHttpServletResponse implements HttpServletResponse {

    protected static final ServletOutputStream NUL_OS = new ServletOutputStream() {
        @Override
        public void write(int b) throws IOException {
        }
    };

    protected static final PrintWriter NUL_WRITER = new PrintWriter(NUL_OS);

    protected int status = 200;
    protected String charset = "UTF-8";
    protected int contentLength = 0;
    protected String contentType = "text/plain; charset=UTF-8";
    protected Locale loc = Locale.getDefault();
    protected int size = 32;
    protected final Set<Cookie> cookies = new LinkedHashSet<Cookie>();
    protected final Map<String, Set<String>> headers = new LazyLinkedHashMap<String, Set<String>>() {
        @Override
        protected Set<String> createValue(String s) {
            return new LinkedHashSet<String>();
        }
    };

    @Override
    public String getCharacterEncoding() {
        return charset;
    }

    @Override
    public String getContentType() {
        return contentType;
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
        headers.clear();
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
        setHeader(name, Long.toString(date));
    }

    @Override
    public void setHeader(String name, String value) {
        headers.get(name).clear();
        headers.get(name).add(value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        setHeader(name, Integer.toString(value));
    }

    @Override
    public void setStatus(int sc) {
        this.status = sc;
    }

    @Override
    @Deprecated
    public void setStatus(int sc, String sm) {
        this.status = sc;
    }

    @Override
    public void addIntHeader(String name, int value) {
        addHeader(name, Integer.toString(value));
    }

    @Override
    public void addHeader(String name, String value) {
        headers.get(name).add(value);
    }

    @Override
    public void addDateHeader(String name, long date) {
        addHeader(name, Long.toString(date));
    }

    @Override
    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return headers.containsKey(name);
    }

    @Override
    public String encodeURL(String url) {
        try {
            return URLEncoder.encode(url, getCharacterEncoding());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String encodeRedirectURL(String url) {
        return encodeURL(url);
    }

    @Override
    @Deprecated
    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    @Override
    @Deprecated
    public String encodeRedirectUrl(String url) {
        return encodeURL(url);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.charset = charset;
    }

    @Override
    public void setContentLength(int len) {
        this.contentLength = len;
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }

    @Override
    public void setLocale(Locale loc) {
        this.loc = loc;
    }

    @Override
    public Locale getLocale() {
        return loc;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public int getStatus() {
        return SC_OK;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return Collections.emptySet();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return Collections.emptySet();
    }
}