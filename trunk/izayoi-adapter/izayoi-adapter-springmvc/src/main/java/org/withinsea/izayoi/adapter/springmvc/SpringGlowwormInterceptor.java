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

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.withinsea.izayoi.commons.servlet.FlagChain;
import org.withinsea.izayoi.glowworm.core.Glowworm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-30
 * Time: 3:31:19
 */
public class SpringGlowwormInterceptor extends HandlerInterceptorAdapter implements ApplicationContextAware {

    protected ApplicationContext applicationContext;
    protected Glowworm glowworm;
    protected String configPath;

    public SpringGlowwormInterceptor() {
        this((String) null);
    }

    public SpringGlowwormInterceptor(String configPath) {
        this.configPath = configPath;
    }

    public SpringGlowwormInterceptor(Glowworm glowworm) {
        this.glowworm = glowworm;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (glowworm == null) {
            glowworm = new Glowworm();
            glowworm.setConfigurator(new SpringGlowwormConfigurator(applicationContext));
            glowworm.init(request.getSession().getServletContext(), configPath);
        }

        FlagChain chain = new FlagChain();
        glowworm.doDispatch(request, response, chain);
        return chain.isInvoked();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public void setGlowworm(Glowworm glowworm) {
        this.glowworm = glowworm;
    }
}