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

package org.withinsea.izayoi.core.context;

import javax.servlet.http.HttpSession;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-10
 * Time: 10:16:18
 */
public class Session extends Application {

    protected final HttpSession session;

    public Session(HttpSession session) {
        super(session.getServletContext());
        this.session = session;
    }

    @Override
    public Object getConstant(String name) {
        return name.equals("session") ? session
                : super.getConstant(name);
    }

    @Override
    public Object getAttribute(String name) {
        Object obj = session.getAttribute(name);
        if (obj == null) obj = super.getAttribute(name);
        return obj;
    }

    @Override
    public void setAttribute(String name, Object obj) {
        session.setAttribute(name, obj);
    }

    public HttpSession getSession() {
        return session;
    }
}