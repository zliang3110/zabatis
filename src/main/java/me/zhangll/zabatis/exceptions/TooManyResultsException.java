package me.zhangll.zabatis.exceptions;

public class TooManyResultsException extends PersistenceException {
    private static final long serialVersionUID = 4066998627388113414L;

    public TooManyResultsException() {
    }

    public TooManyResultsException(String message) {
        super(message);
    }

    public TooManyResultsException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManyResultsException(Throwable cause) {
        super(cause);
    }
}
