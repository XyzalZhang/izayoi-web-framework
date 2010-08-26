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
 * The Original Code is the @PROJECT_NAME
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
import org.withinsea.izayoi.core.bean.BeanFactory;

import javax.annotation.Resource;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-24
 * Time: 13:54:47
 */
public class SpringSimpleBeanFactory implements BeanFactory {

    @Resource
    ApplicationContext applicationContext;

    public SpringSimpleBeanFactory() {
    }

    public SpringSimpleBeanFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public <T> T create(Class<T> claz, Object... args) throws InstantiationException {
        return applicationContext.getAutowireCapableBeanFactory().createBean(claz);
    }
}
