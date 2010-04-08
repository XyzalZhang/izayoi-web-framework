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

import org.withinsea.izayoi.core.conf.Configurator;
import org.withinsea.izayoi.cortile.core.Cortile;
import org.withinsea.izayoi.glowworm.core.Glowworm;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormException;
import org.withinsea.izayoi.glowworm.core.exception.GlowwormRuntimeException;

import javax.servlet.ServletContext;
import java.util.Locale;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-13
 * Time: 13:10:10
 */
public class SpringCortilePathVariablesViewResolver extends SpringCortileViewResolver {

    protected class CortilePathVariablesView extends CortileView {
        @Override
        public boolean checkResource(Locale locale) throws Exception {
            if (super.checkResource(locale)) {
                return true;
            } else {
                setUrl(glowworm.findTemplatePath(getUrl()));
                return super.checkResource(locale);
            }
        }
    }

    protected Glowworm glowworm;

    public SpringCortilePathVariablesViewResolver() {
        super();
    }

    public SpringCortilePathVariablesViewResolver(String configPath) {
        super(configPath);
    }

    public SpringCortilePathVariablesViewResolver(Cortile scenery, Glowworm glowworm) {
        super(scenery);
        this.glowworm = glowworm;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {

        super.initServletContext(servletContext);

        if (glowworm == null) {

            try {

                Configurator configurator = new SpringGlowwormConfigurator(getApplicationContext());

                glowworm = new Glowworm();
                glowworm.setConfigurator(configurator);
                glowworm.init(servletContext, configPath);

            } catch (GlowwormException e) {
                throw new GlowwormRuntimeException(e);
            }
        }
    }

    @Override
    protected CortileView instantiateView() {
        return new CortilePathVariablesView();
    }

    public void setGlowworm(Glowworm glowworm) {
        this.glowworm = glowworm;
    }
}
