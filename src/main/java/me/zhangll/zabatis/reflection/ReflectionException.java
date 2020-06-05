package me.zhangll.zabatis.reflection;

import me.zhangll.zabatis.exceptions.PersistenceException;

public class ReflectionException extends PersistenceException {


    private static final long serialVersionUID = -5019266597895399969L;

    public ReflectionException() {
        super();
    }

    public ReflectionException(String message) {
        super(message);
    }

    public ReflectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReflectionException(Throwable cause) {
        super(cause);
    }

}
