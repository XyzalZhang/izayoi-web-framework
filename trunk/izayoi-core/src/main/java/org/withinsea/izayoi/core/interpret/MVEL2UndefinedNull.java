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

package org.withinsea.izayoi.core.interpret;

import javassist.*;
import org.withinsea.izayoi.core.code.Code;
import org.withinsea.izayoi.core.exception.IzayoiException;
import org.withinsea.izayoi.core.exception.IzayoiRuntimeException;

import javax.script.Bindings;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-3-4
 * Time: 13:59:24
 * <p/>
 * Any undefinded variable will be gave a null value.
 */
public class MVEL2UndefinedNull implements Interpreter {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T interpret(Code code, Bindings bindings, String... importedClasses) throws IzayoiException {
        String importsEl = "";
        for (String claz : importedClasses) {
            importsEl += "import " + claz + ";";
        }
        return (T) NullTricker.eval(importsEl + code.getCode(), bindings);
    }

    protected static class NullTricker {

        public static Object eval(String expression, Map<String, Object> imports) throws IzayoiException {
            try {
                Class.forName("org.mvel2.MVEL");
                return EVAL.invoke(null, expression, imports);
            } catch (Exception e) {
                throw new IzayoiRuntimeException(e);
            }
        }

        static Method EVAL;

        static {
            try {
                Class.forName("java.lang.IllegalAccessException");
                Class.forName("java.lang.reflect.InvocationTargetException");
                Class.forName("org.mvel2.PropertyAccessor");
                try {
                    ClassPool pool = ClassPool.getDefault();
                    {
                        pool.appendClassPath(new LoaderClassPath(MVEL2UndefinedNull.class.getClassLoader()));
                        pool.importPackage("org.mvel2");
                        CtClass cc = pool.get("org.mvel2.PropertyAccessor");
                        CtMethod m = cc.getDeclaredMethod("getBeanProperty");
                        m.setName("_getBeanProperty");
                        CtMethod nm = CtMethod.make("" +
                                "private Object getBeanProperty(Object ctx, String property) " +
                                "       throws java.lang.IllegalAccessException, java.lang.reflect.InvocationTargetException {" +
                                "   try { return _getBeanProperty(ctx, property); }" +
                                "   catch (PropertyAccessException e) { return null; }" +
                                "}", cc);
                        cc.addMethod(nm);
                    }
                    Loader cl = new Loader(pool);
                    for (Method m : cl.loadClass("org.mvel2.MVEL").getDeclaredMethods()) {
                        if (m.getName().equals("eval") && m.getParameterTypes().length == 2
                                && m.getParameterTypes()[0].equals(String.class)
                                && Map.class.isAssignableFrom(m.getParameterTypes()[1])) {
                            EVAL = m;
                            break;
                        }
                    }
                } catch (Exception e) {
                    throw new IzayoiRuntimeException(e);
                }
            } catch (ClassNotFoundException e) {
                // MVEL runtime doesn't exist. ignore.
            }
        }
    }
}