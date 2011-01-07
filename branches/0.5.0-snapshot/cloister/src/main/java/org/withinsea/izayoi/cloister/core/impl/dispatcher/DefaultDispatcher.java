package org.withinsea.izayoi.cloister.core.impl.dispatcher;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.feature.dispatcher.Dispatcher;
import org.withinsea.izayoi.cloister.core.feature.dispatcher.Dispatching;
import org.withinsea.izayoi.cloister.core.feature.dynapage.DynapageManager;
import org.withinsea.izayoi.cloister.core.feature.dynapage.DynapageRenderer;
import org.withinsea.izayoi.cloister.core.feature.pathvar.PathvarMatch;
import org.withinsea.izayoi.cloister.core.feature.pathvar.PathvarMatcher;
import org.withinsea.izayoi.cloister.core.feature.postscript.PostscriptEncloser;
import org.withinsea.izayoi.cloister.core.feature.postscript.PostscriptManager;
import org.withinsea.izayoi.cloister.core.feature.postscript.PostscriptPath;
import org.withinsea.izayoi.cloister.core.impl.request.RequestWrapper;
import org.withinsea.izayoi.cloister.core.kernal.*;
import org.withinsea.izayoi.common.util.Varstack;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-26
 * Time: 下午5:27
 */
public abstract class DefaultDispatcher extends Dispatcher {

    abstract protected boolean isDirectAccess(Request request) throws CloisterException;

    protected PathvarMatcher pathvarMatcher;
    protected PostscriptManager postscriptManager;
    protected PostscriptEncloser postscriptEncloser;
    protected DynapageManager dynapageManager;
    protected DynapageRenderer dynapageRenderer;
    protected String encoding = CloisterConfig.getDefault().getProperty("cloister.encoding");

    @Override
    protected String getOutputEncoding(Request request) {
        return encoding;
    }

    @Override
    public void respond(Request request) throws CloisterException {
        if (postscriptManager.isPostscript(request.getEnvironment(), request.getPath())) {
            if (isDirectAccess(request)) {
                throw new HiddenRequest(request.getPath());
            }
        } else {
            super.respond(request);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Target map(Request request) throws CloisterException {
        PathvarMatch match = pathvarMatcher.match(request.getEnvironment(), request.getPath());
        if (match == null) return null;
        if (match.getVars().isEmpty() && match.getCodefile().getPath().equals(request.getPath())) return null;
        return new Target(match.getCodefile().getPath(), (Map) match.getVars());
    }

    protected void redispatch(Request request, final Target target) throws CloisterException {

        final Varstack parameters = new Varstack();
        parameters.push(request.getParameters());
        parameters.push(target.getParameters());

        respond(new RequestWrapper(request) {

            @Override
            public String getPath() {
                return target.getPath();
            }

            @Override
            public Map<String, Object> getParameters() {
                return parameters;
            }
        });
    }

    @Override
    protected List<Responder> lookupResponders(Request request) throws CloisterException {

        List<Responder> responders = new ArrayList<Responder>();

        if (postscriptManager != null) {
            for (Environment.Codefile postscript : postscriptManager.lookupPostscripts(
                    request.getEnvironment(), request.getPath(), request.getScope())) {
                responders.add(new PostscriptResponder(postscript));
            }
        }

        if (dynapageManager != null) {
            Environment.Codefile dynapage = dynapageManager.lookupDynapage(
                    request.getEnvironment(), request.getPath());
            if (dynapage != null) {
                responders.add(new DynapageResponder(dynapage));
            }
        }

        return responders;
    }

    protected class PostscriptResponder implements Responder {

        protected Environment.Codefile postscript;

        public PostscriptResponder(Environment.Codefile postscript) {
            this.postscript = postscript;
        }

        @Override
        public void respond(Request request) throws CloisterException {

            TimestampCache<Void> enclosedCache = TimestampCache.getCache(
                    request.getScope().getScopeAttributes(), CloisterConstants.ATTR_PROCESSED_RESPONDER_CACHE);
            String cacheKey = new PostscriptPath(postscript.getPath()).getPath();

            if (enclosedCache.isModified(cacheKey, postscript.getLastModified())) {

                Varstack context = new Varstack();
                context.push(request.getParameters());
                context.push(request.getScope().getAttributes());

                String entrance = request.getMethod();
                postscriptEncloser.enclose(postscript, entrance, context);

                enclosedCache.put(cacheKey, null, postscript.getLastModified());
            }
        }
    }

    protected class DynapageResponder implements Responder {

        protected Environment.Codefile dynapage;

        public DynapageResponder(Environment.Codefile dynapage) {
            this.dynapage = dynapage;
        }

        @Override
        public void respond(Request request) throws CloisterException {

            Varstack context = new Varstack();
            context.push(request.getParameters());
            context.push(request.getScope().getAttributes());

            try {
                request.setOutputEncoding(encoding);
                request.setOutputContentType("text/html");
                OutputStreamWriter writer = new OutputStreamWriter(request.getOutputStream(), encoding);
                dynapageRenderer.render(writer, dynapage, context);
                try {
                    writer.flush();
                } catch (IOException e) {
                    // client aborted, ignore
                }
                throw Dispatching.finish();
            } catch (UnsupportedEncodingException e) {
                throw new CloisterException(e);
            } catch (IOException e) {
                throw new CloisterException(e);
            }
        }
    }

    public DynapageManager getDynapageManager() {
        return dynapageManager;
    }

    public void setDynapageManager(DynapageManager dynapageManager) {
        this.dynapageManager = dynapageManager;
    }

    public DynapageRenderer getDynapageRenderer() {
        return dynapageRenderer;
    }

    public void setDynapageRenderer(DynapageRenderer dynapageRenderer) {
        this.dynapageRenderer = dynapageRenderer;
    }

    public PostscriptEncloser getPostscriptEncloser() {
        return postscriptEncloser;
    }

    public void setPostscriptEncloser(PostscriptEncloser postscriptEncloser) {
        this.postscriptEncloser = postscriptEncloser;
    }

    public PostscriptManager getPostscriptManager() {
        return postscriptManager;
    }

    public void setPostscriptManager(PostscriptManager postscriptManager) {
        this.postscriptManager = postscriptManager;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public PathvarMatcher getPathvarMatcher() {
        return pathvarMatcher;
    }

    public void setPathvarMatcher(PathvarMatcher pathvarMatcher) {
        this.pathvarMatcher = pathvarMatcher;
    }
}
