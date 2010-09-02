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

import org.withinsea.izayoi.cloister.Cloister;
import org.withinsea.izayoi.commons.servlet.ServletFilterUtils;
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.core.conf.IzayoiContainerFactory;
import org.withinsea.izayoi.cortile.Cortile;
import org.withinsea.izayoi.glowworm.Glowworm;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-6
 * Time: 10:41:06
 */
public class Izayoi implements Filter {

    protected Cloister cloister;
    protected Glowworm glowworm;
    protected Cortile cortile;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        ServletContext servletContext = filterConfig.getServletContext();
        Map<String, String> overriddenProperties = ServletFilterUtils.getParamsMap(filterConfig);

        IzayoiContainer container = new IzayoiContainerFactory()
                .addModule("org.withinsea.izayoi.core")
                .addModule("org.withinsea.izayoi.cloister")
                .addModule("org.withinsea.izayoi.glowworm")
                .addModule("org.withinsea.izayoi.cortile")
                .create(servletContext, overriddenProperties);

        cloister = new Cloister();
        glowworm = new Glowworm();
        cortile = new Cortile();

        cloister.init(container);
        glowworm.init(container);
        cortile.init(container);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        ServletFilterUtils.chain((HttpServletRequest) req, (HttpServletResponse) resp, chain, cloister, glowworm, cortile);
    }

    @Override
    public void destroy() {
    }
}