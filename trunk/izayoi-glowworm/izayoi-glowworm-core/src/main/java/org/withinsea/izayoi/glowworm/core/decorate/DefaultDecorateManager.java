package org.withinsea.izayoi.glowworm.core.decorate;

import org.withinsea.izayoi.commons.util.StringUtils;
import org.withinsea.izayoi.core.code.Path;
import org.withinsea.izayoi.core.invoke.ScopeInvokeManager;
import org.withinsea.izayoi.core.invoker.Invoker;
import org.withinsea.izayoi.core.scope.Scope;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Pattern;


/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-9
 * Time: 5:57:19
 */
public class DefaultDecorateManager extends ScopeInvokeManager implements DecorateManager {

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

    protected List<String> sort(Collection<String> names) {
        List<String> sorted = new ArrayList<String>(names);
        Collections.sort(sorted, new Comparator<String>() {
            @Override
            public int compare(String p1, String p2) {
                return getPriority(p2) - getPriority(p1);
            }
        });
        return sorted;
    }

    protected int getPriority(String name) {
        String type = new Path(name).getAppendantRole();
        return decoratorsOrder.contains(type) ? decoratorsOrder.indexOf(type) : Integer.MIN_VALUE;
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
