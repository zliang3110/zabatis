package me.zhangll.zabatis.cache.decorator;

import me.zhangll.zabatis.cache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache implements Cache{


    private final Cache delegate;
    private Map<Object, Object> keyMap;
    private Object eldestKey;

    public LruCache(Cache delegate){
        this.delegate = delegate;
        setSize(1024);
    }

    @Override
    public String getId(){
        return delegate.getId();
    }

    @Override
    public int getSize(){
        return delegate.getSize();
    }

    public void setSize(final int size){
        keyMap = new LinkedHashMap<Object, Object>(size, .75F, true){

            private static final long serialVersionUID = 3068862699599166798L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
                boolean tooBig = size() > size;
                if (tooBig){
                    eldestKey = eldest.getKey();
                }

                return tooBig;
            }
        };
    }

    @Override
    public void putObject(Object key, Object value){
        delegate.putObject(key, value);
        cycleKeyList(key);
    }

    @Override
    public Object getObject(Object key) {
        keyMap.get(key);
        return delegate.getObject(key);
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
        keyMap.clear();
    }

    private void cycleKeyList(Object key) {
        keyMap.put(key, key);
        if (eldestKey != null){
            delegate.removeObject(eldestKey);
            eldestKey = null;
        }
    }


}
