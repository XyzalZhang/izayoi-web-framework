package org.withinsea.izayoi.core.scope;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 2010-5-28
 * Time: 15:58:46
 */
abstract class InheritedScope<BASE extends Scope> implements Scope {

    protected final BASE baseScope;

    public InheritedScope() {
        this(null);
    }

    public InheritedScope(BASE baseScope) {
        this.baseScope = baseScope;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getConstant(String name) {
        Object obj = getScopeConstant(name);
        if (obj == null && baseScope != null) obj = baseScope.getConstant(name);
        return (T) obj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String name) {
        Object obj = getScopeAttribute(name);
        if (obj == null && baseScope != null) obj = baseScope.getAttribute(name);
        return (T) obj;
    }

    public BASE getBaseScope() {
        return baseScope;
    }
}