package org.withinsea.izayoi.cloister.core.feature.pathvar;

import org.withinsea.izayoi.cloister.core.feature.postscript.PostscriptPath;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.common.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-10-26
 * Time: 17:07:01
 */
public class PathvarMatcher {

    public PathvarMatch match(Environment environment, String virtualPath) {

        virtualPath = virtualPath.trim();

        if (!environment.exist("/")) {
            return null;
        } else if (environment.exist(virtualPath)) {
            String matchedPath = virtualPath;
            if (environment.isFolder(virtualPath) && !matchedPath.endsWith("/")) {
                matchedPath += "/";
            }
            Environment.Codefile codefile = environment.getCodefile(matchedPath);
            return (codefile == null) ? null : new MatchImpl(codefile);
        } else {
            Map<String, String> pathvars = new LinkedHashMap<String, String>();
            String matchedPath = match(environment, "/", pathvars, environment.getCodefile("/"), virtualPath);
            if (matchedPath == null) {
                return null;
            } else {
                matchedPath = matchedPath.replaceAll("/+", "/");
                if (virtualPath.endsWith("/") && !matchedPath.endsWith("/")) {
                    matchedPath += "/";
                }
                Environment.Codefile codefile = environment.getCodefile(matchedPath);
                return (codefile == null) ? null : new MatchImpl(codefile, pathvars);
            }
        }
    }

    protected String match(Environment environment, String matchedPath, Map<String, String> pathVariables,
                           Environment.Codefile folder, String virtualPath) {

        if (virtualPath.equals("")) {
            return folder.getPath();
        }

        MatchPath virtualPathHelper = new MatchPath(virtualPath);

        String pathName = virtualPathHelper.getPath().substring(1).replaceAll("/.*", "");
        {
            String nextFolderPath = folder.getPath().replaceAll("/+$", "") + "/" + pathName + "/";
            if (environment.isFolder(nextFolderPath)) {
                Environment.Codefile nextFolder = environment.getCodefile(nextFolderPath);
                String nextVirtualPath = virtualPath.substring(pathName.length() + 1);
                return match(environment, matchedPath + pathName + "/", pathVariables, nextFolder, nextVirtualPath);
            }
        }

        // item13.html
        List<Environment.Codefile> codefiles = environment.listCodefiles(folder.getPath(), ".+");
        Collections.sort(codefiles, new Comparator<Environment.Codefile>() {
            @Override
            public int compare(Environment.Codefile f1, Environment.Codefile f2) {
                return score(f2) - score(f1);
            }
        });

        // item{id}.html.template.vm
        for (Environment.Codefile codefile : codefiles) {

            // item{id}
            MatchPath matchPath = new MatchPath(codefile.getPath());
            String codeName = matchPath.getFullname();
            String codeMainName = matchPath.getBasicname();

            // \Qitem\E(.+).*? on item13.html
            Matcher pathMatcher = Pattern.compile(StringUtils.replaceAll(
                    codeMainName, "\\{\\w+\\}", new StringUtils.Replace() {
                        @Override
                        public String replace(String... groups) {
                            return "(.+)";
                        }
                    }, new StringUtils.Transform() {
                        @Override
                        public String transform(String str) {
                            return Pattern.quote(str);
                        }
                    }
            ) + ".*?").matcher(pathName);

            if (pathMatcher.matches()) {

                // \Qitem\E\{(id)\}.*? on item{id}.html.template.vm
                Matcher codeMatcher = Pattern.compile(StringUtils.replaceAll(
                        codeMainName, "\\{(\\w+)\\}", new StringUtils.Replace() {
                            @Override
                            public String replace(String... groups) {
                                return "\\{(" + groups[1] + ")\\}";
                            }
                        }, new StringUtils.Transform() {
                            @Override
                            public String transform(String str) {
                                return Pattern.quote(str);
                            }
                        }
                ) + ".*?").matcher(codeName);

                codeMatcher.matches();
                for (int i = 1; i <= codeMatcher.groupCount(); i++) {
                    pathVariables.put(codeMatcher.group(i), pathMatcher.group(i));
                }

                StringBuffer templateNameBuffer = new StringBuffer(pathName);
                for (int i = codeMatcher.groupCount(); i >= 1; i--) {
                    templateNameBuffer.replace(pathMatcher.start(i), pathMatcher.end(i), "{" + codeMatcher.group(i) + "}");
                }
                // item{id}.html
                String templateName = templateNameBuffer.toString();

                String nextFolderPath = folder.getPath() + codeName + "/";
                if (environment.isFolder(nextFolderPath)) {
                    Environment.Codefile nextFolder = environment.getCodefile(nextFolderPath);
                    String nextVirtualPath = virtualPath.substring(pathName.length() + 1);
                    return match(environment, matchedPath + "/" + templateName, pathVariables, nextFolder, nextVirtualPath);
                } else {
                    return matchedPath + "/" + templateName;
                }
            }
        }

        return null;
    }

    protected static final Pattern PATHVAR_PATTERN = Pattern.compile("\\{\\w+\\}");

    protected int score(Environment.Codefile codefile) {
        String name = new PostscriptPath(codefile.getPath()).getBaseMainname();
        Matcher matcher = PATHVAR_PATTERN.matcher(name);
        int count = 0;
        while (matcher.find()) count++;
        return -count;
    }

    protected static class MatchImpl implements PathvarMatch {

        protected Environment.Codefile codefile;
        protected Map<String, String> vars;

        public MatchImpl(Environment.Codefile codefile) {
            this(codefile, new LinkedHashMap<String, String>());
        }

        public MatchImpl(Environment.Codefile codefile, Map<String, String> vars) {
            this.codefile = codefile;
            this.vars = vars;
        }

        @Override
        public Environment.Codefile getCodefile() {
            return codefile;
        }

        @Override
        public Map<String, String> getVars() {
            return vars;
        }
    }
}
 