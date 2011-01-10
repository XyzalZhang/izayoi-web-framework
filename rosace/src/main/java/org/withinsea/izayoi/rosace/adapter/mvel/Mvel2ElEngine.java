package org.withinsea.izayoi.rosace.adapter.mvel;

import javassist.*;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.exception.RosaceRuntimeException;
import org.withinsea.izayoi.rosace.core.impl.template.HostlangUtils;
import org.withinsea.izayoi.rosace.core.kernel.ElEngine;
import org.withinsea.izayoi.rosace.core.kernel.PrecompiletimeContext;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConstants;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-7
 * Time: 下午6:15
 */
public class Mvel2ElEngine implements ElEngine {

    @Override
    public String getName() {
        return "MVEL2";
    }

    @Override
    public String precompileEl(String el) throws RosaceException {
        Set<String> imports = PrecompiletimeContext.get().getScopeAttribute(RosaceConstants.ATTR_IMPORTS, Collections.<String>emptySet());
        String importsEl = "";
        for (String claz : imports) {
            importsEl += "import " + claz + ";";
        }
        el = HostlangUtils.jspString(importsEl + el);
        return Tricker.class.getCanonicalName() + ".eval(\"" + el + "\", " + RosaceConstants.VARIABLE_VARSTACK + ")";
    }

    public static class Tricker {

        public static Object eval(String el, Map<String, Object> context) {
            try {
                Class.forName("org.mvel2.MVEL");
                return EVAL.invoke(null, el, context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        static Method EVAL;

        static {
            try {
                Class.forName("java.lang.IllegalAccessException");
                Class.forName("java.lang.reflect.InvocationTargetException");
                Class.forName("org.mvel2.PropertyAccessor");
                try {
                    ClassPool pool = new ClassPool(ClassPool.getDefault());
                    {
                        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
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
                    throw new RosaceRuntimeException("failed in create MVEL2 evaluator.", e);
                }
            } catch (ClassNotFoundException e) {
                // MVEL runtime doesn't exist. ignore.
            }
        }
    }
}
