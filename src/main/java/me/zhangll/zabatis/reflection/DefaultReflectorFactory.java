package me.zhangll.zabatis.reflection;

import java.util.concurrent.ConcurrentHashMap;


/**
 * Reflector工厂，会缓存创建的reflector
 */
public class DefaultReflectorFactory implements ReflectorFactory {
    private boolean classCacheEnable = true;
    private final ConcurrentHashMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<>();


    public DefaultReflectorFactory(){

    }

    @Override
    public boolean isClassCacheEnabled() {
        return classCacheEnable;
    }

    @Override
    public void setClassCacheEnabled(boolean classCacheEnabled) {
        this.classCacheEnable = classCacheEnabled;
    }

    @Override
    public Reflector findForClass(Class<?> type) {
        if (classCacheEnable)
        {
            return reflectorMap.computeIfAbsent(type, Reflector::new);
        }else {
            return new Reflector(type);
        }
    }

}
