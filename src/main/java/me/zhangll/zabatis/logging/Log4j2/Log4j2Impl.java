package me.zhangll.zabatis.logging.Log4j2;

import me.zhangll.zabatis.logging.Log;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.spi.AbstractLogger;

public class Log4j2Impl implements Log{

    private final Log log;

    public Log4j2Impl(String clazz) {
        Logger logger = LogManager.getLogger(clazz);

        if (logger instanceof AbstractLogger){
            //TODO
//            log = new Log4j2AbstractLoggerImpl((AbstractLogger) logger);
            log = new Log4j2LoggerImpl(logger);
        }else{
            log = new Log4j2LoggerImpl(logger);
        }
    }

    @Override
    public boolean isDebugEnable() {
        return log.isDebugEnable();
    }

    @Override
    public boolean isTraceEnable() {
        return log.isTraceEnable();
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(s, e);
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }

    @Override
    public void trace(String s) {
        log.trace(s);
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }
}
