package me.zhangll.zabatis.reflection.invoker;

import me.zhangll.zabatis.reflection.Reflector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;


/**
 * 封装Field.set()方法
 */
public class SetFieldInvoker implements Invoker {

    private final Field field;

    public SetFieldInvoker(Field field) {
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
        try {
            field.set(target, args[0]);
        }catch (IllegalAccessException e){
            if (Reflector.canControlMemberAccessible())
            {
                field.setAccessible(true);
                field.set(target, args[0]);
            }else {
                throw  e;
            }
        }
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
