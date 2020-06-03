package me.zhangll.zabatis.exceptions;

public class PersistenceException extends IbatisException {
    private static final long serialVersionUID = 6205437116027701445L;

    public PersistenceException() {
    }

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenceException(Throwable cause) {
        super(cause);
    }
}
