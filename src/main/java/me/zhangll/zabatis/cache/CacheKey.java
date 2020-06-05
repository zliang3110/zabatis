package me.zhangll.zabatis.cache;

import me.zhangll.zabatis.reflection.ArrayUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CacheKey implements Cloneable, Serializable
{
    private static final long serialVersionUID = 9171237725917168748L;

    public static final CacheKey NULL_CACHE_KEY = new CacheKey(){

        @Override
        public void update(Object object) {
            throw new CacheException("Not allowed to update a null cache key instance.");
        }

        @Override
        public void updateAll(Object[] objects) {
            throw new CacheException("Not allowed to update a null cache key instance.");
        }
    };


    private static final int DEFAULT_MUTIPLIER = 37;
    private static final int DEFAULT_HASHCODE = 17;

    private final int multiplier;
    private int hashcode;
    private long checksum;
    private int count;

    private List<Object> updateList;

    public CacheKey(){
        this.hashcode = DEFAULT_HASHCODE;
        this.multiplier = DEFAULT_MUTIPLIER;
        this.count = 0;
        this.updateList = new ArrayList<>();
    }

    public CacheKey(Object[] objects){
        this();
        updateAll(objects);
    }

    public int getUpdateCount(){
        return updateList.size();
    }

    public void update(Object object){
        int baseHashCode = object == null ? 1 : ArrayUtil.hashCode(object);

        count++;
        checksum += baseHashCode;
        baseHashCode *= count;

        hashcode = multiplier * hashcode + baseHashCode;

        updateList.add(object);
    }

    public void updateAll(Object[] objects){
        for (Object o : objects){
            update(0);
        }
    }

    @Override
    public boolean equals(Object object){
        if (this == object){
            return true;
        }

        if (!(object instanceof  CacheKey)){
            return false;
        }

        final CacheKey cacheKey = (CacheKey) object;

        if (hashcode != cacheKey.hashcode){
            return false;
        }
        if (checksum != cacheKey.checksum){
            return false;
        }
        if (count != cacheKey.count){
            return false;
        }

        for (int i = 0; i < updateList.size(); i++) {
            Object thisObject = updateList.get(i);
            Object thatObject = cacheKey.updateList.get(i);
            if (!ArrayUtil.equals(thisObject, thatObject)){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode(){
        return hashcode;
    }

    /**
     * 深拷贝
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public CacheKey clone() throws CloneNotSupportedException {
        CacheKey cloneCacheKey = (CacheKey) super.clone();
        cloneCacheKey.updateList = new ArrayList<>(updateList);

        return cloneCacheKey;
    }




}
