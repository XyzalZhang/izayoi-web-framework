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
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.withinsea.izayoi.cloister.core.Cloister;
import org.withinsea.izayoi.commons.servlet.FilterUtils;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;
import org.withinsea.izayoi.cortile.core.Cortile;
import org.withinsea.izayoi.glowworm.core.Glowworm;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
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

    protected static final String CARRIER_CHAIN_ATTR = SpringIzayoiDispatcherFilter.class.getCanonicalName() + ".CARRIER_CHAIN";

    protected boolean usingDefaultViewResolver = false;

    protected Configurator cloisterConfigurator;
    protected Configurator glowwormConfigurator;
    protected Configurator cortileConfigurator;
    protected Cloister cloister;
    protected Glowworm glowworm;
    protected Cortile cortile;
    protected SpringGlowwormInterceptor izayoiInterceptor;
    protected SpringCloisterCortileViewResolver izayoiViewResolver;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {

        ServletContext servletContext = filterConfig.getServletContext();
        String configPath = filterConfig.getInitParameter("config-path");

        cloister = new Cloister();
        glowworm = new Glowworm();
        cortile = new Cortile();
        izayoiInterceptor = new SpringGlowwormInterceptor(glowworm);
        izayoiViewResolver = new SpringCloisterCortileViewResolver(cortile, cloister);

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

        cloister.setConfigurator((this.cloisterConfigurator != null) ? this.cloisterConfigurator :
                new SpringCloisterConfigurator(getWebApplicationContext()));
        glowworm.setConfigurator((this.glowwormConfigurator != null) ? this.glowwormConfigurator :
                new SpringGlowwormConfigurator(getWebApplicationContext()));
        cortile.setConfigurator((this.cortileConfigurator != null) ? this.cortileConfigurator :
                new SpringCortileConfigurator(getWebApplicationContext()));

        cloister.init(servletContext, configPath);
        glowworm.init(servletContext, configPath);
        cortile.init(servletContext, configPath);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        req.setAttribute(CARRIER_CHAIN_ATTR, chain);
        service(req, resp);
        req.removeAttribute(CARRIER_CHAIN_ATTR);
    }

    @Override
    protected HandlerExecutionChain getHandler(HttpServletRequest request, boolean cache) throws Exception {
        HandlerExecutionChain mappedHandler = super.getHandler(request, cache);
        if (mappedHandler == null || mappedHandler.getHandler() == null) {
            return mappedHandler;
        } else {
            HandlerInterceptor[] originalInterceptors = mappedHandler.getInterceptors();
            HandlerInterceptor[] interceptors = new HandlerInterceptor[originalInterceptors.length + 1];
            interceptors[0] = izayoiInterceptor;
            System.arraycopy(originalInterceptors, 0, interceptors, 1, originalInterceptors.length);
            return new HandlerExecutionChain(mappedHandler.getHandler(), interceptors);
        }
    }

    @Override
    protected void noHandlerFound(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        FilterChain chain = (FilterChain) req.getAttribute(CARRIER_CHAIN_ATTR);
        req.removeAttribute(CARRIER_CHAIN_ATTR);
        if (chain == null) {
            noHandlerFound0(req, resp);
        } else {
            FilterUtils.chain(req, resp, chain, cloister, glowworm, cortile);
        }
    }

    protected void noHandlerFound0(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.noHandlerFound(request, response);
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

    @Override
    protected void initStrategies(ApplicationContext context) {
        super.initStrategies(context);
        SpringCloisterCortileViewResolver cloisterCortileViewResolver = (SpringCloisterCortileViewResolver)
                context.getAutowireCapableBeanFactory().initializeBean(izayoiViewResolver, "cortilePathVariablesViewResolver");
        try {
            Field field = DispatcherServlet.class.getDeclaredField("viewResolvers");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<ViewResolver> viewResolvers = (List<ViewResolver>) field.get(this);
            if (viewResolvers == null) {
                field.set(this, Arrays.asList(cloisterCortileViewResolver));
            } else if (usingDefaultViewResolver) {
                viewResolvers.add(0, cloisterCortileViewResolver);
            } else {
                viewResolvers.add(cloisterCortileViewResolver);
            }
        } catch (Exception e) {
            throw new IzayoiRuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public void setCloisterConfigurator(Configurator cloisterConfigurator) {
        this.cloisterConfigurator = cloisterConfigurator;
    }

    public void setGlowwormConfigurator(Configurator glowwormConfigurator) {
        this.glowwormConfigurator = glowwormConfigurator;
    }

    public void setCortileConfigurator(Configurator cortileConfigurator) {
        this.cortileConfigurator = cortileConfigurator;
    }
}
