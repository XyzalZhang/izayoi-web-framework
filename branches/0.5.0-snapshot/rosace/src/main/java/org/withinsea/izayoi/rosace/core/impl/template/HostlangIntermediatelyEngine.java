package org.withinsea.izayoi.rosace.core.impl.template;

import org.withinsea.izayoi.common.util.StringUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.exception.RosaceRuntimeException;
import org.withinsea.izayoi.rosace.core.kernel.*;

import java.util.HashSet;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-5
 * Time: 下午5:07
 */
public abstract class HostlangIntermediatelyEngine implements TemplateEngine {

    abstract protected String precompileTemplateToHostlang(String template) throws RosaceException;

    protected ElEngineManager elEngineManager;
    protected String defaultElType = RosaceConfig.getDefault().getProperty("elType");

    @Override
    public String precompileTemplate(String template) throws RosaceException {

        String code;

        PrecompiletimeContext ctx = PrecompiletimeContext.open(this, elEngineManager);
        {
            ctx.setScopeAttribute(RosaceConstants.ATTR_LOCKED, false);
            ctx.setScopeAttribute(RosaceConstants.ATTR_ELTYPE, defaultElType);
            ctx.setScopeAttribute(RosaceConstants.ATTR_IMPORTS, new HashSet<String>());
            code = precompileTemplateToHostlang(template);
        }
        PrecompiletimeContext.close();

        code = code.replaceAll("<%!([\\s\\S]*?)%>", "");
        code = code.replaceAll("<%=([\\s\\S]*?)%>", "<%" + precompileOutput("$1") + "%>");
        return StringUtils.replaceAll(code, "<%([\\s\\S]*?)%>", new StringUtils.Replace() {
            @Override
            public String replace(String... groups) {
                return groups[1];
            }
        }, new StringUtils.Transform() {
            @Override
            public String transform(String str) {
                return precompileOutput("\"" + HostlangUtils.javaString(str) + "\"") + "\n";
            }
        });
    }

    public String precompileEl(String el) {
        return precompileEl(el, false);
    }

    public String precompileEl(String el, boolean forOutput) {
        ElEngine elEngine = PrecompiletimeContext.get().getElEngine();
        String elCode;
        try {
            elCode = elEngine.precompileEl(el);
        } catch (RosaceException e) {
            throw new RosaceRuntimeException(e.getMessage(), e.getCause());
        }
        return (!forOutput) ? elCode : HostlangUtils.class.getCanonicalName() + ".checkNull(" + elCode + ", \"\")";
    }

    public String precompileOpenScope() {
        return precompileOpenScope("");
    }

    public String precompileOpenScope(String bindingsCode) {
        return RosaceConstants.VARIABLE_VARSTACK + ".push(" + bindingsCode + ");";
    }

    public String precompileCloseScope() {
        return RosaceConstants.VARIABLE_VARSTACK + ".pop();";
    }

    public String precompilePut(String key, String valueCode) {
        key = HostlangUtils.jspString(key);
        return RosaceConstants.VARIABLE_VARSTACK + ".put(\"" + key + "\", " + valueCode + ");";
    }

    public String precompileGet(String key) {
        key = HostlangUtils.jspString(key);
        return RosaceConstants.VARIABLE_VARSTACK + ".get(\"" + key + "\")";
    }

    public String precompileGet(String key, Class<?> cast) {
        return "((" + cast.getCanonicalName() + ")" + precompileGet(key) + ")";
    }

    public String precompileOutput(String outputCode) {
        return RosaceConstants.VARIABLE_WRITER + ".print(" + outputCode + ");";
    }

    public ElEngineManager getElEngineManager() {
        return elEngineManager;
    }

    public void setElEngineManager(ElEngineManager elEngineManager) {
        this.elEngineManager = elEngineManager;
    }

    public String getDefaultElType() {
        return defaultElType;
    }

    public void setDefaultElType(String defaultElType) {
        this.defaultElType = defaultElType;
    }

//    protected static String SILENCER_CODE_TEMPLATE = "(" +
//            "new " + Silencer.class.getCanonicalName() + "() {" +
//            "  public Object output() {" +
//            "    try { Object o = #CODE#; return (o == null) ? \"\" : o; }" +
//            "    catch (Exception e) { return \"\"; }" +
//            "  }" +
//            "}).output()";
//
//    public static interface Silencer {
//        String output();
//    }
}
