package org.withinsea.izayoi.cloister.adapter.rosace;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.feature.dynapage.DynapageEngine;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.common.util.IOUtils;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.kernel.IncludeSupport;
import org.withinsea.izayoi.rosace.core.kernel.Renderer;
import org.withinsea.izayoi.rosace.core.kernel.RosaceConfig;
import org.withinsea.izayoi.rosace.core.kernel.TemplateCompiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-29
 * Time: 下午2:36
 */
public class RosaceDynapageEngine implements DynapageEngine {

    protected TemplateCompiler templateCompiler;
    protected IncludeSupport includeSupport;
    protected String type;
    protected String encoding;

    public RosaceDynapageEngine(TemplateCompiler templateCompiler, String type) {
        this(templateCompiler, type, RosaceConfig.getDefault().getProperty("rosace.encoding"), null);
    }

    public RosaceDynapageEngine(TemplateCompiler templateCompiler, String type, String encoding, IncludeSupport includeSupport) {
        this.templateCompiler = templateCompiler;
        this.type = type;
        this.encoding = encoding;
        this.includeSupport = includeSupport;
    }

    @Override
    public CompiledDynapage compile(Environment.Codefile dynapage) throws CloisterException {
        try {
            Renderer renderer = templateCompiler.compile(generateClassname(dynapage), type,
                    IOUtils.toString(new InputStreamReader(dynapage.getInputStream(), encoding)));
            return new CompiledRosace(renderer, dynapage.getPath());
        } catch (RosaceException e) {
            throw new CloisterException(e);
        } catch (IOException e) {
            throw new CloisterException(e);
        }
    }

    protected String generateClassname(Environment.Codefile dynapage) {
        return "dynapage.rosace" + dynapage.getPath()
                .replaceAll("[-\\.]", "_")
                .replace("/", ".");
    }

    protected class CompiledRosace implements CompiledDynapage {

        protected Renderer renderer;
        protected String path;

        public CompiledRosace(Renderer renderer, String path) {
            this.renderer = renderer;
            this.path = path;
        }

        @Override
        public void render(Writer writer, Map<String, Object> context) throws CloisterException {
            try {
                renderer.render(writer, context, includeSupport, path);
            } catch (RosaceException e) {
                throw new CloisterException(e);
            }
        }
    }
}
