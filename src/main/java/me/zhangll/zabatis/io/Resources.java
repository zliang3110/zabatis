package me.zhangll.zabatis.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.File;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;


/**
 * 通过classloader访问资源
 */
public class Resources {

    private static ClassLoaderWrapper classLoaderWrapper
            = new ClassLoaderWrapper();

    private static Charset charset;

    public Resources() {
    }

    public static Class<?> classForName(String name) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(name);
    }

    /**
     * 可能为null
     * @return
     */
    public static ClassLoader getDefaultClassLoader(){
        return classLoaderWrapper.defaultClassLoder;
    }

    public static void setDefaultClassLoader(ClassLoader defaultClassLoader){
        classLoaderWrapper.defaultClassLoder = defaultClassLoader;
    }


    public static URL getResourceURL(String resource) throws IOException {
        return getResourceURL(resource, null);
    }

    public static URL getResourceURL(String resource, ClassLoader classLoader) throws IOException {
        URL url = classLoaderWrapper.getResourceAsURL(resource, classLoader);
        if (null == url){
            throw new IOException("Could not find resource: " + resource);
        }
        return url;
    }

    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(resource, null);
    }


    public static InputStream getResourceAsStream(String resource, ClassLoader classLoader) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, classLoader);

        if (null == in){
            throw new IOException("Could not find resource: " + resource);
        }

        return in;
    }


    public static Properties getResourceProperties(String resource) throws IOException {
        return getResourceAsProperties(resource, null);
    }

    public static Properties getResourceAsProperties(String resource, ClassLoader classLoader) throws IOException {
        Properties props = new Properties();

        try (InputStream in = getResourceAsStream(resource, classLoader)){
            props.load(in);
        }
        return props;
    }



    public static Reader getResourceAsReader(String resource) throws IOException {
        return getResourceAsReader(resource, null);
    }

    public static Reader getResourceAsReader(String resource, ClassLoader classLoader) throws IOException {

        Reader reader = charset == null?new InputStreamReader(getResourceAsStream(resource, classLoader))
                : new InputStreamReader(getResourceAsStream(resource, classLoader), charset);

        return reader;
    }


    public static File getResourceAsFile(String resource, ClassLoader classLoader) throws IOException {
        return new File(getResourceURL(resource, classLoader).getFile());
    }

    public static File getResourceAsFile(String resource) throws IOException {
        return getResourceAsFile(resource, null);
    }

    public static InputStream getUrlAsSteam(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }

    public static Reader getUrlAsReader(String urlString) throws IOException {
        return charset == null?new InputStreamReader(getUrlAsSteam(urlString))
                :new InputStreamReader(getUrlAsSteam(urlString), charset);
    }

    public static Properties getUrlAsProperties(String UrlString) throws IOException {
        Properties props = new Properties();
        try(InputStream in = getUrlAsSteam(UrlString)){
            props.load(in);
        }

        return props;
    }


    public static Charset getCharset() {
        return charset;
    }

    public static void setCharset(Charset charset) {
        Resources.charset = charset;
    }
}
