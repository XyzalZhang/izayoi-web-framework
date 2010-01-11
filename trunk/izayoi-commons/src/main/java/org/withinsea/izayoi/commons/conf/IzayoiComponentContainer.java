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

package org.withinsea.izayoi.commons.conf;

import org.picocontainer.*;
import org.picocontainer.behaviors.Behaviors;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-1-9
 * Time: 15:13:00
 */
class IzayoiComponentContainer extends DefaultPicoContainer {

    public IzayoiComponentContainer() {
        super(Behaviors.caching());
        change(Characteristics.SDI, Characteristics.USE_NAMES);
    }

    @Override
    public Object getComponent(Object componentKey) {
        return (componentKey instanceof String)
                ? getComponent((String) componentKey)
                : super.getComponent(componentKey);
    }

    @Override
    public MutablePicoContainer addComponent(Object componentKey, Object componentImplementationOrInstance, Parameter... parameters) {
        return (componentKey instanceof String)
                ? addComponent((String) componentKey, componentImplementationOrInstance, parameters)
                : super.addComponent(componentKey, componentImplementationOrInstance, parameters);
    }

    public Object getComponent(String componentKey) {
        return super.getComponent(toInjectName(componentKey));
    }

    public MutablePicoContainer addComponent(String componentKey, Object componentImplementationOrInstance, Parameter... parameters) {
        try {
            super.addComponent(toInjectName(componentKey), componentImplementationOrInstance, parameters);
        } catch (PicoCompositionException e) {
            if (!e.getMessage().startsWith("Unprocessed Characteristics")) {
                throw e;
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    protected static final String toInjectName(String componentKey) {
        StringBuffer buf = new StringBuffer();
        String[] names = componentKey.trim().split("\\.");
        for (int i = names.length - 1; i >= 0; i--) {
            buf.append(names[i].substring(0, 1).toUpperCase()).append(names[i].substring(1));
        }
        String injectName = buf.toString();
        return injectName.substring(0, 1).toLowerCase() + injectName.substring(1);
    }
}