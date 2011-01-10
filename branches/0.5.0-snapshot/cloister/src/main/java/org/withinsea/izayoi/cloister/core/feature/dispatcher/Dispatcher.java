package org.withinsea.izayoi.cloister.core.feature.dispatcher;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.kernal.CloisterConstants;
import org.withinsea.izayoi.cloister.core.kernal.CodefilePath;
import org.withinsea.izayoi.cloister.core.kernal.Request;
import org.withinsea.izayoi.cloister.core.kernal.Responder;

import java.util.List;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-23
 * Time: 下午5:24
 */
public abstract class Dispatcher implements Responder {

    abstract protected String getOutputContentType(Request request, String encoding);

    abstract protected String getOutputEncoding(Request request);

    abstract protected List<Responder> lookupResponders(Request request) throws CloisterException;

    abstract protected Target map(Request request) throws CloisterException;

    abstract protected void redispatch(Request request, final Target target) throws CloisterException;

    abstract protected void include(Request request, String targetPath) throws CloisterException;

    abstract protected void forward(Request request, String targetPath) throws CloisterException;

    abstract protected void redirect(Request request, String targetPath) throws CloisterException;

    @Override
    public void respond(Request request) throws CloisterException {

        if (!request.getPath().equals(request.getScope().getAttributes().get(CloisterConstants.ATTR_REDISPATCHED_REQUEST))) {
            Target mappedTarget = map(request);
            if (mappedTarget != null) {
                Object backupDispatched = request.getScope().getAttributes().get(CloisterConstants.ATTR_REDISPATCHED_REQUEST);
                request.getScope().getAttributes().put(CloisterConstants.ATTR_REDISPATCHED_REQUEST, true);
                redispatch(request, mappedTarget);
                request.getScope().getAttributes().put(CloisterConstants.ATTR_REDISPATCHED_REQUEST, backupDispatched);
                throw Dispatching.finish();
            }
        }

        CodefilePath pathHelper = new CodefilePath(request.getPath());
        if (!pathHelper.isFolder()) {
            String encoding = getOutputEncoding(request);
            request.setOutputEncoding(encoding);
            request.setOutputContentType(getOutputContentType(request, encoding));
        }

        for (Responder responder : lookupResponders(request)) {
            try {
                responder.respond(request);
            } catch (Dispatching dispatching) {
                if (dispatching.getTargetPath() != null) {
                    String newPath = dispatching.getTargetPath();
                    if (dispatching.isRedirect()) {
                        redirect(request, newPath);
                    } else if (dispatching.isForward()) {
                        forward(request, newPath);
                    } else {
                        include(request, newPath);
                    }
                    if (dispatching.isFinish()) {
                        throw dispatching;
                    }
                } else {
                    throw dispatching;
                }
            }
        }
    }

    protected static class Target {

        protected String path;
        protected Map<String, Object> parameters;

        public Target(String path, Map<String, Object> parameters) {
            this.path = path;
            this.parameters = parameters;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }

        public String getPath() {
            return path;
        }
    }
}
