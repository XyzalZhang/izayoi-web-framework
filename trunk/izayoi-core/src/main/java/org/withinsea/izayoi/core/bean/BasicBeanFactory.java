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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-21
 * Time: 10:34:23
 */
public class BasicBeanFactory implements BeanFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> claz, Object... args) throws InstantiationException {
        try {
            List<Constructor> constructors = new ArrayList<Constructor>();
            for (Constructor constructor : claz.getDeclaredConstructors()) {
                Class<?>[] ptypes = constructor.getParameterTypes();
                if (ptypes.length == args.length) {
                    boolean match = true;
                    for (int i = 0; i < ptypes.length; i++) {
                        if ((args[i] == null && ptypes[i].isPrimitive())
                                || !ptypes[i].isAssignableFrom(args[i].getClass())) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        constructors.add(constructor);
                    }
                }
            }
            if (constructors.isEmpty()) {
                throw new InstantiationException("Unable to instant class " + claz.getCanonicalName() +
                        ", no suitable constructor.");
            }
            Constructor constructor = constructors.get(0);
            if (constructors.size() > 1) {
                for (int ic = 1; ic < constructors.size(); ic++) {
                    Constructor c = constructors.get(ic);
                    for (int i = 0; i < args.length; i++) {
                        if (constructor.getParameterTypes()[i].isAssignableFrom(c.getParameterTypes()[i])) {
                            constructor = c;
                            break;
                        }
                    }
                }
            }
            constructor.setAccessible(true);
            return (T) constructor.newInstance(args);
        } catch (IllegalAccessException e) {
            throw new InstantiationException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new InstantiationException("Unable to instant class " + claz.getCanonicalName() + ". " + e.getMessage());
        }
    }
}
