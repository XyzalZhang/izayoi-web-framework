package org.withinsea.izayoi.rosace.util;

import org.withinsea.izayoi.common.util.ClassUtils;
import org.withinsea.izayoi.rosace.adapter.mvel.Mvel2ElEngine;
import org.withinsea.izayoi.rosace.core.exception.RosaceRuntimeException;
import org.withinsea.izayoi.rosace.core.impl.el.Java;
import org.withinsea.izayoi.rosace.core.impl.template.dom.DomTemplateEngine;
import org.withinsea.izayoi.rosace.core.impl.template.dom.grammar.Grammar;
import org.withinsea.izayoi.rosace.core.impl.template.txt.EltextTemplateEngine;
import org.withinsea.izayoi.rosace.core.kernel.ElEngineManager;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConfig;
import org.withinsea.izayoi.rosace.core.kernel.TemplateCompiler;
import org.withinsea.izayoi.rosace.core.kernel.TemplateEngineManager;
import org.withinsea.izayoi.rosace.web.impl.IncludedUrlAdjust;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-7
 * Time: 下午7:04
 */
public class Rosaces {

    protected static Properties DEFAULT_CONFIG = RosaceConfig.getDefault();

    public static final ElEngineManager DEFAULT_EL_ENGINE_MANAGER = new ElEngineManager(); static {
        DEFAULT_EL_ENGINE_MANAGER.registerElEngine("mvel2", new Mvel2ElEngine());
        DEFAULT_EL_ENGINE_MANAGER.registerElEngine("java", new Java());
    }

    public static final Set<Grammar> DEFAULT_GLOBAL_GRAMMARS; static {
        String base = "org.withinsea.izayoi.rosace.core.impl.grammar.global";
        try {
            DEFAULT_GLOBAL_GRAMMARS = new LinkedHashSet<Grammar>();
            DEFAULT_GLOBAL_GRAMMARS.addAll(ClassUtils.instantiatePackageClasses(Grammar.class, base));
            DEFAULT_GLOBAL_GRAMMARS.add(new IncludedUrlAdjust());
        } catch (Exception e) {
            throw new RosaceRuntimeException(e);
        }
    }

    public static final Map<String, Set<Grammar>> DEFAULT_NAMESPACED_GRAMMARS = new HashMap<String, Set<Grammar>>(); static {
        String base = "org.withinsea.izayoi.rosace.core.impl.grammar.ns";
        try {
            for (String pname : ClassUtils.getSubPackageNames(base)) {
                String ns = pname.substring(base.length() + 1);
                Set<Grammar> grammars = ClassUtils.instantiatePackageClasses(Grammar.class, pname);
                DEFAULT_NAMESPACED_GRAMMARS.put(ns, grammars);
            }
        } catch (Exception e) {
            throw new RosaceRuntimeException(e);
        }
    }

    public static final DomTemplateEngine DEFAULT_DOM_TEMPLATE_ENGINE = new DomTemplateEngine(); static {
        DEFAULT_DOM_TEMPLATE_ENGINE.setElEngineManager(DEFAULT_EL_ENGINE_MANAGER);
        DEFAULT_DOM_TEMPLATE_ENGINE.setDefaultElType(DEFAULT_CONFIG.getProperty("rosace.elType"));
        DEFAULT_DOM_TEMPLATE_ENGINE.setGlobalGrammars(DEFAULT_GLOBAL_GRAMMARS);
        DEFAULT_DOM_TEMPLATE_ENGINE.setNamespacedGrammars(DEFAULT_NAMESPACED_GRAMMARS);
    }

    public static final EltextTemplateEngine DEFAULT_ELTEXT_TEMPLATE_ENGINE = new EltextTemplateEngine(); static {
        DEFAULT_ELTEXT_TEMPLATE_ENGINE.setElEngineManager(DEFAULT_EL_ENGINE_MANAGER);
        DEFAULT_ELTEXT_TEMPLATE_ENGINE.setDefaultElType(DEFAULT_CONFIG.getProperty("rosace.elType"));
    }

    public static final TemplateEngineManager DEFAULT_TEMPLATE_ENGINE_MANAGER = new TemplateEngineManager(); static {
        DEFAULT_TEMPLATE_ENGINE_MANAGER.registerTemplateEngine("dom", DEFAULT_DOM_TEMPLATE_ENGINE);
        DEFAULT_TEMPLATE_ENGINE_MANAGER.registerTemplateEngine("html", DEFAULT_DOM_TEMPLATE_ENGINE);
        DEFAULT_TEMPLATE_ENGINE_MANAGER.registerTemplateEngine("xml", DEFAULT_DOM_TEMPLATE_ENGINE);
        DEFAULT_TEMPLATE_ENGINE_MANAGER.registerTemplateEngine("elt", DEFAULT_ELTEXT_TEMPLATE_ENGINE);
        DEFAULT_TEMPLATE_ENGINE_MANAGER.registerTemplateEngine("txt", DEFAULT_ELTEXT_TEMPLATE_ENGINE);
    }

    public static TemplateCompiler newDefaultTemplateCompiler() {
        TemplateCompiler templateCompiler = new TemplateCompiler();
        templateCompiler.setTemplateEngineManager(DEFAULT_TEMPLATE_ENGINE_MANAGER);
        return templateCompiler;
    }
}
