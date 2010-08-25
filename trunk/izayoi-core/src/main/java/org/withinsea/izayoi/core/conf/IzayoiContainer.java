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

import org.withinsea.izayoi.core.bean.BeanContainer;
import org.withinsea.izayoi.core.bean.BeanSource;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 15:13:00
 */
public class IzayoiContainer extends ComponentContainer implements BeanContainer {

    protected static final String CONTAINERS_ATTR = IzayoiContainer.class.getCanonicalName() + ".CONTAINERS";

    public static void store(ServletContext servletContext, String retrievalKey, IzayoiContainer container) {
        getContainers(servletContext).put(retrievalKey, container);
    }

    public static IzayoiContainer retrieval(ServletContext servletContext, String retrievalKey) {
        return getContainers(servletContext).get(retrievalKey);
    }

    protected synchronized static Map<String, IzayoiContainer> getContainers(ServletContext servletContext) {
        @SuppressWarnings("unchecked")
        Map<String, IzayoiContainer> containers = (Map<String, IzayoiContainer>) servletContext.getAttribute(CONTAINERS_ATTR);
        if (containers == null) {
            containers = new HashMap<String, IzayoiContainer>();
            servletContext.setAttribute(CONTAINERS_ATTR, containers);
        }
        return containers;
    }

    public IzayoiContainer(List<BeanSource> beanSources, String prefix) {
        super(beanSources, prefix);
    }
}
