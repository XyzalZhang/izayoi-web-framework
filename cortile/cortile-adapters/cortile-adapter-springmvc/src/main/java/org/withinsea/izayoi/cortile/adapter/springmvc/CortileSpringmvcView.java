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

package org.withinsea.izayoi.cortile.adapter.springmvc;

import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.withinsea.izayoi.cortile.core.CortileMirage;
import org.withinsea.izayoi.cortile.core.CortileScenery;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-8
 * Time: 18:04:37
 */
public class CortileSpringmvcView extends AbstractUrlBasedView {

    protected CortileMirage mirage;
    protected CortileScenery scenery;

    public CortileSpringmvcView(CortileMirage mirage, CortileScenery scenery) {
        this(mirage, scenery, null);
    }

    public CortileSpringmvcView(CortileMirage mirage, CortileScenery scenery, String url) {
        super(url);
        this.mirage = mirage;
        this.scenery = scenery;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        exposeModelAsRequestAttributes(model, request);

        request = (getUrl() == null) ? request : new UrlHttpServletRequestWrapper(request);

        mirage.doFilter(request, response, new FilterChain() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                scenery.service(request, response);
            }
        });
    }

    public CortileMirage getMirage() {
        return mirage;
    }

    public void setMirage(CortileMirage mirage) {
        this.mirage = mirage;
    }

    public CortileScenery getScenery() {
        return scenery;
    }

    public void setScenery(CortileScenery scenery) {
        this.scenery = scenery;
    }

    protected class UrlHttpServletRequestWrapper extends HttpServletRequestWrapper {

        protected boolean forwarded = false;

        public UrlHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getServletPath() {
            return (forwarded) ? super.getServletPath() : getUrl();
        }

        @Override
        public RequestDispatcher getRequestDispatcher(String path) {
            forwarded = true;
            return super.getRequestDispatcher(path);
        }
    }
}
