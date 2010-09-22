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

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.withinsea.izayoi.core.bean.BeanSource;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-24
 * Time: 13:41:55
 */
public class SpringBeanSource implements BeanSource {

    protected ApplicationContext appctx;

    public SpringBeanSource(ApplicationContext appctx) {
        this.appctx = appctx;
    }

    @Override
    public Set<String> names() {
        Set<String> names = new LinkedHashSet<String>();
        names.add("applicationContext");
        names.addAll(Arrays.asList(appctx.getBeanDefinitionNames()));
        return names;
    }

    @Override
    public boolean exist(Object bean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exist(String name) {
        return name.equals("applicationContext") || appctx.containsBean(name);
    }

    @Override
    public boolean exist(Class<?> claz) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        try {
            return (T) (name.equals("applicationContext") ? appctx : appctx.getBean(name));
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public <T> T get(Class<T> claz) {
        try {
            return appctx.getBean(claz);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    @Override
    public <T> List<T> list(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<T> list(Class<T> claz) {
        try {
            return new ArrayList<T>(appctx.getBeansOfType(claz).values());
        } catch (NoSuchBeanDefinitionException e) {
            return Collections.emptyList();
        }
    }
}
