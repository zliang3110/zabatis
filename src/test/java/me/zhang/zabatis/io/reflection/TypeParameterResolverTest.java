package me.zhang.zabatis.io.reflection;

import org.junit.Test;
import me.zhangll.zabatis.reflection.TypeParameterResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class TypeParameterResolverTest {

    @Test
    public void paramTypeResolveTest() throws NoSuchMethodException {
        Method getAge = Student.class.getMethod("getAge");
        Method setAge = Student.class.getMethod("setAge", int.class);
        Method setHobbys = Student.class.getMethod("setHobbys", List.class);
        Method setParam = Student.class.getMethod("setParam", Object.class);

//        Method[] methods = Student.class.getMethods();
//        System.out.println(Arrays.toString(methods));
        Type[] type = TypeParameterResolver.resolveParamTypes(setParam, Student.class);
        System.out.println(Arrays.toString(type));
    }

}


class Student<T>{
    private String name;
    private int age;
    private List<String> hobbys;
    private T param;

    public T getParam() {
        return param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getHobbys() {
        return hobbys;
    }

    public void setHobbys(List<String> hobbys) {
        this.hobbys = hobbys;
    }



    public Student(String name, int age, List<String> hobbys) {
        this.name = name;
        this.age = age;
        this.hobbys = hobbys;
    }
}