package org.withinsea.izayoi.cloister.core.kernal;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mo Chen <withinsea@gmail.com>
 * Date: 10-12-26
 * Time: 下午4:38
 */
public class TimestampCache<T> {

    public static <T> TimestampCache<T> getCache(Map<String, Object> context, String cacheId) {
        @SuppressWarnings("unchecked")
        TimestampCache<T> timestampCache = (TimestampCache<T>) context.get(cacheId);
        if (timestampCache == null) {
            timestampCache = new TimestampCache<T>();
            context.put(cacheId, timestampCache);
        }
        return timestampCache;
    }

    protected Map<String, Item<T>> cacheMap = new HashMap<String, Item<T>>();

    protected TimestampCache() {
    }

    public void put(String key, T object, long lastModified) {
        cacheMap.put(key, new Item<T>(object, lastModified));
    }

    public T get(String key) {
        return cacheMap.get(key).getObject();
    }

    public boolean isModified(String key, long lastModified) {
        Item<T> item = cacheMap.get(key);
        return (item == null) || (item.getLastModified() < lastModified);
    }

    public boolean containsKey(String key) {
        return cacheMap.containsKey(key);
    }

    public T remove(String key) {
        return cacheMap.remove(key).getObject();
    }

    public void clear() {
        cacheMap.clear();
    }

    protected static final class Item<T> {

        protected T object;
        protected long lastModified;

        protected Item(T object, long lastModified) {
            this.object = object;
            this.lastModified = lastModified;
        }

        public long getLastModified() {
            return lastModified;
        }

        public T getObject() {
            return object;
        }
    }
}
