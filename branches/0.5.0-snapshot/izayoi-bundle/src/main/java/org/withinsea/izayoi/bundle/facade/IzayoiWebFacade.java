package org.withinsea.izayoi.bundle.facade;

import org.withinsea.izayoi.bundle.kernal.IzayoiWebConfig;
import org.withinsea.izayoi.cloister.adapter.rosace.RosaceDynapageEngine;
import org.withinsea.izayoi.cloister.core.feature.dynapage.DynapageEngineManager;
import org.withinsea.izayoi.cloister.web.facade.CloisterWebFacade;
import org.withinsea.izayoi.rosace.core.kernel.IncludeSupport;
import org.withinsea.izayoi.rosace.core.kernel.TemplateCompiler;
import org.withinsea.izayoi.rosace.util.Rosaces;
import org.withinsea.izayoi.rosace.web.impl.HttpIncludeSupport;

import java.util.Properties;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-29
 * Time: 下午3:54
 */
public class IzayoiWebFacade extends CloisterWebFacade {

    @Override
    protected Properties getConfig() {
        return IzayoiWebConfig.getDefault(servletContext);
    }

    @Override
    protected void registerDynapageEngines(DynapageEngineManager dynapageEngineManager) {

        String encoding = globalConfig.getProperty("rosace.encoding");

        TemplateCompiler templateCompiler = Rosaces.newDefaultTemplateCompiler();
        IncludeSupport includeSupport = new HttpIncludeSupport();

        dynapageEngineManager.registerDynapageEngine("html", new RosaceDynapageEngine(
                templateCompiler, "html", encoding, includeSupport));
        dynapageEngineManager.registerDynapageEngine("elt", new RosaceDynapageEngine(
                templateCompiler, "elt", encoding, includeSupport));
    }
}
