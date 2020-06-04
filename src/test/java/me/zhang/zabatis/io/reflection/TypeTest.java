package me.zhang.zabatis.io.reflection;

import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class TypeTest<T> {
    String str;
    T[] t;
    List<String> list;
    Map<String, String> map;

    @Test
    public void typeTst() throws NoSuchFieldException {
        System.out.println("String str------------");
        Field field1 =  TypeTest.class.getDeclaredField("str");
        System.out.println("field1.getType(): " + field1.getType());
        System.out.println("field1.getGenericType(): "+ field1.getGenericType());

        System.out.println("T[] t ----------------");
        Field field2 =  TypeTest.class.getDeclaredField("t");
        System.out.println("field2.getType(): "+ field2.getType());
        Type type2 = field2.getGenericType();
        System.out.println("type2.getClass().getSimpleName(): " + type2.getClass().getSimpleName());

        System.out.println("List<String> list; ----------------");
        Field field3 =  TypeTest.class.getDeclaredField("list");
        ParameterizedType type3 = (ParameterizedType) field3.getGenericType();
        System.out.println("field3.getType(): " + field3.getType());
        System.out.println("type3.getClass().getSimpleName(): " + type3.getClass().getSimpleName());
        System.out.println("type3.getActualTypeArguments(): " + type3.getActualTypeArguments()[0]);
        System.out.println("type3.getOwnerType(): " + type3.getOwnerType());
        System.out.println("type3.getRawType(): " + type3.getRawType());

    }
}
