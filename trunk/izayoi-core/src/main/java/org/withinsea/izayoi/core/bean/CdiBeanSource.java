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

package org.withinsea.izayoi.core.bean;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-17
 * Time: 17:52:11
 */
public class CdiBeanSource implements BeanSource {

    @Override
    public boolean exist(Object bean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean exist(String name) {
        return get(name) != null;
    }

    @Override
    public boolean exist(Class<?> claz) {
        return get(claz) != null;
    }

    @Override
    public <T> T get(String name) {
        List<T> beans = list(name);
        return beans.isEmpty() ? null : beans.get(0);
    }

    @Override
    public <T> T get(Class<T> claz) {
        List<T> beans = list(claz);
        return beans.isEmpty() ? null : beans.get(0);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> list(String name) {
        BeanManager beanManager = lookupBeanManager();
        if (beanManager == null) {
            return Collections.emptyList();
        }
        List<T> beans = new ArrayList<T>();
        for (Bean<?> bean : beanManager.getBeans(name)) {
            beans.add((T) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)));
        }
        return beans;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<T> claz) {
        BeanManager beanManager = lookupBeanManager();
        if (beanManager == null) {
            return Collections.emptyList();
        }
        List<T> beans = new ArrayList<T>();
        for (Bean<?> bean : beanManager.getBeans(claz, claz.getAnnotations())) {
            beans.add((T) beanManager.getReference(bean, bean.getBeanClass(), beanManager.createCreationalContext(bean)));
        }
        return beans;
    }

    protected BeanManager lookupBeanManager() {
        try {
            return (BeanManager) JndiBeanSource.lookupJndi("BeanManager");
        } catch (Exception ex) {
            return null;
        }
    }
}