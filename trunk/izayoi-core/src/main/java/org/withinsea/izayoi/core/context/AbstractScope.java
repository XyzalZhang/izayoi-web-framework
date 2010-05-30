package org.withinsea.izayoi.core.context;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-28
 * Time: 15:58:46
 */
abstract class AbstractScope<BASE extends Scope> implements Scope {

    protected final BASE baseScope;

    public AbstractScope() {
        this(null);
    }

    public AbstractScope(BASE baseScope) {
        this.baseScope = baseScope;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(String name) {
        Object obj = getConstant(name);
        if (obj == null) obj = getAttribute(name);
        if (obj == null && baseScope != null) obj = baseScope.getBean(name);
        return (T) obj;
    }

    @Override
    public <T> void setBean(String name, T object) {
        setAttribute(name, object);
    }

    protected abstract Object getConstant(String name);

    protected abstract Object getAttribute(String name);

    protected abstract void setAttribute(String name, Object obj);

    public BASE getBaseScope() {
        return baseScope;
    }
}