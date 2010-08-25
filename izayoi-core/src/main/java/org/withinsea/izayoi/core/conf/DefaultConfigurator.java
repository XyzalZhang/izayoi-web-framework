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

import org.withinsea.izayoi.commons.util.ClassUtils;
import org.withinsea.izayoi.core.bean.BeanContainer;

import java.io.IOException;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 16:05:31
 */
public class DefaultConfigurator implements Configurator {

    protected String prefix;

    public DefaultConfigurator() {
        this("");
    }

    public DefaultConfigurator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void configurate(IzayoiContainer container, Properties props) throws ClassNotFoundException, InstantiationException {
        for (String name : props.stringPropertyNames()) {
            String exp = props.getProperty(name).trim();
            if (exp.startsWith("|")) {
                if (getBean(container, name) != null) {
                    continue;
                } else {
                    exp = exp.substring(1).trim();
                }
            }
            Object value = get(container, exp);
            if (value != null) {
                setBean(container, name, value);
            }
        }
    }

    protected Object get(BeanContainer container, String exp) throws ClassNotFoundException, InstantiationException {
        if (exp.startsWith("[") && exp.endsWith("]")) {
            String contentExp = exp.substring(1, exp.length() - 1).trim();
            if (contentExp.equals("")) {
                return new ArrayList<Object>();
            } else {
                Object value = get(container, contentExp);
                if (value == null || value instanceof List) {
                    return value;
                } else {
                    List<Object> values = new ArrayList<Object>();
                    values.add(value);
                    return values;
                }
            }
        } else if (exp.indexOf(",") >= 0) {
            List<Object> values = new ArrayList<Object>();
            for (String itemExp : exp.split("\\s*,\\s*")) {
                values.add(get(container, itemExp.trim()));
            }
            return values;
        } else if (exp.indexOf(";") >= 0) {
            List<Object> values = new ArrayList<Object>();
            for (String partExp : exp.split("\\s*;\\s*")) {
                values.addAll((Collection<?>) get(container, partExp.trim()));
            }
            return values;
        } else {
            if (exp.startsWith("\"") && exp.endsWith("\"")) {
                return exp.substring(1, exp.length() - 1).replace("\\\"", "\"");
            } else if (exp.startsWith("#")) {
                return getBean(container, exp.substring(1));
            } else if (exp.startsWith("<") && exp.endsWith(">")) {
                String className = exp.substring(1, exp.length() - 1);
                if (className.endsWith(".*")) {
                    List<Object> beans = new ArrayList<Object>();
                    String packageName = className.substring(0, className.length() - 2);
                    try {
                        for (String cname : ClassUtils.getPackageClassNames(packageName)) {
                            if (cname.indexOf("$") < 0) {
                                Class<?> c = Class.forName(cname);
                                beans.add(container.exist(c) ? container.get(c) : container.create(c));
                            }
                        }
                    } catch (IOException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                    return beans;
                } else {
                    Class<?> c = Class.forName(className);
                    return container.exist(c) ? container.get(c) : container.create(c);
                }
            } else {
                return exp;
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected Object getBean(BeanContainer container, String name) {
        if (name.indexOf(".") < 0) {
            return container.get(name);
        } else {
            String[] split = name.split("\\.", 2);
            Map<String, Object> map = (Map<String, Object>) getBean(container, split[0]);
            if ((map == null || map.get(split[1]) == null) && container.exist(name)) {
                Object value = container.get(name);
                setBean(container, name, value);
                return value;
            } else if (map == null) {
                return null;
            } else {
                return map.get(split[1]);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void setBean(BeanContainer container, String name, Object value) {
        if (name.indexOf(".") < 0) {
            container.set(prefix + name, value);
        } else {
            String[] split = name.split("\\.", 2);
            Map<String, Object> map = (Map<String, Object>) getBean(container, split[0]);
            if (map == null) {
                map = new LinkedHashMap<String, Object>();
                container.set(prefix + split[0], map);
            }
            map.put(split[1], value);
            if (value != null && !container.exist(prefix + name)) {
                container.set(prefix + name, value);
            }
        }
    }
}