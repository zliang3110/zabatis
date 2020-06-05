package me.zhangll.zabatis.cache;

import me.zhangll.zabatis.exceptions.PersistenceException;

public class CacheException extends PersistenceException
{

    private static final long serialVersionUID = 2969152907408397196L;

    public CacheException() {
        super();
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
}
