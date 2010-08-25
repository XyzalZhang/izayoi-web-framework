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

package org.withinsea.izayoi.core.conf;

import org.withinsea.izayoi.commons.util.LinkedProperties;
import org.withinsea.izayoi.core.bean.BeanSource;
import org.withinsea.izayoi.core.bean.CdiBeanSource;
import org.withinsea.izayoi.core.bean.ConstantsBeanSource;
import org.withinsea.izayoi.core.bean.JndiBeanSource;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;
import org.withinsea.izayoi.core.interpret.Vars;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-21
 * Time: 14:25:31
 */
public class IzayoiContainerFactory {

    public static final String PREFIX = "izayoi.";
    public static final String CONTEXT_PROPERTIES_PATH = "/WEB-INF/izayoi.properties";

    protected static List<BeanSource> DEFAULT_CONTEXT_SOURCES = new ArrayList<BeanSource>(); static {
        DEFAULT_CONTEXT_SOURCES.add(new CdiBeanSource());
        DEFAULT_CONTEXT_SOURCES.add(new JndiBeanSource());
    }

    protected Configurator configurator;
    protected String prefix;

    protected List<Properties> modulePropertieses = new ArrayList<Properties>();
    protected List<BeanSource> thirdPartyBeanSources = new ArrayList<BeanSource>();

    public IzayoiContainerFactory() {
        this(new DefaultConfigurator(PREFIX), PREFIX);
    }

    public IzayoiContainerFactory(Configurator configurator, String prefix) {
        this.configurator = configurator;
        this.prefix = prefix;
    }

    public IzayoiContainerFactory addModule(String... packageNames) {
        for (String packageName : packageNames) {
            modulePropertieses.add(getDefaultProps(packageName));
        }
        return this;
    }

    public IzayoiContainerFactory addModule(Properties... propertieses) {
        modulePropertieses.addAll(Arrays.asList(propertieses));
        return this;
    }

    public IzayoiContainerFactory addBeanSource(BeanSource... beanSources) {
        thirdPartyBeanSources.addAll(Arrays.asList(beanSources));
        return this;
    }

    public IzayoiContainer create(ServletContext servletContext, Map<?, ?> overriddenProperties) {

        String retrievalKey = generateRetrievalKey(servletContext);

        Vars constants = new Vars();
        List<BeanSource> beanSources = new ArrayList<BeanSource>();
        {
            beanSources.add(new ConstantsBeanSource(constants));
            beanSources.addAll(thirdPartyBeanSources);
            beanSources.addAll(DEFAULT_CONTEXT_SOURCES);
        }

        IzayoiContainer container = new IzayoiContainer(beanSources, prefix);

        constants.putAll(
                "servletContext", servletContext,
                "izayoiContainerRetrievalKey", retrievalKey,
                "izayoiContainer", container
        );

        Properties props = new LinkedProperties();
        {
            for (Properties defaultProp : modulePropertieses) {
                props.putAll(defaultProp);
            }
            props.putAll(getDefaultProps(servletContext));
            props.putAll(overriddenProperties);
        }


        try {
            configurator.configurate(container, props);
        } catch (Exception e) {
            throw new IzayoiRuntimeException(e);
        }

        IzayoiContainer.store(servletContext, retrievalKey, container);

        return container;
    }

    protected String generateRetrievalKey(ServletContext servletContext) {
        String key;
        do {
            key = UUID.randomUUID().toString();
        } while (IzayoiContainer.retrieval(servletContext, key) != null);
        return key;
    }

    protected Properties getDefaultProps(ServletContext servletContext) {
        Properties props = new LinkedProperties();
        try {
            InputStream is = servletContext.getResourceAsStream(CONTEXT_PROPERTIES_PATH);
            if (is != null) {
                props.load(is);
            }
        } catch (IOException e) {
            // do nothings;
        }
        return props;
    }

    protected Properties getDefaultProps(String packageName) {
        Properties props = new LinkedProperties();
        {
            String path = packageName.replace(".", "/") + "/" + packageName.replaceAll(".*\\.", "") + ".properties";
            InputStream is = getClass().getClassLoader().getResourceAsStream(path);
            if (is != null) {
                try {
                    props.load(is);
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
        return props;
    }
}
