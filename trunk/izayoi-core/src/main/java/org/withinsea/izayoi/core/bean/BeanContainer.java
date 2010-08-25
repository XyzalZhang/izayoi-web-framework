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

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-16
 * Time: 0:08:53
 */
public interface BeanContainer extends BeanSource, BeanFactory {

    public void add(Class<?> claz);

    public void add(Object bean);

    public void add(String name, String value);

    public void add(String name, Class<?> claz);

    public void add(String name, Object bean);

    public void set(String name, String value);

    public void set(String name, Class<?> claz);

    public void set(String name, Object bean);

    public void set(Class<?> claz, Object bean);

    public void remove(String name);

    public void remove(Object bean);

    public void remove(Class<?> claz);
}
