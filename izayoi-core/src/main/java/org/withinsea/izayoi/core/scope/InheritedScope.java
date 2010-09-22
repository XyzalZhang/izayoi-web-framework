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

package org.withinsea.izayoi.core.scope;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-28
 * Time: 15:58:46
 */
public abstract class InheritedScope implements Scope {

    protected Scope inheritedScope;
    protected Scope declaredScope;

    protected InheritedScope(Scope inheritedScope, Scope declaredScope) {
        this.inheritedScope = inheritedScope;
        this.declaredScope = declaredScope;
    }

    @Override
    public Set<String> getContantNames() {
        Set<String> names = new LinkedHashSet<String>();
        names.addAll(declaredScope.getContantNames());
        names.addAll(inheritedScope.getContantNames());
        return names;
    }

    @Override
    public Set<String> getAttributeNames() {
        Set<String> names = new LinkedHashSet<String>();
        names.addAll(declaredScope.getAttributeNames());
        names.addAll(inheritedScope.getAttributeNames());
        return names;
    }

    @Override
    public boolean containsConstant(String name) {
        return declaredScope.containsConstant(name)
                || (inheritedScope != null && inheritedScope.containsConstant(name));
    }

    @Override
    public boolean containsAttribute(String name) {
        return declaredScope.containsAttribute(name)
                || (inheritedScope != null && inheritedScope.containsAttribute(name));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getConstant(String name) {
        return declaredScope.containsConstant(name) ? (T) declaredScope.getConstant(name)
                : (inheritedScope != null) ? (T) inheritedScope.getConstant(name)
                : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        return declaredScope.containsAttribute(name) ? (T) declaredScope.getAttribute(name)
                : (inheritedScope != null) ? (T) inheritedScope.getAttribute(name)
                : null;
    }

    @Override
    public void setAttribute(String name, Object value) {
        declaredScope.setAttribute(name, value);
    }

    @Override
    public Scope getInheritedScope() {
        return inheritedScope;
    }

    @Override
    public Scope getDeclaredScope() {
        return declaredScope;
    }
}