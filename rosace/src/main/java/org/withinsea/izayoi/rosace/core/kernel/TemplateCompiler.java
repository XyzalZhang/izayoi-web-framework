package org.withinsea.izayoi.rosace.core.kernel;

import javassist.*;
import org.withinsea.izayoi.common.util.Vars;
import org.withinsea.izayoi.common.util.Varstack;
import org.withinsea.izayoi.rosace.core.exception.RosaceException;
import org.withinsea.izayoi.rosace.core.exception.RosaceRuntimeException;

import java.io.PrintWriter;
import java.io.Writer;
import java.security.ProtectionDomain;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-5
 * Time: 下午3:18
 */
public class TemplateCompiler {

    protected TemplateEngineManager templateEngineManager;

    protected final ProtectionDomain domain;
    protected final ClassLoader loader;
    protected final ClassPool pool;
    protected final Map<String, ClassLoader> renderLoaders = new HashMap<String, ClassLoader>();

    public TemplateCompiler() {
        ClassLoader currentLoader = Thread.currentThread().getContextClassLoader();
        this.domain = new ProtectionDomain(null, null);
        this.pool = new ClassPool(ClassPool.getDefault());
        pool.appendClassPath(new LoaderClassPath(currentLoader));
        this.loader = new ClassLoader(currentLoader) {
        };
    }

    public Renderer compile(String classname, String templateType, String template) throws RosaceException {
        if (templateType == null) {
            throw new RosaceRuntimeException("unspecified template type.");
        }
        if (templateEngineManager == null) {
            throw new RosaceRuntimeException("missing TemplateEngineManager.");
        }

        TemplateEngine templateEngine = templateEngineManager.lookupTemplateEngine(templateType);
        if (templateEngine == null) {
            throw new RosaceRuntimeException("missing TemplateEngine for type " + templateType + ".");
        }

        String renderCode = templateEngine.precompileTemplate(template);

        try {
            Class<? extends Renderer> claz = getRendererClass(classname, renderCode);
            return claz.newInstance();
        } catch (InstantiationException e) {
            throw new RosaceException("failed in compiling renderer.", e);
        } catch (IllegalAccessException e) {
            throw new RosaceException("failed in compiling renderer.", e);
        } catch (NotFoundException e) {
            throw new RosaceException("failed in compiling renderer.", e);
        } catch (CannotCompileException e) {
            throw new RosaceException("failed in compiling renderer.", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected Class<? extends Renderer> getRendererClass(String classname, String renderCode) throws NotFoundException, CannotCompileException {

        CtClass claz;
        CtMethod renderTo = null;
        String methodCode = CompiledTemplate.RENDER_TO_METHOD_TEMPLATE.replace("#RENDER_CODE#", renderCode);
        try {
            claz = pool.getCtClass(classname);
            renderTo = claz.getDeclaredMethod("renderTo");
        } catch (NotFoundException e) {
            claz = pool.makeClass(classname, pool.getCtClass(TemplateCompiler.class.getCanonicalName() + "$CompiledTemplate"));
        }

        synchronized (loader) {

            claz.defrost();
            if (renderTo != null) {
                claz.removeMethod(renderTo);
            }
            renderTo = CtNewMethod.make(methodCode, claz);
            for (CtClass pt : renderTo.getParameterTypes()) {
                pt.setModifiers(Modifier.FINAL);
            }
            claz.addMethod(renderTo);

            ClassLoader renderLoader = new ClassLoader(loader) {
            };
            renderLoaders.put(classname, renderLoader);
            return claz.toClass(renderLoader, domain);
        }
    }

    public TemplateEngineManager getTemplateEngineManager() {
        return templateEngineManager;
    }

    public void setTemplateEngineManager(TemplateEngineManager templateEngineManager) {
        this.templateEngineManager = templateEngineManager;
    }

    public static abstract class CompiledTemplate implements Renderer {

        public static final String RENDER_TO_METHOD_TEMPLATE = "" +
                "public void renderTo(" + PrintWriter.class.getCanonicalName() + " " + RosaceConstants.VARIABLE_WRITER + "," +
                "                        " + Varstack.class.getCanonicalName() + " " + RosaceConstants.VARIABLE_VARSTACK + ")" +
                "       throws Exception {" +
                "   #RENDER_CODE#" +
                "}";

        abstract public void renderTo(PrintWriter writer, Varstack varstack) throws Exception;

        @Override
        public void render(Writer writer, Map<String, Object> context) throws RosaceException {
            render(writer, context, new IncludeSupport() {
                @Override
                public void doInclude(Writer writer, String path, Map<String, Object> context) throws RosaceException {
                    if (path != null) {
                        throw new RosaceRuntimeException("missing IncludeSupport for multi-templates rendering.");
                    } else if (context.get(RosaceConstants.ATTR_INCLUDE_SECTION) == null) {
                        throw new RosaceRuntimeException("infinite recursive including.");
                    } else {
                        render(writer, context, this, null);
                    }
                }
            }, null);
        }

        @Override
        public void render(Writer writer, Map<String, Object> context, IncludeSupport includeSupport, String path) throws RosaceException {

            Map<String, Object> includerContext = null;
            {
                Deque<IncludeSupport.Tracer.Including> includingStack = IncludeSupport.Tracer.getIncludingStack();
                if (!includingStack.isEmpty()) {
                    includerContext = includingStack.peek().getContext();
                }
            }

            Varstack varstack = new Varstack(new Vars(
                    RosaceConstants.ATTR_INCLUDE_SUPPORT, includeSupport));
            {
                varstack.push(context);
                if (includerContext != null) varstack.push(includerContext);
                varstack.push();
            }

            IncludeSupport.Tracer.setPath(path);
            try {
                renderTo(new PrintWriter(writer), varstack);
            } catch (RosaceException e) {
                throw e;
            } catch (Exception e) {
                throw new RosaceException(e);
            }
        }
    }
}
