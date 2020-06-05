package me.zhangll.zabatis.reflection.factory;

import java.util.List;
import java.util.Properties;

public interface ObjectFactory {

    default void setProperties(Properties properties){

    };

    /**
     * 根据无参构造函数创建对象
     * @param type
     * @param <T>
     * @return
     */
    <T> T create(Class<T> type);

    /**
     * 指定构造参数创建对象
     * @param type
     * @param constructorArgTypes
     * @param constructorArgs
     * @param <T>
     * @return
     */
    <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

    <T> boolean isCollection(Class<T> type);
}
