package vn.iotstar.jobhub_hcmute_be.config;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class CustomCache implements Cache {

    private Cache delegate;

    public CustomCache(Cache delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        System.out.println("Cache key: " + key);
        ValueWrapper value = delegate.get(key);
        if (value != null) {
            System.out.println("Cache value: " + value.get());
        }
        return value;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        delegate.put(key, value);
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }
}