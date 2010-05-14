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

package org.withinsea.izayoi.glowworm.core.decorate;

import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.invoke.DefaultInvokeManager;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.Scope;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:57:19
 */
public class DefaultDecorateManager extends DefaultInvokeManager implements DecorateManager {

    protected String appendantFolder;
    protected String globalPrefix;

    protected Map<String, Invoker> decorators;
    protected List<String> decoratorsOrder;

    @Override
    protected Invoker getInvoker(String path) {
        String type = new Path(path).getAppendantRole();
        return decorators.get(decorators.containsKey(type) ? type : "default");
    }

    @Override
    public boolean isDecorator(String codePath) {
        Path parsedPath = new Path(codePath);
        return parsedPath.isAppendant() && decorators.containsKey(parsedPath.getAppendantRole());
    }

    @Override
    public List<String> findScopedDecoratorPaths(String scopeName, Scope scope) {

        String globalNameRegex = Pattern.quote(globalPrefix) + "[^\\.]*";
        String scopeRegex = Pattern.quote("@" + scopeName);
        String suffixRegex = "\\.(" + StringUtils.join("|", decorators.keySet()) + ")" + "\\.[^\\.]+$";

        List<String> decoratorPaths = new ArrayList<String>();

        String folder = appendantFolder;
        for (String decoratorName : sort(codeManager.listNames(folder, globalNameRegex + scopeRegex + suffixRegex))) {
            String decoratorPath = folder + "/" + decoratorName;
            if (check(getInvoker(decoratorPath), scope)) {
                decoratorPaths.add(decoratorPath);
            }
        }

        return decoratorPaths;
    }

    @Override
    public List<String> findRequestDecoratorPaths(String requestPath) {

        Path parsedPath = new Path(requestPath);

        String globalNameRegex = Pattern.quote(globalPrefix) + "[^\\.]*";
        String requestNameRegex = Pattern.quote(parsedPath.getName());
        String suffixRegex = "\\.(" + StringUtils.join("|", decorators.keySet()) + ")" + "\\.[^\\.]+$";

        List<String> decoratorPaths = new ArrayList<String>();

        String requestFolder = parsedPath.getFolder();
        String folder = appendantFolder;
        for (String folderItem : requestFolder.equals("/") ? new String[]{""} : requestFolder.split("/")) {
            folder = folder + "/" + folderItem;
            for (String scriptName : sort(codeManager.listNames(folder, globalNameRegex + suffixRegex))) {
                decoratorPaths.add(folder + "/" + scriptName);
            }
        }
        for (String scriptName : sort(codeManager.listNames(folder, requestNameRegex + suffixRegex))) {
            decoratorPaths.add(folder + "/" + scriptName);
        }

        return decoratorPaths;
    }

    protected boolean check(Invoker invoker, Scope scope) {
        for (Method m : invoker.getClass().getDeclaredMethods()) {
            if (m.getName().equals("invoker") && !Modifier.isVolatile(m.getModifiers())) {
                Class<?>[] pts = m.getParameterTypes();
                if (pts.length == 2 && pts[0] == String.class && pts[1].isAssignableFrom(scope.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected List<String> getInvokersOrder() {
        return decoratorsOrder;
    }

    public void setDecorators(Map<String, Invoker> decorators) {
        this.decorators = decorators;
    }

    public void setDecoratorsOrder(List<String> decoratorsOrder) {
        this.decoratorsOrder = decoratorsOrder;
    }

    public void setAppendantFolder(String appendantFolder) {
        this.appendantFolder = appendantFolder;
    }

    public void setGlobalPrefix(String globalPrefix) {
        this.globalPrefix = globalPrefix;
    }
}
