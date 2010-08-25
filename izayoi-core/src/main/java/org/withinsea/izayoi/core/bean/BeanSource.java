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

import java.util.List;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-8-16
 * Time: 0:08:53
 */
public interface BeanSource {

    public boolean exist(String name);

    public boolean exist(Class<?> claz);

    public boolean exist(Object bean);

    public <T> T get(String name);

    public <T> T get(Class<T> claz);

    public <T> List<T> list(String name);

    public <T> List<T> list(Class<T> claz);
}