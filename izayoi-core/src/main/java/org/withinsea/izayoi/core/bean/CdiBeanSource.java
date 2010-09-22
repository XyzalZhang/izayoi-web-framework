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

package org.withinsea.izayoi.core.bean;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-17
 * Time: 17:52:11
 */
public class CdiBeanSource implements BeanSource {

    @Override
    public Set<String> names() {
        try {
            Class.forName("javax.enterprise.inject.spi.BeanManager");
            return CDIHelper.listNames();
        } catch (ClassNotFoundException cnfe) {
            return Collections.emptySet();
        }
    }

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
        try {
            Class.forName("javax.enterprise.inject.spi.BeanManager");
            return CDIHelper.list(name);
        } catch (ClassNotFoundException cnfe) {
            return Collections.emptyList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> list(Class<T> claz) {
        try {
            Class.forName("javax.enterprise.inject.spi.BeanManager");
            return CDIHelper.list(claz);
        } catch (ClassNotFoundException cnfe) {
            return Collections.emptyList();
        }
    }

    // lazy load CDI jars

    protected static class CDIHelper {

        public static Set<String> listNames() {
            BeanManager beanManager = lookupBeanManager();
            if (beanManager == null) {
                return Collections.emptySet();
            }
            Set<String> names = new LinkedHashSet<String>();
            for (Bean<?> bean : beanManager.getBeans(Object.class)) {
                names.add(bean.getName());
            }
            return names;
        }

        @SuppressWarnings("unchecked")
        public static <T> List<T> list(String name) {
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

        @SuppressWarnings("unchecked")
        public static <T> List<T> list(Class<T> claz) {
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

        protected static BeanManager lookupBeanManager() {
            try {
                return (BeanManager) JndiBeanSource.lookupJndi("BeanManager");
            } catch (Exception ex) {
                return null;
            }
        }
    }
}