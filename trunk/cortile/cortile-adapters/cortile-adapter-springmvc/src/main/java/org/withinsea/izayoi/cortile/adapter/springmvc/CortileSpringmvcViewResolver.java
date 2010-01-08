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
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.core.exception.CortileRuntimeException;

import javax.servlet.ServletContext;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-8
 * Time: 17:12:13
 */
public class CortileSpringmvcViewResolver extends UrlBasedViewResolver {

    protected final CortileMirage mirage = new CortileMirage();
    protected final CortileScenery scenery = new CortileScenery();
    protected final String configPath;

    public CortileSpringmvcViewResolver() {
        this(null);
    }

    public CortileSpringmvcViewResolver(String configPath) {
        this.configPath = configPath;
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
    protected void initApplicationContext() {

    }

    @Override
    protected void initServletContext(final ServletContext servletContext) {
        super.initServletContext(servletContext);
        try {
            mirage.init(servletContext, configPath);
            scenery.init(servletContext, configPath);
        } catch (CortileException e) {
            throw new CortileRuntimeException(e);
        }
    }

    public String getConfigPath() {
        return configPath;
    }
}
