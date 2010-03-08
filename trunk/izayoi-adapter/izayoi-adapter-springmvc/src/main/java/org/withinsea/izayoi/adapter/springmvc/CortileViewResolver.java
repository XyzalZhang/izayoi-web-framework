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

import org.picocontainer.MutablePicoContainer;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.UrlBasedViewResolver;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.cortile.core.CortileMirage;
import org.withinsea.izayoi.cortile.core.CortileScenery;
import org.withinsea.izayoi.cortile.core.conf.CortileConfigurator;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.core.exception.CortileRuntimeException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-8
 * Time: 17:12:13
 */
public class CortileViewResolver extends UrlBasedViewResolver implements ApplicationContextAware {

    public static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";

    protected String configPath;

    protected CortileMirage mirage;
    protected CortileScenery scenery;

    public CortileViewResolver() {
        this(null);
    }

    public CortileViewResolver(String configPath) {
        this.configPath = configPath;
    }

    @Override
    protected AbstractUrlBasedView buildView(String viewName) throws Exception {

        AbstractUrlBasedView view = new AbstractUrlBasedView() {

            @Override
            protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

                exposeModelAsRequestAttributes(model, request);

                mirage.doDispatch(request, response, getUrl(), new FilterChain() {
                    @Override
                    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                        try {
                            scenery.doDispatch((HttpServletRequest) request, (HttpServletResponse) response, getUrl(), null);
                        } catch (ClassCastException e) {
                            throw new ServletException("non-HTTP request or response");
                        } catch (CortileException e) {
                            throw new ServletException(e);
                        }

                    }
                });
            }
        };

        view.setUrl(("/" + getPrefix() + viewName + getSuffix()).replaceAll("^/+", "/"));
        view.setContentType(getContentType() == null ? DEFAULT_CONTENT_TYPE : getContentType());
        view.setRequestContextAttribute(getRequestContextAttribute());
        view.setAttributesMap(getAttributesMap());

        return view;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {

        super.initServletContext(servletContext);

        try {

            Configurator configurator = new CortileConfigurator() {

                @Override
                protected void loadDefaultConf(Properties conf, ServletContext servletContext) throws Exception {
                    super.loadDefaultConf(conf, servletContext);
                    conf.setProperty("class.dependencyManager", "org.withinsea.izayoi.adapter.springmvc.SpringWebContextBindingsManager");
                }

                @Override
                public void initComponents(MutablePicoContainer container, Properties conf) throws Exception {
                    container.addComponent("applicationContext", getApplicationContext());
                    super.initComponents(container, conf);
                }
            };

            mirage = new CortileMirage();
            mirage.setConfigurator(configurator);
            mirage.init(servletContext, configPath);

            scenery = new CortileScenery();
            scenery.setConfigurator(configurator);
            scenery.init(servletContext, configPath);

        } catch (CortileException e) {
            throw new CortileRuntimeException(e);
        }
    }

    @Override
    protected void initApplicationContext() {

    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }
}