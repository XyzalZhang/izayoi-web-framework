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

import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.cortile.core.Cortile;
import org.withinsea.izayoi.cortile.core.conf.CortileConfigurator;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.glowworm.core.Glowworm;
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
public class Izayoi implements Filter {

    protected Configurator glowwormConfigurator;
    protected Configurator cortileConfigurator;

    protected Glowworm glowworm;
    protected Cortile cortile;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ServletContext servletContext = filterConfig.getServletContext();
        String configPath = filterConfig.getInitParameter("config-path");

        glowworm = new Glowworm();
        glowworm.setConfigurator((this.glowwormConfigurator != null) ? this.glowwormConfigurator :
                new GlowwormConfigurator());

        cortile = new Cortile();
        cortile.setConfigurator((this.cortileConfigurator != null) ? this.cortileConfigurator :
                new CortileConfigurator());

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
                    try {
                        cortile.doDispatch((HttpServletRequest) req, (HttpServletResponse) resp, null, new FilterChain() {
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
        } catch (IzayoiException e) {
            throw (e.getCause() instanceof ServletException) ? (ServletException) e.getCause() : new ServletException(e);
        }
    }

    @Override
    public void destroy() {
    }

    public void setGlowwormConfigurator(Configurator glowwormConfigurator) {
        this.glowwormConfigurator = glowwormConfigurator;
    }

    public void setCortileConfigurator(Configurator cortileConfigurator) {
        this.cortileConfigurator = cortileConfigurator;
    }

}
