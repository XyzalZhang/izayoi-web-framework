package org.withinsea.izayoi.cloister.web.facade;

import org.withinsea.izayoi.cloister.core.exception.CloisterException;
import org.withinsea.izayoi.cloister.core.feature.dispatcher.Dispatcher;
import org.withinsea.izayoi.cloister.core.feature.dispatcher.Dispatching;
import org.withinsea.izayoi.cloister.core.feature.dynapage.DynapageEngineManager;
import org.withinsea.izayoi.cloister.core.feature.dynapage.DynapageManager;
import org.withinsea.izayoi.cloister.core.feature.dynapage.DynapageRenderer;
import org.withinsea.izayoi.cloister.core.feature.overlay.OverlaysEnvironment;
import org.withinsea.izayoi.cloister.core.feature.pathvar.PathvarMatcher;
import org.withinsea.izayoi.cloister.core.feature.postscript.*;
import org.withinsea.izayoi.cloister.core.impl.dispatcher.HiddenRequest;
import org.withinsea.izayoi.cloister.core.impl.environment.FolderEnvironment;
import org.withinsea.izayoi.cloister.core.impl.role.Context;
import org.withinsea.izayoi.cloister.core.impl.role.DispatchingRoleEngine;
import org.withinsea.izayoi.cloister.core.impl.scope.SimpleScope;
import org.withinsea.izayoi.cloister.core.impl.script.PropertiesScriptEngine;
import org.withinsea.izayoi.cloister.core.impl.script.Txt;
import org.withinsea.izayoi.cloister.core.kernal.CloisterConstants;
import org.withinsea.izayoi.cloister.core.kernal.Environment;
import org.withinsea.izayoi.cloister.core.kernal.Scope;
import org.withinsea.izayoi.cloister.web.feature.jspscript.JspScriptEngine;
import org.withinsea.izayoi.cloister.web.impl.*;
import org.withinsea.izayoi.cloister.web.kernal.CloisterWebConfig;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-28
 * Time: 下午12:11
 */
public class CloisterWebFacade implements Filter {

    protected ServletContext servletContext;

    protected Properties globalConfig;
    protected Environment globalEnvironment;
    protected Scope globalScope;
    protected Dispatcher dispatcher;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
        this.globalConfig = getConfig();
        this.globalEnvironment = createGlobalEnvironment();
        this.globalScope = createGlobalScope();
        this.dispatcher = createDispatcher();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        } catch (CloisterException e) {
            throw new ServletException(e);
        }
    }

    protected void doFilter(HttpServletRequest httpReq, HttpServletResponse httpResp, FilterChain chain)
            throws IOException, ServletException, CloisterException {

        if (JspScriptEngine.RUNTIME_CONTEXT.isEmpty()) {
            JspScriptEngine.RUNTIME_CONTEXT.set(httpReq, httpResp);
        }

        try {

            if (!Boolean.TRUE.equals(httpReq.getAttribute(CloisterConstants.ATTR_DISPATCHED_GLOBALSCOPES))) {

                httpReq.setAttribute(CloisterConstants.ATTR_DISPATCHED_GLOBALSCOPES, true);

                AppRequest appRequest = new AppRequest(globalEnvironment, globalScope, servletContext);
                dispatcher.respond(appRequest);

                SessionRequest sessionRequest = new SessionRequest(globalEnvironment, globalScope, httpReq.getSession());
                dispatcher.respond(sessionRequest);
            }

            RequestRequest requestRequest = new RequestRequest(globalEnvironment, globalScope, httpReq, httpResp, chain);
            dispatcher.respond(requestRequest);

            chain.doFilter(httpReq, httpResp);

        } catch (HiddenRequest hiddenRequest) {

            httpResp.sendError(404, hiddenRequest.getPath());

        } catch (Dispatching dispatching) {
            if (!dispatching.isFinish()) {
                throw dispatching;
            }
        }
    }

    protected Properties getConfig() {
        return CloisterWebConfig.getDefault(servletContext);
    }

    protected Environment createGlobalEnvironment() {

        Environment webappEnvironment = new WebappEnvironment(servletContext);

        List<Environment> extraOverlays = new ArrayList<Environment>();
        {
            String overlaysPath = globalConfig.getProperty("cloister.overlays").trim();
            if (!overlaysPath.equals("")) {
                if (overlaysPath.endsWith("/*")) {
                    File overlaysFolder = new File(servletContext.getRealPath(overlaysPath.replaceAll("\\*$", "")).replace("%20", " "));
                    if (overlaysFolder.exists() && overlaysFolder.isDirectory()) {
                        for (File overlayFolder : overlaysFolder.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File file) {
                                return file.isDirectory();
                            }
                        })) {
                            extraOverlays.add(new FolderEnvironment(overlayFolder));
                        }
                    }
                } else {
                    for (String overlayPath : overlaysPath.trim().split("[\\s;, ]+")) {
                        File overlayFolder = new File(servletContext.getRealPath(overlayPath).replace("%20", " "));
                        if (overlayFolder.exists() && overlayFolder.isDirectory()) {
                            extraOverlays.add(new FolderEnvironment(overlayFolder));
                        }
                    }
                }
            }
        }

        if (extraOverlays.isEmpty()) {
            return webappEnvironment;
        } else {
            OverlaysEnvironment overlaysEnvironment = new OverlaysEnvironment();
            overlaysEnvironment.getOverlays().add(webappEnvironment);
            overlaysEnvironment.getOverlays().addAll(extraOverlays);
            return overlaysEnvironment;
        }
    }

    protected Scope createGlobalScope() {
        return new SimpleScope();
    }

    protected Dispatcher createDispatcher() {

        String encoding = globalConfig.getProperty("cloister.encoding");
        boolean ignoreUnsupported = Boolean.parseBoolean(globalConfig.getProperty("cloister.ignoreUnsupported"));

        Set<String> bypassPaths = new LinkedHashSet<String>();
        {
            String bypassConf = globalConfig.getProperty("cloister.bypass").trim();
            if (!bypassConf.equals("")) {
                bypassPaths.addAll(Arrays.asList(bypassConf.split("[\\s;, ]+")));
            }
        }

        PathvarMatcher pathvarMatcher = new PathvarMatcher();

        DynapageEngineManager dynapageEngineManager = new DynapageEngineManager();
        registerDynapageEngines(dynapageEngineManager);
        DynapageManager dynapageManager = new DynapageManager();
        dynapageManager.setDynapageEngineManager(dynapageEngineManager);
        DynapageRenderer dynapageRenderer = new DynapageRenderer();
        dynapageRenderer.setDynapageEngineManager(dynapageEngineManager);

        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        registerScriptEngines(scriptEngineManager);
        RoleEngineManager roleEngineManager = new RoleEngineManager();
        registerRoleEngines(roleEngineManager);
        PostscriptManager postscriptManager = new PostscriptManager();
        postscriptManager.setScriptEngineManager(scriptEngineManager);
        postscriptManager.setRoleEngineManager(roleEngineManager);
        PostscriptEncloser postscriptEncloser = new PostscriptEncloser();
        postscriptEncloser.setScriptEngineManager(scriptEngineManager);
        postscriptEncloser.setRoleEngineManager(roleEngineManager);
        postscriptEncloser.setIgnoreUnsupported(ignoreUnsupported);

        WebappDispatcher dispatcher = new WebappDispatcher();
        dispatcher.setPathvarMatcher(pathvarMatcher);
        dispatcher.setDynapageManager(dynapageManager);
        dispatcher.setDynapageRenderer(dynapageRenderer);
        dispatcher.setPostscriptManager(postscriptManager);
        dispatcher.setPostscriptEncloser(postscriptEncloser);
        dispatcher.setEncoding(encoding);
        dispatcher.setBypassPaths(bypassPaths);

        return dispatcher;
    }

    protected void registerDynapageEngines(DynapageEngineManager dynapageEngineManager) {
    }

    protected void registerRoleEngines(RoleEngineManager roleEngineManager) {
        roleEngineManager.registerRoleEngine("dispatching", new DispatchingRoleEngine());
        roleEngineManager.registerRoleEngine("context", new Context());
    }

    protected void registerScriptEngines(ScriptEngineManager scriptEngineManager) {
        String encoding = globalConfig.getProperty("cloister.encoding");
        scriptEngineManager.registerScriptEngine("txt", new Txt(encoding));
        scriptEngineManager.registerScriptEngine("properties", new PropertiesScriptEngine(encoding));
        scriptEngineManager.registerScriptEngine("jsp", createJspScriptEngine());
    }

    protected ScriptEngine createJspScriptEngine() {
        String encoding = globalConfig.getProperty("cloister.encoding");
        return new JspScriptEngine(servletContext, encoding);
    }
}
