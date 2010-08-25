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

package org.withinsea.izayoi.cortile.core.compile.el;

import org.withinsea.izayoi.core.code.TempCode;
import org.withinsea.izayoi.core.conf.IzayoiContainer;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;
import org.withinsea.izayoi.core.interpret.BindingsUtils;
import org.withinsea.izayoi.core.interpret.InterpretManager;
import org.withinsea.izayoi.core.interpret.Varstack;
import org.withinsea.izayoi.core.scope.Request;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-5
 * Time: 2:32:23
 */
public class ELHelper {

    protected static final String HELPER_ATTR = ELHelper.class.getCanonicalName() + ".HELPER";

    public synchronized static ELHelper get(String retrievalKey, HttpServletRequest request, HttpServletResponse response) {
        ELHelper helper = (ELHelper) request.getAttribute(HELPER_ATTR);
        if (helper == null) {
            IzayoiContainer container = IzayoiContainer.retrieval(request.getSession().getServletContext(), retrievalKey);
            try {
                helper = container.create(ELHelper.class);
                helper.init(request, response);
            } catch (InstantiationException e) {
                throw new IzayoiRuntimeException(e);
            }
            request.setAttribute(HELPER_ATTR, helper);
        }
        return helper;
    }

    @Resource
    IzayoiContainer izayoiContainer;

    @Resource
    InterpretManager interpretManager;

    @Resource
    String elType;

    protected Varstack varstack;

    @SuppressWarnings("unchecked")
    protected void init(HttpServletRequest request, HttpServletResponse response) {
        this.varstack = new Varstack(
                BindingsUtils.asBindings(izayoiContainer),
                BindingsUtils.asBindings(new Request(request, response))
        );
    }

    public Object eval(String el, boolean forOutput, String elType, String... importedClasses) {
        if (elType == null) elType = this.elType;
        Object ret;
        try {
            ret = interpretManager.interpret(new TempCode(el, elType), varstack, importedClasses);
        } catch (Exception e) {
            ret = null; // silent exception stack trace
        }
        return (!forOutput) ? ret : (ret == null) ? "" : ret;
    }

    public void bind(String key, Object value) {
        varstack.put(key, value);
    }

    public void openScope() {
        openScope(null);
    }

    public void openScope(Map<String, Object> bindings) {
        if (bindings == null) {
            varstack.push();
        } else {
            varstack.push(bindings);
        }
    }

    public void closeScope() {
        varstack.pop();
    }
}