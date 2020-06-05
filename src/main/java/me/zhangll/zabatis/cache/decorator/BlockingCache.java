package me.zhangll.zabatis.cache.decorator;

import me.zhangll.zabatis.cache.Cache;
import me.zhangll.zabatis.cache.CacheException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingCache implements Cache {

    private long timeout;
    private final Cache delegate;
    private final ConcurrentHashMap<Object, ReentrantLock> locks;

    public BlockingCache(Cache delegate){
        this.delegate = delegate;
        this.locks = new ConcurrentHashMap<>();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        try {
            //正常是先查询缓存，如果没有数据，再往缓存里放，查询的时候会加锁，所以此处不用加锁
            delegate.putObject(key, value);
        } finally {
            releaseLock(key);
        }
    }


    @Override
    public Object getObject(Object key) {
        acquireLock(key);
        Object value = delegate.getObject(key);
        if (value != null){
            //在这里释放锁，如果没有查到值，会进行插入缓存操作，会在Put完以后释放锁
            releaseLock(key);
        }

        return value;
    }

    @Override
    public Object removeObject(Object key) {
        //这里仅仅释放锁，不做任何操作？
        releaseLock(key);
        return null;
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    private ReentrantLock getLockForKey(Object key){
        return locks.computeIfAbsent(key, k -> new ReentrantLock());
    }

    private void acquireLock(Object key){
        Lock lock = getLockForKey(key);
        if (timeout > 0){
            try {
                boolean required = lock.tryLock(timeout, TimeUnit.MICROSECONDS);
                if (!required){
                    throw new CacheException("获取锁超时，超时时间：" + timeout + " Cache key: " +  key + " at the cache " + delegate.getId());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            lock.lock();
        }
    }

    private void releaseLock(Object key){
        ReentrantLock lock = locks.get(key);
        if (lock.isHeldByCurrentThread()){
            lock.unlock();
        }
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
