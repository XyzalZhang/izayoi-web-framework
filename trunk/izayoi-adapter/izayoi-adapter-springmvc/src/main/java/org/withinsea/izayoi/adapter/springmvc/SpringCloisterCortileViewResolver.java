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

import org.withinsea.izayoi.cloister.core.Cloister;
import org.withinsea.izayoi.cortile.core.Cortile;

import javax.servlet.ServletContext;
import java.util.Locale;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-13
 * Time: 13:10:10
 */
public class SpringCloisterCortileViewResolver extends SpringCortileViewResolver {

    protected class CortilePathVariablesView extends CortileView {
        @Override
        public boolean checkResource(Locale locale) throws Exception {
            if (super.checkResource(locale)) {
                return true;
            } else {
                setUrl(cloister.findTemplatePath(getUrl()));
                return super.checkResource(locale);
            }
        }
    }

    protected Cloister cloister;

    public SpringCloisterCortileViewResolver() {
        super();
    }

    public SpringCloisterCortileViewResolver(String configPath) {
        super(configPath);
    }

    public SpringCloisterCortileViewResolver(Cortile cortile, Cloister cloister) {
        super(cortile);
        this.cloister = cloister;
    }

    @Override
    protected void initServletContext(ServletContext servletContext) {
        super.initServletContext(servletContext);
        if (cloister == null) {
            cloister = new Cloister();
            cloister.setConfigurator(new SpringCloisterConfigurator(getApplicationContext()));
            cloister.init(servletContext, configPath);
        }
    }

    @Override
    protected CortileView instantiateView() {
        return new CortilePathVariablesView();
    }

    public void setCloister(Cloister cloister) {
        this.cloister = cloister;
    }
}
