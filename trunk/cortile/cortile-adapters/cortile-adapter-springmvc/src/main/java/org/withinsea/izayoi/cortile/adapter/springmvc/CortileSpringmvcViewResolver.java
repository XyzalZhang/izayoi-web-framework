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
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.withinsea.izayoi.cortile.core.CortileMirage;
import org.withinsea.izayoi.cortile.core.CortileScenery;
import org.withinsea.izayoi.cortile.core.exception.CortileRuntimeException;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-8
 * Time: 17:12:13
 */
public class CortileSpringmvcViewResolver extends UrlBasedViewResolver {

    protected final CortileMirage mirage = new CortileMirage();
    protected final CortileScenery scenery = new CortileScenery();
    protected final Hashtable<String, String> initParams = new Hashtable<String, String>();

    public CortileSpringmvcViewResolver() {
        this(Collections.<String, String>emptyMap());
    }

    public CortileSpringmvcViewResolver(Map<String, String> initParams) {
        this.initParams.putAll(initParams);
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {
        CortileSpringmvcView view = new CortileSpringmvcView(mirage, scenery, getPrefix() + viewName + getSuffix());
        String contentType = getContentType();
        if (contentType != null) {
            view.setContentType(contentType);
        }
        view.setRequestContextAttribute(getRequestContextAttribute());
        view.setAttributesMap(getAttributesMap());
        return view;
    }

    @Override
    protected void initServletContext(final ServletContext servletContext) {

        super.initServletContext(servletContext);

        try {

            mirage.init(new FilterConfig() {

                @Override
                public String getFilterName() {
                    return "cortile-mirage";
                }

                @Override
                public ServletContext getServletContext() {
                    return servletContext;
                }

                @Override
                public String getInitParameter(String name) {
                    return initParams.get(name);
                }

                @Override
                public Enumeration getInitParameterNames() {
                    return initParams.keys();
                }
            });

            scenery.init(new ServletConfig() {

                @Override
                public String getServletName() {
                    return "cortile-scenery";
                }

                @Override
                public ServletContext getServletContext() {
                    return servletContext;
                }

                @Override
                public String getInitParameter(String name) {
                    return initParams.get(name);
                }

                @Override
                public Enumeration getInitParameterNames() {
                    return initParams.keys();
                }
            });

        } catch (ServletException e) {
            throw new CortileRuntimeException(e);
        }
    }

    public Hashtable<String, String> getInitParams() {
        return initParams;
    }
}
