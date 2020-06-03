package me.zhangll.zabatis.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class DefaultVFS extends VFS {



    private static final byte[] JAR_MAGIC = {'P', 'K', 3, 4};

    @Override
    protected List<String> list(URL url, String forPath) throws IOException {
        return null;
    }

    protected String getPackagePath(String packageName){
        return packageName == null? null : packageName.replaceAll(".", "/");
    }

    protected boolean isJar(URL url, byte[] buffer){

        try (InputStream in = url.openStream()){

            in.read(buffer, 0, JAR_MAGIC.length);
            if (Arrays.equals(buffer, JAR_MAGIC)){
                return true;
            }
        } catch (IOException e) {
            //忽略
            System.out.println("如果到这说明不是jar");
        }
        return false;
    }

    @Override
    boolean isVaild() {
        return true;
    }
}
