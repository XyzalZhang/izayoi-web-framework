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

package org.withinsea.izayoi.cortile.core.compile;

import org.withinsea.izayoi.core.interpret.Vars;
import org.withinsea.izayoi.core.interpret.Varstack;
import org.withinsea.izayoi.cortile.core.exception.CortileRuntimeException;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-7-28
 * Time: 14:17:46
 */
public final class CompileContext {

    protected static ThreadLocal<CompileContext> HOLDER = new ThreadLocal<CompileContext>();

    public static CompileContext open(Compilr compiler, String templatePath, Compilr.Result result) {
        CompileContext ctx = new CompileContext(compiler, templatePath, result);
        HOLDER.set(ctx);
        return ctx;
    }

    public static CompileContext get() {
        return HOLDER.get();
    }

    public static CompileContext close() {
        CompileContext ctx = get();
        HOLDER.set(null);
        return ctx;
    }

    protected static final String LOCKED_ATTR = CompileContext.class.getCanonicalName() + ".LOCKED";

    protected Compilr compiler;
    protected String templatePath;
    protected Compilr.Result result = new Compilr.Result();

    protected Vars attributes = new Vars();
    protected Varstack scopeAttributes = new Varstack();

    protected CompileContext(Compilr compiler, String templatePath, Compilr.Result result) {
        this.compiler = compiler;
        this.templatePath = templatePath;
        this.result = result;
    }

    @SuppressWarnings("unchecked")
    public <T extends Compilr> T getCompiler() {
        return (T) compiler;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    @SuppressWarnings("unchecked")
    public <T extends Compilr.Result> T getResult() {
        return (T) result;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getScopeAttribute(String key) {
        return (T) scopeAttributes.get(key);
    }

    public <T> void setScopeAttribute(String key, T value) {
        if (isLocked()) {
            throw new CortileRuntimeException("Invalid attribute setting, context locked.");
        } else {
            scopeAttributes.put(key, value);
        }
    }

    public void openScope() {
        scopeAttributes.push();
    }

    public void closeScope() {
        scopeAttributes.pop();
    }

    public void lock() {
        setScopeAttribute(LOCKED_ATTR, true);
    }

    public boolean isLocked() {
        return Boolean.TRUE.equals(getScopeAttribute(LOCKED_ATTR));
    }
}
