package me.zhangll.zabatis.transaction;

import me.zhangll.zabatis.exceptions.PersistenceException;

public class TransactionException extends PersistenceException
{

    private static final long serialVersionUID = 4347604441982528528L;

    public TransactionException() {
        super();
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionException(Throwable cause) {
        super(cause);
    }
}
