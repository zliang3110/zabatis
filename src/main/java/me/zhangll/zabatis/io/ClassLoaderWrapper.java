package me.zhangll.zabatis.io;

import java.io.InputStream;
import java.net.URL;

public class ClassLoaderWrapper {

    ClassLoader defaultClassLoder;
    ClassLoader systemClassLoder;

    ClassLoaderWrapper() {
        this.systemClassLoder = ClassLoader.getSystemClassLoader();
    }

    public URL getResourceAsURL(String resource){
        return getResourceAsURL(resource, getClassLoaders(null));
    }

    public URL getResourceAsURL(String resource, ClassLoader[] classloader){

        URL url;

        for (ClassLoader cl : classloader){

            url = cl.getResource(resource);

            //有些classloader需要加/,如果没有找到，尝试加上/查找
            if (null != resource){
                url = cl.getResource("/" + resource);
            }

            if (null != url) return url;
        }

        return null;
    }

    public InputStream getResourceAsStream(String resource){
        return getResourceAsStream(resource, getClassLoaders(null));
    }

    public InputStream getResourceAsStream(String resource, ClassLoader classLoader){
        return getResourceAsStream(resource, getClassLoaders(classLoader));
    }

    InputStream getResourceAsStream(String resource, ClassLoader[] classLoader){
        for (ClassLoader cl : classLoader){
            if (null != cl){
                InputStream retrunVal = cl.getResourceAsStream(resource);

                if (null == retrunVal){
                    retrunVal = cl.getResourceAsStream("/" + resource);
                }

                if (null != retrunVal){
                    return retrunVal;
                }
            }
        }
        return null;
    }

    public Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    public Class<?> clasForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(classLoader));
    }

    Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {
        for (ClassLoader cl : classLoader){
            if (null != cl){
                try {
                    Class<?> clazz = Class.forName(name);
                } catch (ClassNotFoundException e) {
                    //在这里忽略异常,继续查找
                }
            }
        }

        throw new ClassNotFoundException("没有找到class: "+ name);
    }



    ClassLoader[] getClassLoaders(ClassLoader classLoader){
        return new ClassLoader[]{
                classLoader,
                defaultClassLoder,
                Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(),
                systemClassLoder
        };
    }
}
