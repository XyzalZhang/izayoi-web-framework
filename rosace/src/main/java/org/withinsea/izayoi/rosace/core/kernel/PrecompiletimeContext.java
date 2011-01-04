package org.withinsea.izayoi.rosace.core.kernel;

import org.withinsea.izayoi.common.util.Varstack;
import org.withinsea.izayoi.rosace.core.exception.RosaceRuntimeException;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-11-30
 * Time: 12:31:35
 */
public class PrecompiletimeContext {

    protected static ThreadLocal<Deque<PrecompiletimeContext>> CONTEXT_HOLDER = new ThreadLocal<Deque<PrecompiletimeContext>>() {
        @Override
        protected Deque<PrecompiletimeContext> initialValue() {
            return new LinkedList<PrecompiletimeContext>();
        }
    };

    @SuppressWarnings("unchecked")
    public static <T extends PrecompiletimeContext> T get() {
        Deque<PrecompiletimeContext> stack = CONTEXT_HOLDER.get();
        if (stack.isEmpty()) {
            throw new RosaceRuntimeException("missing opened PrecompiletimeContext.");
        }
        return (T) stack.peek();
    }

    public static PrecompiletimeContext open(TemplateEngine templateEngine, ElEngineManager elEngineManager) {
        return open(new PrecompiletimeContext(templateEngine, elEngineManager));
    }

    public static PrecompiletimeContext open(PrecompiletimeContext ctx) {
        Deque<PrecompiletimeContext> stack = CONTEXT_HOLDER.get();
        stack.push(ctx);
        return ctx;
    }

    @SuppressWarnings("unchecked")
    public static <T extends PrecompiletimeContext> T close() {
        Deque<PrecompiletimeContext> stack = CONTEXT_HOLDER.get();
        if (stack.isEmpty()) {
            throw new RosaceRuntimeException("missing opened PrecompiletimeContext.");
        }
        return (T) stack.pop();
    }

    protected Varstack scopeAttributes = new Varstack();
    protected TemplateEngine engine;
    protected ElEngineManager elEngineManager;

    public PrecompiletimeContext(TemplateEngine templateEngine, ElEngineManager elEngineManager) {
        this.engine = templateEngine;
        this.elEngineManager = elEngineManager;
    }

    @SuppressWarnings("unchecked")
    public <T> T getScopeAttribute(String key) {
        return (T) scopeAttributes.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getScopeAttribute(String key, T defaultValue) {
        T value = (T) scopeAttributes.get(key);
        return (value == null) ? defaultValue : value;
    }

    public <T> void setScopeAttribute(String key, T value) {
        if (isLocked()) {
            throw new RosaceRuntimeException("Invalid attribute setting, context locked.");
        } else {
            scopeAttributes.put(key, value);
        }
    }

    public void openScope() {
        scopeAttributes.push();
    }

    public void closeScope() {
        scopeAttributes.pop();
    }

    public boolean isLocked() {
        return Boolean.TRUE.equals(getScopeAttribute(RosaceConstants.ATTR_LOCKED));
    }

    public ElEngine getElEngine() {
        String elType = get().getScopeAttribute(RosaceConstants.ATTR_ELTYPE);
        if (elType == null) {
            throw new RosaceRuntimeException("unspecified EL type.");
        }
        ElEngineManager elEngineManager = get().getElEngineManager();
        if (elEngineManager == null) {
            throw new RosaceRuntimeException("missing ElEngineManager.");
        }
        ElEngine elEngine = elEngineManager.lookupElEngine(elType);
        if (elEngine == null) {
            throw new RosaceRuntimeException("missing ElEngine for type " + elType + ".");
        }
        return elEngine;
    }

    @SuppressWarnings("unchecked")
    public <T extends ElEngineManager> T getElEngineManager() {
        return (T) elEngineManager;
    }

    public void setElEngineManager(ElEngineManager elEngineManager) {
        this.elEngineManager = elEngineManager;
    }

    @SuppressWarnings("unchecked")
    public <T extends TemplateEngine> T getEngine() {
        return (T) engine;
    }

    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }
}
