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

package org.withinsea.izayoi.core.conf;

import org.withinsea.izayoi.core.bean.*;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-19
 * Time: 15:29:47
 */
public abstract class ComponentContainer implements BeanContainer {

    protected BeanSource componentSource;
    protected BeanFactory componentFactory;
    protected BeanContainer managedComponentContainer;

    protected ComponentContainer(List<BeanSource> beanSources) {
        this(beanSources, null);
    }

    protected ComponentContainer(List<BeanSource> beanSources, String prefix) {

        this.managedComponentContainer = new LazyCreateBeanContainer() {
            @Override
            public <T> T create(Class<T> claz, Object... args) throws InstantiationException {
                return ComponentContainer.this.create(claz, args);
            }
        };

        List<BeanSource> loopBeanSources = new ArrayList<BeanSource>();
        {
            loopBeanSources.add(managedComponentContainer);
            loopBeanSources.addAll(beanSources);
        }
        this.componentSource = new NameFixedBeanSource(new ChainBeanSource(loopBeanSources), prefix);

        this.componentFactory = new AnnotationInjectBeanFactory(this.componentSource);
    }

    @Override
    public <T> T create(Class<T> claz, Object... args) throws InstantiationException {
        return componentFactory.create(claz, args);
    }

    @Override
    public Set<String> names() {
        return componentSource.names();
    }

    @Override
    public boolean exist(Object bean) {
        return componentSource.exist(bean);
    }

    @Override
    public boolean exist(String name) {
        return componentSource.exist(name);
    }

    @Override
    public boolean exist(Class<?> claz) {
        return componentSource.exist(claz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        Object bean = componentSource.get(name);
        return (T) bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> claz) {
        Object bean = componentSource.get(claz);
        if (bean == null) {
            try {
                bean = create(claz);
            } catch (InstantiationException e) {
                throw new IzayoiRuntimeException(e);
            }
            add(bean);
        }
        return (T) bean;
    }

    @Override
    public <T> List<T> list(String name) {
        return componentSource.list(name);
    }

    @Override
    public <T> List<T> list(Class<T> claz) {
        return componentSource.list(claz);
    }

    @Override
    public void add(Object bean) {
        managedComponentContainer.add(bean);
    }

    @Override
    public void add(Class<?> claz) {
        managedComponentContainer.add(claz);
    }

    @Override
    public void add(String name, String value) {
        managedComponentContainer.add(name, value);
    }

    @Override
    public void add(String name, Class<?> claz) {
        managedComponentContainer.add(name, claz);
    }

    @Override
    public void add(String name, Object bean) {
        managedComponentContainer.add(name, bean);
    }

    @Override
    public void set(String name, String value) {
        managedComponentContainer.set(name, value);
    }

    @Override
    public void set(String name, Class<?> claz) {
        managedComponentContainer.set(name, claz);
    }

    @Override
    public void set(String name, Object bean) {
        managedComponentContainer.set(name, bean);
    }

    @Override
    public void set(Class<?> claz, Object bean) {
        managedComponentContainer.set(claz, bean);
    }

    @Override
    public void remove(String name) {
        managedComponentContainer.remove(name);
    }

    @Override
    public void remove(Object bean) {
        managedComponentContainer.remove(bean);
    }

    @Override
    public void remove(Class<?> claz) {
        managedComponentContainer.remove(claz);
    }

    protected static class NameFixedBeanSource implements BeanSource {

        protected BeanSource beanSource;
        protected String prefix;

        public NameFixedBeanSource(BeanSource beanSource, String prefix) {
            this.beanSource = beanSource;
            this.prefix = prefix;
        }

        @Override
        public Set<String> names() {
            Set<String> original = beanSource.names();
            Set<String> names = new LinkedHashSet<String>(original);
            for (String name : original) {
                names.add(prefix + name);
            }
            return names;
        }

        @Override
        public boolean exist(Object bean) {
            return beanSource.exist(bean);
        }

        @Override
        public boolean exist(String name) {
            return beanSource.exist(name) || beanSource.exist(prefix + name);
        }

        @Override
        public boolean exist(Class<?> claz) {
            return beanSource.exist(claz);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T get(String name) {
            Object bean = beanSource.get(prefix + name);
            if (bean == null) bean = beanSource.get(name);
            return (T) bean;
        }

        @Override
        public <T> T get(Class<T> claz) {
            return beanSource.get(claz);
        }

        @Override
        public <T> List<T> list(String name) {
            List<T> beans = new ArrayList<T>();
            {
                beans.addAll(beanSource.<T>list(name));
                beans.addAll(beanSource.<T>list(prefix + name));
            }
            return beans;
        }

        @Override
        public <T> List<T> list(Class<T> claz) {
            return beanSource.list(claz);
        }
    }
}
