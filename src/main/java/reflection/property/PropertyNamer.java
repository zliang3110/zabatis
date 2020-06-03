package reflection.property;

import reflection.ReflectionException;

import java.util.Locale;

public class PropertyNamer {
    private PropertyNamer()
    {

    }

    /**
     * 根据set,get获取属性名
     * @param name
     * @return
     */
    public static String methodToProperty(String name) {

        //判断是否已'is'、'get'、'set'开头
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set"))
        {
            name = name.substring(3);
        }else
        {
            throw new ReflectionException("获取属性名失败: " + name +", 没有以'is', 'get', 'set'开头" );
        }

        //首字母转换成小霞
        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))){
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }


    /**
     *判断方式是否是setter或getter方法
     * @param name
     * @return
     */
    public static boolean isProperty(String name)
    {
        return isGetter(name) || isSetter(name);
    }

    /**
     * 判断是否是get方法
     * @param name
     * @return
     */
    public static boolean isGetter(String name)
    {
        return (name.startsWith("get") && name.length() > 3) || (name.startsWith("is") && name.length() > 2);
    }

    public static boolean isSetter(String name)
    {
        return name.startsWith("set") && name.length() > 3;
    }


}
