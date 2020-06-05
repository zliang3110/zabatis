package me.zhangll.zabatis.cache.decorator;

import me.zhangll.zabatis.cache.Cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Deque;
import java.util.LinkedList;

public class WeakCache implements Cache {

    //强引用键值对队列
    private final Deque<Object> hardLinksToAvoidGarbageCollection;
    //被GC回收的WeakEntity集合,避免被gc
    private final ReferenceQueue<Object> queueOfGarbageCollectedEntries;

    private final Cache delegate;

    private int numberOfHardLinks;

    public WeakCache(Cache delegate) {
        this.delegate = delegate;
        this.numberOfHardLinks = 256;
        this.hardLinksToAvoidGarbageCollection = new LinkedList<>();
        this.queueOfGarbageCollectedEntries = new ReferenceQueue<>();
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public int getSize() {
        removeGarbageCollectedItems();
        return delegate.getSize();
    }

    public void setSize(int size){
        this.numberOfHardLinks = size;
    }

    @Override
    public void putObject(Object key, Object value) {
        removeGarbageCollectedItems();
        delegate.putObject(key, new WeakEntity(key, value, queueOfGarbageCollectedEntries));
    }

    @Override
    public Object getObject(Object key) {
        Object result = null;
        WeakReference<Object> weakReference = (WeakReference<Object>) delegate.getObject(key);
        if (weakReference!= null){
            result = weakReference.get();
            if (result != null){
                delegate.removeObject(key);
            }else {
                //非空，添加到 hardLinksToAvoidGarbageCollection 队列首部，未做key唯一性判断，所以存在重复添加的情况 ，避免被 GC
                hardLinksToAvoidGarbageCollection.addFirst(result);
                if (hardLinksToAvoidGarbageCollection.size() > numberOfHardLinks) {
                    //如果长度超出上限，则移除队列尾部的元素
                    hardLinksToAvoidGarbageCollection.removeLast();
                }
            }
        }
        return result;
    }

    @Override
    public Object removeObject(Object key) {
        removeGarbageCollectedItems();
        return delegate.removeObject(key);
    }

    @Override
    public void clear() {
        hardLinksToAvoidGarbageCollection.clear();
        removeGarbageCollectedItems();
        delegate.clear();
    }


    /**
     * 移除已经被 GC 回收的键
     */
    private void removeGarbageCollectedItems() {
        WeakEntity sv;
        while ((sv = (WeakEntity) queueOfGarbageCollectedEntries.poll()) != null){
            delegate.removeObject(sv.key);
        }
    }


    /**
     * 继承自 WeakReference ，增加缓存key属性，
     */
    private static class WeakEntity extends WeakReference<Object>{
        //键
        private final Object key;

        private WeakEntity(Object key, Object value, ReferenceQueue<Object> garbageCollectionQueue){
            super(value, garbageCollectionQueue);
            this.key = key;
        }
    }
}
