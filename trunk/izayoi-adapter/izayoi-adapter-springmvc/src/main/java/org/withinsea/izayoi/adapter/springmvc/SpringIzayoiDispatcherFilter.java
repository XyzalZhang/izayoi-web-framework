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

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;
import org.withinsea.izayoi.cortile.core.Cortile;
import org.withinsea.izayoi.glowworm.core.Glowworm;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-12
 * Time: 18:57:44
 */
public class SpringIzayoiDispatcherFilter extends DispatcherServlet implements Filter {

    protected static class ChainCarrier extends HttpServletRequestWrapper {

        protected final FilterChain chain;

        public ChainCarrier(HttpServletRequest httpServletRequest, FilterChain chain) {
            super(httpServletRequest);
            this.chain = chain;
        }
    }

    protected Configurator glowwormConfigurator;
    protected Glowworm glowworm;

    protected Configurator cortileConfigurator;
    protected Cortile cortile;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

        ServletContext servletContext = filterConfig.getServletContext();
        String configPath = filterConfig.getInitParameter("config-path");

        glowworm = new Glowworm();
        glowworm.setConfigurator((this.glowwormConfigurator != null) ? this.glowwormConfigurator :
                new SpringGlowwormConfigurator(getWebApplicationContext()));

        cortile = new Cortile();
        cortile.setConfigurator((this.cortileConfigurator != null) ? this.cortileConfigurator :
                new SpringCortileConfigurator(getWebApplicationContext()));

        super.init(new ServletConfig() {

            @Override
            public String getInitParameter(String s) {
                return filterConfig.getInitParameter(s);
            }

            @Override
            public String getServletName() {
                return filterConfig.getFilterName();
            }

            @Override
            public ServletContext getServletContext() {
                return filterConfig.getServletContext();
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return filterConfig.getInitParameterNames();
            }
        });

        try {
            glowworm.init(servletContext, configPath);
            cortile.init(servletContext, configPath);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        try {
            glowworm.doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, new FilterChain() {
                @Override
                public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
                    service(new ChainCarrier((HttpServletRequest) req, chain), (HttpServletResponse) resp);
                }
            });
        } catch (IzayoiException e) {
            throw (e.getCause() instanceof ServletException) ? (ServletException) e.getCause() : new ServletException(e);
        }
    }

    @Override
    protected void noHandlerFound(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if (req instanceof ChainCarrier) {
            cortile.doDispatch(req, resp, null, new FilterChain() {
                @Override
                public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
                    if (req instanceof ChainCarrier) {
                        ((ChainCarrier) req).chain.doFilter(req, resp);
                    } else {
                        try {
                            noHandlerFound0((HttpServletRequest) req, (HttpServletResponse) resp);
                        } catch (Exception e) {
                            throw new SecurityException(e);
                        }
                    }
                }
            });
        } else {
            noHandlerFound0(req, resp);
        }
    }

    protected void noHandlerFound0(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.noHandlerFound(request, response);
    }

    protected boolean usingDefaultViewResolver = false;

    @Override
    protected void initStrategies(ApplicationContext context) {
        super.initStrategies(context);
        appendDefaultCortileViewResolver(context);
    }

    @Override
    protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
        List<T> strategies = super.getDefaultStrategies(context, strategyInterface);
        if (strategyInterface == ViewResolver.class) {
            usingDefaultViewResolver = true;
        } else if (strategyInterface == RequestToViewNameTranslator.class) {
            for (T translator : strategies) {
                if (translator instanceof DefaultRequestToViewNameTranslator) {
                    ((DefaultRequestToViewNameTranslator) translator).setStripExtension(false);
                }
            }
        }
        return strategies;
    }

    protected void appendDefaultCortileViewResolver(ApplicationContext context) {

        SpringCortilePathVariablesViewResolver cortileViewResolver = (SpringCortilePathVariablesViewResolver)
                context.getAutowireCapableBeanFactory().initializeBean(
                        new SpringCortilePathVariablesViewResolver(cortile, glowworm), "cortilePathVariablesViewResolver");

        try {
            Field field = DispatcherServlet.class.getDeclaredField("viewResolvers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ViewResolver> viewResolvers = (List<ViewResolver>) field.get(this);
            if (viewResolvers == null) {
                field.set(this, Arrays.asList(cortileViewResolver));
            } else if (usingDefaultViewResolver) {
                viewResolvers.add(0, cortileViewResolver);
            } else {
                viewResolvers.add(cortileViewResolver);
            }
        } catch (Exception e) {
            throw new IzayoiRuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public void setGlowwormConfigurator(Configurator glowwormConfigurator) {
        this.glowwormConfigurator = glowwormConfigurator;
    }

    public void setCortileConfigurator(Configurator cortileConfigurator) {
        this.cortileConfigurator = cortileConfigurator;
    }
}
