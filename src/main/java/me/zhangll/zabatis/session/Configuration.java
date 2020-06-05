package me.zhangll.zabatis.session;

import me.zhangll.zabatis.type.TypeHandlerRegistry;

public class Configuration {
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return null;
    }

    public boolean isUseColumnLabel() {
        return false;
    }

    public boolean isLazyLoadingEnabled() {
        return false;
    }

    public boolean isUseActualParamName() {
        return false;
    }
}
