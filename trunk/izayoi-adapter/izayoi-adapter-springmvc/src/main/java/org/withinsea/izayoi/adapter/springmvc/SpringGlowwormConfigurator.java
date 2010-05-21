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
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.glowworm.core.conf.GlowwormConfigurator;

import javax.servlet.ServletContext;
import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-13
 * Time: 1:04:43
 */
public class SpringGlowwormConfigurator extends GlowwormConfigurator {

    protected ApplicationContext applicationContext;

    public SpringGlowwormConfigurator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected void loadDefaultConf(Properties conf, ServletContext servletContext) throws Exception {
        super.loadDefaultConf(conf, servletContext);
        conf.setProperty("class.globalContext", "org.withinsea.izayoi.adapter.springmvc.SpringGlobalContext");
    }

    @Override
    public void initComponents(IzayoiContainer container, Properties conf) throws Exception {
        container.addComponent("applicationContext", applicationContext);
        super.initComponents(container, conf);
    }
}
