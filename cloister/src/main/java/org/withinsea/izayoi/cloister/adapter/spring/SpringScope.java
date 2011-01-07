package org.withinsea.izayoi.cloister.adapter.spring;

import org.springframework.context.ApplicationContext;
import org.withinsea.izayoi.cloister.core.kernal.Scope;

import java.util.*;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 11-1-5
 * Time: 上午10:22
 */
public class SpringScope implements Scope {

    protected ApplicationContext applicationContext;
    protected SpringContextMap attributes;

    public SpringScope(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.attributes = new SpringContextMap(applicationContext);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Map<String, Object> getScopeAttributes() {
        return attributes;
    }

    @Override
    public Scope getParentScope() {
        return null;
    }

    @Override
    public String getName() {
        return "spring";
    }

    protected static class SpringContextMap implements Map<String, Object> {

        protected ApplicationContext applicationContext;

        public SpringContextMap(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return keySet().size();
        }

        @Override
        public boolean isEmpty() {
            return keySet().isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return Arrays.asList(applicationContext.getBeanDefinitionNames()).contains(key.toString())
                    || applicationContext.getAutowireCapableBeanFactory().containsBean(key.toString());
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object get(Object key) {
            return applicationContext.getBean(key.toString());
        }

        @Override
        public Object put(String key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Object remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends String, ?> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> keySet() {
            Set<String> keys = new LinkedHashSet<String>();
            keys.addAll(Arrays.asList(applicationContext.getBeanDefinitionNames()));
            return keys;
        }

        @Override
        public Collection<Object> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<Entry<String, Object>> entrySet() {
            Map<String, Object> map = new LinkedHashMap<String, Object>();
            for (String key : keySet()) {
                map.put(key, get(key));
            }
            return map.entrySet();
        }
    }
}
