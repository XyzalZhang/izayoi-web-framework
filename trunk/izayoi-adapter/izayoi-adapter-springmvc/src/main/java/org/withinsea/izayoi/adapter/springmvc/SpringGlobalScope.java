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
import org.withinsea.izayoi.core.scope.DefaultGlobalScope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-4-23
 * Time: 16:26:37
 */
public class SpringGlobalScope extends DefaultGlobalScope {

    protected ApplicationContext applicationContext;

    @Override
    protected Object getConstant(String name) {
        return name.equals("springApplicationContext") ? applicationContext
                : super.getConstant(name);
    }

    @Override
    protected Object getAttribute(String name) {
        Object obj = lookupSpring(name);
        if (obj == null) obj = super.getAttribute(name);
        return obj;
    }

    protected Object lookupSpring(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (Exception e) {
            return null;
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}