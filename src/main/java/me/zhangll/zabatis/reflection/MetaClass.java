package me.zhangll.zabatis.reflection;

import me.zhangll.zabatis.reflection.property.PropertyTokenizer;

public class MetaClass {

    private final ReflectorFactory reflectorFactory;
    private final Reflector reflector;

    private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
        this.reflector = reflectorFactory.findForClass(type);
    }

    public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory){
        return new MetaClass(type, reflectorFactory);
    }

    public MetaClass metaClassForProperty(String name){
        Class<?> propType = reflector.getGetterType(name);
        return MetaClass.forClass(propType, reflectorFactory);
    }

    public String findProperty(String name){
        StringBuilder prop = buildProterty(name, new StringBuilder());
        return prop.length() > 0 ? prop.toString() : null;
    }

    public String findProperty(String name, boolean useCamelCaseMapping){
        if (useCamelCaseMapping){
            name = name.replace("_", "");
        }

        return findProperty(name);
    }

    public String[] getGetterNames(){
        return reflector.getGetablePropertyNames();
    }

    public String[] getSetterNames(){
        return reflector.getSetablePropertyNames();
    }

    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            MetaClass metaProp = metaClassForProperty(prop.getName());
            return metaProp.getSetterType(prop.getChilden());
        } else {
            return reflector.getSetterType(prop.getName());
        }
    }

//    public Class<?> getGetterType(String name) {
//        PropertyTokenizer prop = new PropertyTokenizer(name);
//        if (prop.hasNext()) {
//            MetaClass metaProp = metaClassForProperty(prop);
//            return metaProp.getGetterType(prop.getChilden());
//        }
//        // issue #506. Resolve the type inside a Collection Object
//        return getGetterType(prop);
//    }



    private StringBuilder buildProterty(String name, StringBuilder builder) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()){
            String propertyName = reflector.findPropertyName(prop.getName());
            if (null != propertyName){
                builder.append(propertyName);
                builder.append(".");
                MetaClass metaProp = metaClassForProperty(propertyName);
                metaProp.buildProterty(prop.getChilden(), builder);
            }
        }else {
            String propertyName = reflector.findPropertyName(name);
            if (propertyName != null){
                builder.append(propertyName);
            }
        }
        return builder;
    }
}
