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

import org.picocontainer.MutablePicoContainer;
import org.withinsea.izayoi.commons.conf.IzayoiConfig;
import org.withinsea.izayoi.commons.util.ClassUtils;
import org.withinsea.izayoi.cortile.core.compiler.Compilr;
import org.withinsea.izayoi.cortile.core.compiler.Grammar;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 16:05:31
 */
public class CortileConfig extends IzayoiConfig {

    public CortileConfig(ServletContext servletContext) {
        this(servletContext, null);
    }

    public CortileConfig(ServletContext servletContext, String configPath) {
        super(servletContext, configPath);
    }

    @Override
    protected void initComponents(MutablePicoContainer container, ServletContext servletContext, Properties conf) throws Exception {

        super.initComponents(container, servletContext, conf);

        container.addComponent("target", container.getComponent("webroot"));
        container.addComponent("elIterpreter", Class.forName(conf.getProperty("class.elInterpreter")));
        container.addComponent("codeManager", Class.forName(conf.getProperty("class.codeManager")));

        Map<String, Set<Grammar>> grammars = new HashMap<String, Set<Grammar>>();
        {
            for (String propname : conf.stringPropertyNames()) {
                if (propname.startsWith("class.grammars")) {
                    String namespace = propname.substring("class.grammars".length()).replaceAll("^\\.", "");
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
        container.addComponent("grammars", grammars);

        Map<String, Compilr> compilers = new HashMap<String, Compilr>();
        {
            for (String propname : conf.stringPropertyNames()) {
                if (propname.startsWith("class.compiler")) {
                    String type = propname.substring("class.compiler".length()).replaceAll("^\\.", "");
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
        container.addComponent("compilers", compilers);

        container.addComponent("compileManager", Class.forName(conf.getProperty("class.compileManager")));
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
