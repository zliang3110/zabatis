package me.zhangll.zabatis.reflection.property;

import me.zhangll.zabatis.reflection.Reflector;

import java.lang.reflect.Field;

public class PropertyCopier {

    private PropertyCopier()
    {

    }

    /**
     * 浅拷贝
     * @param type
     * @param sourceBean
     * @param destinationBean
     */
    public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean)
    {
        Class<?> parent = type;

        while (parent != null)
        {
            final Field[] fields = parent.getDeclaredFields();
            for(Field field : fields)
            {
                try {
                    try {
                        field.set(destinationBean, field.get(sourceBean));
                    } catch (IllegalAccessException e) {
                        if (Reflector.canControlMemberAccessible()) {
                            field.setAccessible(true);
                            field.set(destinationBean, field.get(sourceBean));
                        }
                        else {
                            throw e;
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            parent = parent.getSuperclass();
        }
    }
}
