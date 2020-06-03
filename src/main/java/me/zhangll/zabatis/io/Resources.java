package me.zhangll.zabatis.io;

import java.nio.charset.Charset;

/**
 * 通过classloader访问资源
 */
public class Resources {

    private static ClassLoaderWrapper classLoaderWrapper
            = new ClassLoaderWrapper();

    private static Charset charset;



    public static Class<?> classForName(String name) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(name);
    }

    public static Charset getCharset() {
        return charset;
    }

    public static void setCharset(Charset charset) {
        Resources.charset = charset;
    }
}
