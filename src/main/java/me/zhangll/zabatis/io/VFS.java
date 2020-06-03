package me.zhangll.zabatis.io;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 提供了一套简单的访问应用服务器资源的API
 */
public abstract class VFS {

    /** 内置实现类 */
    public static final Class<?>[] IMPLEMENTIONS = {DefaultVFS.class};

    /** 客户自定义实现类，通过{@link #addImplClass(Class)}添加*/
    public static final List<Class<? extends VFS>> USER_IMPLEMENTIONS = new ArrayList<>();


    private static class VFSHolder{
        static final VFS INSTANCE = createVFS();

        static VFS createVFS() {
            List<Class<? extends VFS>> impls = new ArrayList<>();
            impls.addAll(USER_IMPLEMENTIONS);
            impls.addAll(Arrays.asList((Class<? extends VFS>[])IMPLEMENTIONS));

            VFS vfs= null;

            for (int i = 0; vfs == null||!vfs.isVaild(); i++){
                Class<? extends VFS> impl = impls.get(i);

                try {
                    vfs = impl.getDeclaredConstructor().newInstance();

                    if (!vfs.isVaild()){
                        System.out.println("VFS实现类在这个环境无效");
                    }

                } catch (NoSuchMethodException
                        | IllegalAccessException
                        | InstantiationException
                        | InvocationTargetException e) {

                    e.printStackTrace();
                }
            }

            return vfs;
        }
    }

    public static VFS getInstance(){
        return VFSHolder.INSTANCE;
    }

    public static void addImplClass(Class<? extends VFS> clazz){
        if (null != clazz) USER_IMPLEMENTIONS.add(clazz);
    }

    protected static Class<?> getClass(String className){
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found : " + className);
            e.printStackTrace();
        }
        return null;
    }

    protected static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes){
        try {
            return clazz == null? null : clazz.getMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            System.out.println("Method not found : " + clazz.getName()+ "." + methodName);
            e.printStackTrace();
        }

        return null;
    }

    protected static <T> T invoke(Method method, Object obj, Object... parameters){
        try {
            return (T) method.invoke(obj,parameters);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }


    protected static List<URL> getResources(String path) throws IOException {
        return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
    }

    public List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<>();
        for (URL url : getResources(path)){
            names.addAll(list(url, path));
        }

        return names;
    }

    protected abstract List<String> list(URL url, String forPath) throws IOException;

    abstract boolean isVaild();

}
