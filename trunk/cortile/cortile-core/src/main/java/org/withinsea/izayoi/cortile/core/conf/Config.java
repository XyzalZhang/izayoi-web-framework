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

package org.withinsea.izayoi.cortile.core.conf;

import org.picocontainer.*;
import org.picocontainer.injectors.MultiInjection;
import org.withinsea.izayoi.cortile.core.compile.CompileManager;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.Grammar;
import org.withinsea.izayoi.cortile.core.exception.CortileException;
import org.withinsea.izayoi.cortile.util.ClassUtils;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2009-12-27
 * Time: 22:10:22
 */
public class Config {

    public static Properties getConfig(ServletContext servletContext, String configPath) throws CortileException {
        return getHolder(servletContext, configPath).getConf();
    }

    public static CompileManager getCompileManager(ServletContext servletContext, String configPath) throws CortileException {
        return getHolder(servletContext, configPath).getManager();
    }

    protected static final String HOLDER_ATTR_NAME = Config.class.getCanonicalName() + ".HOLDER";

    protected static class Holder {

        private final Properties conf;

        private final CompileManager manager;

        public Holder(Properties conf, CompileManager manager) {
            this.conf = conf;
            this.manager = manager;
        }

        public Properties getConf() {
            return conf;
        }

        public CompileManager getManager() {
            return manager;
        }
    }

    protected static final String DEFAULT_CONF_PATH = "/WEB-INF/cortile.properties";

    protected static synchronized Holder getHolder(ServletContext servletContext, String configPath) throws CortileException {

        configPath = (configPath == null ? DEFAULT_CONF_PATH : configPath);

        @SuppressWarnings("unchecked")
        Map<String, Holder> holders = (Map<String, Holder>) servletContext.getAttribute(HOLDER_ATTR_NAME);
        if (holders == null) {
            holders = new HashMap<String, Holder>();
            servletContext.setAttribute(HOLDER_ATTR_NAME, holders);
        }

        Holder holder = holders.get(configPath);
        if (holder != null) {
            return holder;
        }

        Properties conf = new Properties();
        {
            for (InputStream is : new InputStream[]{
                    Config.class.getResourceAsStream("cortile.properties"),
                    servletContext.getResourceAsStream(configPath)
            }) {
                if (is != null) {
                    try {
                        conf.load(is);
                    } catch (IOException e) {
                        throw new CortileException();
                    }
                }
            }
        }

        try {
            holder = new Holder(conf, (CompileManager)
                    createContainer(servletContext, conf).getComponent("cortile.compileManager"));
            holders.put(configPath, holder);
            return holder;
        } catch (Exception e) {
            throw new CortileException(e);
        }
    }

    protected static PicoContainer createContainer(ServletContext servletContext, Properties conf) throws ClassNotFoundException, IOException {

        MutablePicoContainer container = new CortileContainer(new MultiInjection())
                .change(Characteristics.USE_NAMES);

        for (String propname : conf.stringPropertyNames()) {
            container.addComponent(propname, conf.getProperty(propname));
        }

        container.addComponent("cortile.servletContext", servletContext);
        container.addComponent("cortile.conf", conf);

        container.addComponent("cortile.webroot", new File(servletContext.getRealPath("/").replace("%20", " ")));
        container.addComponent("cortile.target", new File(servletContext.getRealPath("/").replace("%20", " ")));

        container.addComponent("cortile.elIterpreter", Class.forName(conf.getProperty("cortile.class.elInterpreter")));
        container.addComponent("cortile.codeManager", Class.forName(conf.getProperty("cortile.class.codeManager")));

        Map<String, Set<Grammar>> grammars = new HashMap<String, Set<Grammar>>();
        {
            for (String propname : conf.stringPropertyNames()) {
                if (propname.startsWith("cortile.class.grammars")) {
                    String namespace = propname.substring("cortile.class.grammars".length()).replaceAll("^\\.", "");
                    Set<Grammar> grammarGroup = new LinkedHashSet<Grammar>();
                    {
                        for (String className : trimClassNames(conf.getProperty(propname))) {
                            Class<?> claz = Class.forName(className);
                            if (ClassUtils.isExtendsFrom(claz, Grammar.class)) {
                                if (container.getComponentAdapter(claz) == null) {
                                    container.addComponent(claz);
                                }
                                grammarGroup.add((Grammar) container.getComponent(claz));
                            }
                        }
                    }
                    grammars.put(namespace, grammarGroup);
                }
            }
        }
        container.addComponent("cortile.grammars", grammars);

        Map<String, Compilr> compilers = new HashMap<String, Compilr>();
        {
            for (String propname : conf.stringPropertyNames()) {
                if (propname.startsWith("cortile.class.compiler")) {
                    String type = propname.substring("cortile.class.compiler".length()).replaceAll("^\\.", "");
                    Class<?> claz = Class.forName(conf.getProperty(propname));
                    if (ClassUtils.isExtendsFrom(claz, Compilr.class)) {
                        if (container.getComponentAdapter(claz) == null) {
                            container.addComponent(claz);
                        }
                        compilers.put(type, (Compilr) container.getComponent(claz));
                    }
                }
            }
        }
        container.addComponent("cortile.compilers", compilers);

        container.addComponent("cortile.compileManager", Class.forName(conf.getProperty("cortile.class.compileManager")));

        return container;
    }

    protected static class CortileContainer extends DefaultPicoContainer {

        public CortileContainer(final ComponentFactory componentFactory) {
            super(componentFactory, null);
        }

        @Override
        public Object getComponent(Object componentKey) {
            return super.getComponent(toInjectName(componentKey.toString()));
        }

        @Override
        public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) {
            return super.addComponent(toInjectName(componentKey.toString()), componentImplementationOrInstance, parameters);
        }

        protected static String toInjectName(String propname) {
            if (propname.startsWith("cortile.")) {
                propname = propname.substring("cortile.".length());
            }
            StringBuffer buf = new StringBuffer();
            String[] names = propname.trim().split("\\.");
            for (int i = names.length - 1; i >= 0; i--) {
                buf.append(names[i].substring(0, 1).toUpperCase()).append(names[i].substring(1));
            }
            String injectName = buf.toString();
            return injectName.substring(0, 1).toLowerCase() + injectName.substring(1);
        }
    }

    protected static Collection<String> trimClassNames(String classNames) throws IOException {
        Set<String> trimClassNames = new HashSet<String>();
        for (String classes : classNames.split("\\s*(;|,)\\s*")) {
            if (classes.endsWith(".*")) {
                String packageName = classes.substring(0, classes.length() - 2);
                trimClassNames.addAll(ClassUtils.getPackageClassNames(packageName));
            } else {
                trimClassNames.add(classes);
            }
        }
        return trimClassNames;
    }
}
