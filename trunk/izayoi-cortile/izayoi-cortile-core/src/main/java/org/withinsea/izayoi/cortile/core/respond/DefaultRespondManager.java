package org.withinsea.izayoi.cortile.core.respond;

import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.invoke.ScopeInvokeManager;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.custom.Request;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-7
 * Time: 15:45:39
 */
public class DefaultRespondManager extends ScopeInvokeManager implements RespondManager {

    protected Map<String, String> mimeTypes;
    protected Map<String, Invoker> responders;

    @Override
    @SuppressWarnings("unchecked")
    protected Invoker getInvoker(String path) {
        Path parsedPath = new Path(path);
        return responders.get(!parsedPath.isAppendant() ? "default" :
                responders.containsKey(parsedPath.getAppendantRole()) ? parsedPath.getAppendantRole() : "default");
    }

    @Override
    public boolean isResponder(String path) {
        Path parsedPath = new Path(path);
        return parsedPath.isAppendant() && responders.containsKey(parsedPath.getAppendantRole());
    }

    @Override
    public boolean hasResponders(String requestPath) {
        return !findResponderPaths(requestPath).isEmpty();
    }

    @Override
    public String findResponderPath(String requestPath, Request scope) {

        HttpServletRequest request = scope.getRequest();

        List<String> responderPaths = findResponderPaths(requestPath);
        if (responderPaths.isEmpty()) {
            return null;
        }

        return responderPaths.get(0);
    }

    protected List<String> findResponderPaths(String requestPath) {

        List<String> responderPaths = new ArrayList<String>();

        Path parsedPath = new Path(requestPath);
        String standinNameRegex = Pattern.quote(parsedPath.getName())
                + "\\.(" + StringUtils.join("|", responders.keySet()) + ")"
                + "\\.[^\\.]+$";
        for (String standinName : codeManager.listNames(parsedPath.getFolder(), standinNameRegex)) {
            responderPaths.add(parsedPath.getFolder() + "/" + standinName);
        }

        if (codeManager.exist(requestPath)) {
            responderPaths.add(requestPath);
        }

        return responderPaths;
    }

    public void setResponders(Map<String, Invoker> responders) {
        this.responders = responders;
    }

    public void setMimeTypes(Map<String, String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }
}
