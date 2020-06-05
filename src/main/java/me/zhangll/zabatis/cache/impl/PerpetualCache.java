package me.zhangll.zabatis.cache.impl;

import me.zhangll.zabatis.cache.Cache;
import me.zhangll.zabatis.cache.CacheException;

import java.util.HashMap;
import java.util.Map;

public class PerpetualCache implements Cache {

    private final String id;

    private final Map<Object, Object> cache = new HashMap<>();

    public PerpetualCache(String id){
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public Object getObject(Object key) {
        return cache.get(key);
    }

    @Override
    public Object removeObject(Object key) {
        return cache.remove(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public int getSize() {
        return cache.size();
    }

    @Override
    public int hashCode() {
        if (getId() == null){
            throw new CacheException("Cache实例需要一个ID.");
        }
        return getId().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (getId() == null){
            throw new CacheException("Cache实例需要一个ID.");
        }

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Cache)){
            return false;
        }

        Cache otherCache = (Cache) obj;
        return getId().equals(otherCache.getId());
    }
}
