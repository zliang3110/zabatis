package me.zhangll.zabatis.logging.Log4j2;

import me.zhangll.zabatis.logging.Log;
import me.zhangll.zabatis.logging.LogFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;



public class Log4j2LoggerImpl implements Log{

    private static final Marker MAKER = MarkerManager.getMarker(LogFactory.MARKER);

    private final Logger log;

    public Log4j2LoggerImpl(Logger log) {
        this.log = log;
    }


    @Override
    public boolean isDebugEnable() {
        return log.isDebugEnabled();
    }

    @Override
    public boolean isTraceEnable() {
        return log.isTraceEnabled();
    }

    @Override
    public void error(String s, Throwable e) {
        log.error(MAKER, s , e);
    }

    @Override
    public void error(String s) {
        log.error(MAKER, s );
    }

    @Override
    public void debug(String s) {
        log.debug(MAKER, s);
    }

    @Override
    public void trace(String s) {
        log.trace(MAKER, s);
    }

    @Override
    public void warn(String s) {
        log.warn(MAKER, s);
    }
}
