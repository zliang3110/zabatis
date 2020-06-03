package me.zhangll.zabatis.logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class LogFactory {

    public static final String MARKER = "ZABATIS";

    private static Constructor<? extends Log> logConstructor;

    static {
        tryImplementation(LogFactory::userLo4j2Logging);
    }


    private LogFactory(){
        //不能实例化
    }

    public static Log getLog(String logger){
        try {
            return logConstructor.newInstance(logger);
        } catch (Throwable e) {
            throw new LogException("创建logger失败");
        }
    }

    public static void tryImplementation(Runnable runnable){
        if (null == logConstructor){
            try {
                runnable.run();
            }catch (Throwable e){
                //忽略异常
            }

        }
    }

    public static synchronized void userLo4j2Logging(){
        setImplementation(me.zhangll.zabatis.logging.Log4j2.Log4j2Impl.class);

    }

    private static void setImplementation(Class<? extends Log> implClass){
        try {
            Constructor<? extends Log> candidate = implClass.getConstructor(String.class);
            Log log = candidate.newInstance(LogFactory.class.getName());

            if (log.isDebugEnable()){
                log.debug("Logging 初始化: "+ implClass);
            }

            logConstructor = candidate;

        } catch (Throwable e) {
            throw new LogException("Logging初始化失败",e);
        }
    }


}
