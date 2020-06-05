package me.zhangll.zabatis.cache.decorator;

import me.zhangll.zabatis.cache.Cache;
import me.zhangll.zabatis.logging.Log;
import me.zhangll.zabatis.logging.LogFactory;

/**
 * 缓存Cache，统计命中次数和命中率
 */
public class LoggingCache implements Cache {

    private final Log log;
    private final Cache delegate;
    protected  int request = 0;
    protected  int hits = 0;


    public LoggingCache(Cache delegate){
        this.delegate = delegate;
        this.log = LogFactory.getLog(getId());
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        delegate.putObject(key, value);
    }

    @Override
    public Object getObject(Object key) {
        request++;
        final Object value = delegate.getObject(key);
        if (value != null){
            hits++;
        }
        if (log.isDebugEnabled()){
            log.debug("Cache命中率 [" + getId() + "]: " + getHitRatio());
        }
        return value;
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    /**
     * 返回缓存命中率
     * @return
     */
    public double getHitRatio(){
        return (double) hits / (double) request;
    }
}
