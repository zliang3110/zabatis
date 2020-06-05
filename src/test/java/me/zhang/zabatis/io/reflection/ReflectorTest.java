package me.zhang.zabatis.io.reflection;

import org.junit.Test;
import me.zhangll.zabatis.reflection.Reflector;

import java.lang.reflect.InvocationTargetException;

public class ReflectorTest {

    @Test
    public void test() throws InvocationTargetException, IllegalAccessException {
        Reflector reflector = new Reflector(Student.class);
        Student student = new Student("zhangsan", 10, null);
        String propName = reflector.findPropertyName("age");
        Integer[] age = {20};
        reflector.getSetInvoker(propName).invoke(student, age);
        assert (Integer) reflector.getGetInvoker(propName).invoke(student, null) == 20;
    }
}
