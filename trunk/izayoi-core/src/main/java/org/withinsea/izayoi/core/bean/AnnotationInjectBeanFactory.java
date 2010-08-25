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

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-19
 * Time: 12:05:11
 */
public class AnnotationInjectBeanFactory extends BasicBeanFactory {

    protected BeanSource beanSource;

    public AnnotationInjectBeanFactory(BeanSource beanSource) {
        this.beanSource = beanSource;
    }

    @Override
    public <T> T create(Class<T> claz, Object... args) throws InstantiationException {
//        System.out.println("[AnnotationInjectBeanFactory] creating bean " + claz.getCanonicalName());
        T inst = super.create(claz, args);
        try {
            inject(claz, inst, beanSource);
        } catch (IllegalAccessException e) {
            throw new InstantiationException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new InstantiationException("Unable to instant class " + claz.getCanonicalName() + ". " + e.getMessage());
        }
        return inst;
    }

    protected void inject(Class<?> claz, Object o, BeanSource bs) throws InvocationTargetException, IllegalAccessException, InstantiationException {

        List<InjectPoint> ips = new ArrayList<InjectPoint>();

        List<Field> fields = new ArrayList<Field>();
        for (Class<?> c = claz; c != Object.class; c = c.getSuperclass()) {
            fields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        for (Field f : fields) {
            Resource r = f.getAnnotation(Resource.class);
            Inject i = f.getAnnotation(Inject.class);
            Named n = f.getAnnotation(Named.class);
            if (r != null) {
                ips.add(new InjectPoint(f, r.name()));
            } else if (i != null) {
                ips.add(new InjectPoint(f, (n == null) ? null : n.value()));
            }
        }

        List<Method> methods = new ArrayList<Method>();
        for (Class<?> c = claz; c != Object.class; c = c.getSuperclass()) {
            methods.addAll(Arrays.asList(c.getDeclaredMethods()));
        }
        for (Method m : methods) {
            if (m.getName().startsWith("set") && m.getName().length() > "set".length() && m.getParameterTypes().length == 1) {
                Resource r = m.getAnnotation(Resource.class);
                Inject i = m.getAnnotation(Inject.class);
                Named n = m.getAnnotation(Named.class);
                if (r != null) {
                    ips.add(new InjectPoint(m, r.name()));
                } else if (i != null) {
                    ips.add(new InjectPoint(m, (n == null) ? null : n.value()));
                }
            }
        }

        for (InjectPoint ip : ips) {
            inject(claz, o, ip, bs);
        }
    }

    protected void inject(Class<?> claz, Object o, InjectPoint ip, BeanSource bs) throws InstantiationException, InvocationTargetException, IllegalAccessException {

        Object v = null;

        String resource;
        if (ip.name != null && !ip.name.equals("")) {
            v = bs.get(ip.name);
            resource = "'" + ip.name + "'";
        } else {
            if (bs.exist(ip.guessName)) {
                v = bs.get(ip.guessName);
                resource = "'" + ip.guessName + "'";
            } else if (bs.exist(ip.type)) {
                v = bs.get(ip.type);
                resource = ip.type.getName();
            } else {
                resource = "'" + ip.guessName + "'";
            }
        }

        String target = (ip.f != null)
                ? "Field '" + ip.f.getName() + "'"
                : "Method '" + ip.m.getName() + "'";

        if (v == null) {
            throw new InstantiationException("Unable to instant class " + claz.getCanonicalName() +
                    ", resource " + resource + " for " + target + " required.");
        }

        if (ip.f != null) {
            ip.f.set(o, v);
        } else {
            ip.m.invoke(o, v);
        }
    }

    protected static class InjectPoint {

        Field f;
        Method m;
        String name;
        Class<?> type;
        String guessName;

        public InjectPoint(Field f, String name) {
            this.f = f;
            this.type = f.getType();
            this.name = name;
            this.guessName = f.getName();
            this.f.setAccessible(true);
        }

        public InjectPoint(Method m, String name) {
            this.m = m;
            this.type = m.getParameterTypes()[0];
            this.name = name;
            this.guessName = m.getName().replaceFirst("set", "");
            {
                int prelen = 0;
                while (prelen < this.guessName.length() && this.guessName.charAt(prelen) >= 'A' && this.guessName.charAt(prelen) <= 'Z') {
                    prelen++;
                }
                this.guessName = this.guessName.substring(0, prelen).toLowerCase() + this.guessName.substring(prelen);
            }
            this.m.setAccessible(true);
        }
    }

    public BeanSource getBeanSource() {
        return beanSource;
    }

    public void setBeanSource(BeanSource beanSource) {
        this.beanSource = beanSource;
    }
}
