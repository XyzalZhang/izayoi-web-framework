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

package org.withinsea.izayoi;

import org.withinsea.izayoi.core.conf.Configurable;
import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.cortile.core.CortileMirage;
import org.withinsea.izayoi.cortile.core.CortileScenery;
import org.withinsea.izayoi.cortile.core.conf.CortileConfigurator;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.glowworm.core.GlowwormFlare;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfigurator;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-6
 * Time: 10:41:06
 */
public class Izayoi implements Filter, Configurable {

    protected Configurator configurator;

    @Override
    public void setConfigurator(Configurator configurator) {
        this.configurator = configurator;
    }

    // api

    protected GlowwormFlare flare;
    protected CortileScenery scenery;
    protected CortileMirage mirage;

    public void init(ServletContext servletContext, String configPath) throws IzayoiException {

        Configurator glowwormConfigurator = (configurator != null) ? configurator : new GlowwormConfigurator();

        flare = new GlowwormFlare();
        flare.setConfigurator(glowwormConfigurator);
        flare.init(servletContext, configPath);

        Configurator cortileConfigurator = (configurator != null) ? configurator : new CortileConfigurator();

        scenery = new CortileScenery();
        scenery.setConfigurator(cortileConfigurator);
        scenery.init(servletContext, configPath);

        mirage = new CortileMirage();
        mirage.setConfigurator(cortileConfigurator);
        mirage.init(servletContext, configPath);
    }

    public void doDispatch(HttpServletRequest req, HttpServletResponse resp, final FilterChain chain) throws IzayoiException {

        flare.doDispatch(req, resp, new FilterChain() {
            @Override
            public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
                try {
                    mirage.doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, new FilterChain() {
                        @Override
                        public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
                            try {
                                scenery.doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, new FilterChain() {
                                    @Override
                                    public void doFilter(ServletRequest req, ServletResponse resp) throws IOException, ServletException {
                                        chain.doFilter(req, resp);
                                    }
                                });
                            } catch (CortileException e) {
                                throw (e.getCause() instanceof ServletException) ? (ServletException) e.getCause() : new ServletException(e);
                            }
                        }
                    });
                } catch (CortileException e) {
                    throw (e.getCause() instanceof ServletException) ? (ServletException) e.getCause() : new ServletException(e);
                }
            }
        });
    }

    // as filter

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            init(filterConfig.getServletContext(), filterConfig.getInitParameter("config-path"));
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse resp = (HttpServletResponse) response;
            try {
                doDispatch(req, resp, chain);
            } catch (IzayoiException e) {
                throw (e.getCause() instanceof ServletException) ? (ServletException) e.getCause() : new ServletException(e);
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
    }
}
