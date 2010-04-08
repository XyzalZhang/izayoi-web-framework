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

package org.withinsea.izayoi.adapter.springmvc;

import org.springframework.context.ApplicationContext;
import org.withinsea.izayoi.core.bindings.WebContextBindingsManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-3
 * Time: 1:06:51
 */
public class SpringWebContextBindingsManager extends WebContextBindingsManager {

    protected ApplicationContext applicationContext;

    @Override
    public Object getBean(HttpServletRequest request, HttpServletResponse response, String name) {
        Object obj = super.getBean(request, response, name);
        if (obj == null) obj = lookupSpring(name);
        return obj;
    }

    protected Object lookupSpring(String name) {
        try {
            return name.equals("applicationContext") ? applicationContext : applicationContext.getBean(name);
        } catch (Exception e) {
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}