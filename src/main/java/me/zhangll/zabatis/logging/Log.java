package me.zhangll.zabatis.logging;

public interface Log {

    boolean isDebugEnabled();

    boolean isTraceEnable();

    void error(String s, Throwable e);

    void error(String s);

    void debug(String s);

    void trace(String s);

    void warn(String s);
}
