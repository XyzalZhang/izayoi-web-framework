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

package org.withinsea.izayoi.adapter.springmvc;

import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.withinsea.izayoi.core.conf.IzayoiContainerFactory;
import org.withinsea.izayoi.cortile.Cortile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-8
 * Time: 17:12:13
 */
public class SpringCortileViewResolver extends UrlBasedViewResolver implements ApplicationContextAware {

    protected class CortileView extends AbstractUrlBasedView {
        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            exposeModelAsRequestAttributes(model, request);
            cortile.doDispatch(request, response, getUrl(), null);
        }
    }

    public static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    protected String configPath;
    protected Cortile cortile;

    protected String suffix = "";

    public SpringCortileViewResolver() {
        this((String) null);
    }

    public SpringCortileViewResolver(String configPath) {
        this.configPath = configPath;
    }

    public SpringCortileViewResolver(Cortile cortile) {
        this.cortile = cortile;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {
        super.initServletContext(servletContext);
        if (cortile == null) {
            cortile = new Cortile();
            cortile.init(new IzayoiContainerFactory()
                    .addBeanSource(new SpringBeanSource(getApplicationContext()))
                    .addModule("org.withinsea.izayoi.adapter.springmvc")
                    .addModule("org.withinsea.izayoi.core")
                    .addModule("org.withinsea.izayoi.cloister")
                    .addModule("org.withinsea.izayoi.glowworm")
                    .addModule("org.withinsea.izayoi.cortile")
                    .create(servletContext, Collections.<String, String>emptyMap()));
        }
    }

    @Override
    protected CortileView buildView(String viewName) throws Exception {
        CortileView view = instantiateView();
        {
            view.setUrl(getPrefix() + "/" + viewName.replaceAll("^/+", "") + getSuffix());
            view.setContentType(getContentType() != null ? getContentType() : DEFAULT_CONTENT_TYPE);
            view.setRequestContextAttribute(getRequestContextAttribute());
            view.setAttributesMap(getAttributesMap());
        }
        return view;
    }

    protected CortileView instantiateView() {
        return new CortileView();
    }

    @Override
    protected String getSuffix() {
        return (suffix != null) ? suffix : "";
    }

    @Override
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    protected void initApplicationContext() {
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void setCortile(Cortile cortile) {
        this.cortile = cortile;
    }
}