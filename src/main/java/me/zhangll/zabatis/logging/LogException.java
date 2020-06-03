package me.zhangll.zabatis.logging;

import me.zhangll.zabatis.exceptions.PersistenceException;

public class LogException extends PersistenceException {
    private static final long serialVersionUID = -8455367955340913569L;

    public LogException() {
    }

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}
