package me.zhangll.zabatis.reflection.wrapper;

import me.zhangll.zabatis.reflection.MetaObject;
import me.zhangll.zabatis.reflection.factory.ObjectFactory;
import me.zhangll.zabatis.reflection.property.PropertyTokenizer;

import java.util.List;

public interface ObjectWrapper {

    Object get(PropertyTokenizer prop);

    void set(PropertyTokenizer prop, Object value);

    String findProperty(String name, boolean useCamelCaseMapping);

    String[] getGetterNames();

    String[] getSetterNames();

    Class<?> getGetterTypes();

    Class<?> getSetterTypes();

    boolean hasSetter(String name);

    boolean hasGetter(String name);

    MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

    boolean isCollection();

    void add(Object element);

    <E> void addAll(List<E> element);
}
