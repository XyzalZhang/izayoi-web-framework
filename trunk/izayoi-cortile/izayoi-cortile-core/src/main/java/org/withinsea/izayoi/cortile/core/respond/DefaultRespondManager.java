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

package org.withinsea.izayoi.cortile.core.respond;

import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.invoke.DefaultInvokeManager;
import org.withinsea.izayoi.core.invoker.Invoker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-7
 * Time: 15:45:39
 */
public class DefaultRespondManager extends DefaultInvokeManager implements RespondManager {

    protected Map<String, Invoker> responders;
    protected List<String> respondersOrder;

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
    public List<String> findResponderPaths(String requestPath) {

        List<String> responderPaths = new ArrayList<String>();

        Path parsedPath = new Path(requestPath);
        String standinNameRegex = Pattern.quote(parsedPath.getName())
                + "\\.(" + StringUtils.join("|", responders.keySet()) + ")"
                + "\\.[^\\.]+$";
        for (String standinName : sort(codeManager.listNames(parsedPath.getFolder(), standinNameRegex))) {
            responderPaths.add(parsedPath.getFolder() + "/" + standinName);
        }

        if (codeManager.exist(requestPath)) {
            responderPaths.add(requestPath);
        }

        return responderPaths;
    }

    @Override
    protected List<String> getInvokersOrder() {
        return respondersOrder;
    }

    public void setResponders(Map<String, Invoker> responders) {
        this.responders = responders;
    }

    public void setRespondersOrder(List<String> respondersOrder) {
        this.respondersOrder = respondersOrder;
    }
}
