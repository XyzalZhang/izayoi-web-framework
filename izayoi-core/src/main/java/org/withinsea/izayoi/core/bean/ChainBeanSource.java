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

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-19
 * Time: 15:29:47
 */
public class ChainBeanSource implements BeanSource {

    protected List<BeanSource> beanSources;

    public ChainBeanSource(BeanSource... beanSources) {
        this(Arrays.asList(beanSources));
    }

    public ChainBeanSource(List<BeanSource> beanSources) {
        this.beanSources = beanSources;
    }

    @Override
    public boolean exist(Object bean) {
        for (BeanSource bs : getBeanSources()) {
            try {
                if (bs.exist(bean)) {
                    return true;
                }
            } catch (UnsupportedOperationException uoe) {
                // do nothing
            }
        }
        return false;
    }

    @Override
    public boolean exist(Class<?> claz) {
        for (BeanSource bs : getBeanSources()) {
            try {
                if (bs.exist(claz)) {
                    return true;
                }
            } catch (UnsupportedOperationException uoe) {
                // do nothing
            }
        }
        return false;
    }

    @Override
    public boolean exist(String name) {
        for (BeanSource bs : getBeanSources()) {
            try {
                if (bs.exist(name)) {
                    return true;
                }
            } catch (UnsupportedOperationException uoe) {
                // do nothing
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> claz) {
        for (BeanSource bs : getBeanSources()) {
            try {
                Object bean = bs.get(claz);
                if (bean != null) {
                    return (T) bean;
                }
            } catch (UnsupportedOperationException uoe) {
                // do nothing
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        for (BeanSource bs : getBeanSources()) {
            try {
                Object bean = bs.get(name);
                if (bean != null) {
                    return (T) bean;
                }
            } catch (UnsupportedOperationException uoe) {
                // do nothing
            }
        }
        return null;
    }

    @Override
    public <T> List<T> list(Class<T> claz) {
        Set<T> beanset = new LinkedHashSet<T>();
        for (BeanSource bs : getBeanSources()) {
            try {
                beanset.addAll(bs.list(claz));
            } catch (UnsupportedOperationException uoe) {
                // do nothing
            }
        }
        return new ArrayList<T>(beanset);
    }

    @Override
    public <T> List<T> list(String name) {
        Set<T> beanset = new LinkedHashSet<T>();
        for (BeanSource bs : getBeanSources()) {
            try {
                beanset.addAll(bs.<T>list(name));
            } catch (UnsupportedOperationException uoe) {
                // do nothing
            }
        }
        return new ArrayList<T>(beanset);
    }

    public List<BeanSource> getBeanSources() {
        return beanSources;
    }

    public void setBeanSources(List<BeanSource> beanSources) {
        this.beanSources = beanSources;
    }
}