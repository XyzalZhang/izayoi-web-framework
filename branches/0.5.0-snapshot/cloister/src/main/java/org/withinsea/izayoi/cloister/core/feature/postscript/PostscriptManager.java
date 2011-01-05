package org.withinsea.izayoi.cloister.core.feature.postscript;

import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-15
 * Time: 上午8:57
 */
public class PostscriptManager {

    protected ScriptEngineManager scriptEngineManager;
    protected RoleEngineManager roleEngineManager;

    public boolean isPostscript(Environment environment, String path) {
        return environment.exist(path) && new PostscriptPath(path).isPostscript();
    }

    public List<Environment.Codefile> lookupPostscripts(Environment environment, String basePath, Scope scope) {

        if (scriptEngineManager.getScriptEngineNames().isEmpty()
                || roleEngineManager.getRoleEngineNames().isEmpty()) {
            return Collections.emptyList();
        }

        List<String> pathSuffixRegexes = new ArrayList<String>();
        for (String roleName : roleEngineManager.getRoleEngineNames()) {
            for (String scriptName : scriptEngineManager.getScriptEngineNames()) {
                pathSuffixRegexes.add("\\.(\\Q" + roleName + "\\E)\\.(\\Q" + scriptName + "\\E)$");
            }
        }

        PostscriptPath basePostscriptPath = new PostscriptPath(basePath);
        List<Environment.Codefile> postscripts = new ArrayList<Environment.Codefile>();

        if (basePostscriptPath.isFolder()) {

            for (String pathSuffixRegex : pathSuffixRegexes) {
                postscripts.addAll(environment.listCodefiles(basePostscriptPath.getFolder(),
                        Pattern.quote("@" + scope.getName().toLowerCase()) + "(|-[^\\.]+)" + pathSuffixRegex));
            }

        } else {

            String[] split = basePostscriptPath.getFolder().split("/");
            String currentFolder = "";
            for (int i = 0; i < split.length - 1; i++) {
                currentFolder += split[i] + "/";
                for (String pathSuffixRegex : pathSuffixRegexes) {
                    postscripts.addAll(environment.listCodefiles(currentFolder,
                            Pattern.quote("@folder(|-[^\\.]*)") + pathSuffixRegex));
                }
            }

            for (String pathSuffixRegex : pathSuffixRegexes) {
                postscripts.addAll(environment.listCodefiles(basePostscriptPath.getFolder(),
                        Pattern.quote(basePostscriptPath.getBaseFullname()) + pathSuffixRegex));
            }
        }

        return postscripts;
    }

    public RoleEngineManager getRoleEngineManager() {
        return roleEngineManager;
    }

    public void setRoleEngineManager(RoleEngineManager roleEngineManager) {
        this.roleEngineManager = roleEngineManager;
    }

    public ScriptEngineManager getScriptEngineManager() {
        return scriptEngineManager;
    }

    public void setScriptEngineManager(ScriptEngineManager scriptEngineManager) {
        this.scriptEngineManager = scriptEngineManager;
    }
}
