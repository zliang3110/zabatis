package reflection;


import reflection.invoker.*;
import reflection.property.PropertyNamer;

import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.*;

/**
 * 这个类代表类定义信息的缓存，
 * 允许方便的将属性名和setter/getter方法之间映射
 */
public class Reflector {

    //类
    private final Class<?> type;
    private final String[] readablePropertyNames;
    private final String[] writablePropertyNames;
    private final Map<String, Invoker> setMethods = new HashMap<>();
    private final Map<String, Invoker> getMethods = new HashMap<>();
    private final Map<String, Class<?>> setTypes = new HashMap<>();
    private final Map<String, Class<?>> getTypes = new HashMap<>();
    private Constructor<?> defaultConstructor;

    private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

    public Reflector(Class<?> clazz)
    {
        type = clazz;
        addDefaultConstructor(clazz);
        addGetMethods(clazz);
        addSetMethods(clazz);
        addFields(clazz);
        readablePropertyNames = getMethods.keySet().toArray(new String[0]);
        writablePropertyNames = setMethods.keySet().toArray(new String[0]);

        for (String propName : readablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
        for (String propName : writablePropertyNames) {
            caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
        }
    }


    private void addFields(Class<?> clazz)
    {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields)
        {
            if (!setMethods.containsKey(field.getName()))
            {
                int modifiers = field.getModifiers();
                if (!(Modifier.isFinal(modifiers) && Modifier.isStatic(modifiers)))
                {
                    addSetField(field); //自动添加set方法
                }
            }
            if (!getMethods.containsKey(field.getName()))
            {
                addGetField(field);
            }
        }
        if (clazz.getSuperclass() != null){
            addFields(clazz.getSuperclass());
        }
    }

    private void addSetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            setMethods.put(field.getName(), new SetFieldInvoker(field));
            Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
            setTypes.put(field.getName(), typeToClass(fieldType));
        }
    }

    private void addGetField(Field field) {
        if (isValidPropertyName(field.getName())) {
            getMethods.put(field.getName(), new GetFieldInvoker(field));
            Type fieldType = TypeParameterResolver.resolveFieldType(field, type);
            getTypes.put(field.getName(), typeToClass(fieldType));
        }
    }

    /**
     * 获取无参构造方法赋值给defaultConstructor
     * @param clazz
     */
    private void addDefaultConstructor(Class<?> clazz)
    {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Arrays.stream(constructors).filter(constructor -> constructor.getParameterTypes().length ==0)
                .findAny().ifPresent(constructor -> this.defaultConstructor = constructor);
    }


    /**
     * 把所有的getter方法放到getMethods中
     * @param clazz
     */
    private void addGetMethods(Class<?> clazz)
    {
        Map<String, List<Method>> conflictingGetter = new HashMap<>();
        Method[] methods = getClassMethods(clazz);
        Arrays.stream(methods).filter(m -> m.getParameterTypes().length == 0 && PropertyNamer.isGetter(m.getName()))
                .forEach(m -> addMethodConflict(conflictingGetter, PropertyNamer.methodToProperty(m.getName()), m));
        resolveGetterConflicts(conflictingGetter);
    }

    /**
     * 如果同时存在isxx或getxx 取其中一个
     * @param conflictingGetter
     */
    private void resolveGetterConflicts(Map<String, List<Method>> conflictingGetter) {
        for (Map.Entry<String, List<Method>> entry : conflictingGetter.entrySet()){
            Method winner = null;
            String propName = entry.getKey();
            boolean isAmbiguous = false;

            for (Method candidate : entry.getValue())
            {
                if (winner == null)
                {
                    winner = candidate;
                    continue;
                }

                Class<?> winnerType = winner.getReturnType();
                Class<?> candidateType = candidate.getReturnType();
                if (candidateType.equals(winnerType)) //如果返回类型相同
                {
                    if (!boolean.class.equals(winner))//并且返回类型不是boolean
                    {
                        isAmbiguous = true;
                        break;
                    }else if (candidate.getName().startsWith("is"))//如果是方法名是isxxx
                    {
                        winner = candidate;
                    }
                }else if (candidateType.isAssignableFrom(winnerType))
                {
                    //什么也不做
                }else if (winnerType.isAssignableFrom(candidateType)) //如果winnerType是
                {
                    winner = candidate;
                }else {
                    isAmbiguous = true;
                    break;
                }
            }
            addGetMethod(propName, winner, isAmbiguous);
        }

    }

    private void addGetMethod(String propName, Method method, boolean isAmbiguous) {
        MethodInvoker invoker = isAmbiguous ?
                new AmbiguousMethodInvoker(method
                        , MessageFormat.format(
                        "Illegal overloaded getter method with ambiguous type for property ''{0}'' in class ''{1}''. This breaks the JavaBeans specification and can cause unpredictable results.",
                        propName, method.getDeclaringClass().getName()))
                :new MethodInvoker(method);
    }

    private void addMethodConflict(Map<String, List<Method>> conflictingMethod, String name, Method method) {

        if (isValidPropertyName(name))
        {
            List<Method> list = conflictingMethod.computeIfAbsent(name, k -> new ArrayList<>());
            list.add(method);
        }
    }

    public void addSetMethods(Class<?> clazz){
        Map<String, List<Method>> conflictingSetters = new HashMap<>();
        Method[] methods = getClassMethods(clazz);
        Arrays.stream(methods).filter(m -> m.getParameterTypes().length == 1 && PropertyNamer.isSetter(m.getName()))
                .forEach(m -> addMethodConflict(conflictingSetters, PropertyNamer.methodToProperty(m.getName()), m));
        resolveSetterConflicts(conflictingSetters);

    }

    private void resolveSetterConflicts(Map<String, List<Method>> conflictingSetters) {
        for (Map.Entry<String, List<Method>> entry : conflictingSetters.entrySet())
        {
            String propName = entry.getKey();
            List<Method> setters = entry.getValue();
            Class<?> getterType = getTypes.get(propName);
            boolean isGetterAmbiguous =getMethods.get(propName) instanceof AmbiguousMethodInvoker;
            boolean isSetterAmbiguous = false;
            Method match = null;
            for (Method setter : setters)
            {
                if (!isGetterAmbiguous && setter.getParameterTypes()[0].equals(getterType))
                {
                    match = setter;
                    break;
                }
                if (!isSetterAmbiguous)
                {
                    match = pickBetterSetter(match, setter, propName);
                    isSetterAmbiguous = match == null;
                }

            }
            if (match != null)
            {
                addSetMethod(propName, match);
            }
        }
    }

    private void addSetMethod(String propName, Method match) {
        MethodInvoker invoker = new MethodInvoker(match);
        setMethods.put(propName, invoker);
        Type[] paramTypes = TypeParameterResolver.resolveParamTypes(match, type);
        setTypes.put(propName, typeToClass(paramTypes[0]));
    }

    private Method pickBetterSetter(Method setter1, Method setter2, String property) {
        if (setter1 == null)
        {
            return setter2;
        }
        Class<?> paramType1 = setter1.getParameterTypes()[0];
        Class<?> paramType2 = setter2.getParameterTypes()[0];
        if (paramType1.isAssignableFrom(paramType2))
        {
            return setter2;
        }else if (paramType2.isAssignableFrom(paramType1))
        {
            return setter1;
        }

        //如果走到这，说明setter
        MethodInvoker invoker = new AmbiguousMethodInvoker(setter1,
                MessageFormat.format(
                        "Ambiguous setters defined for property ''{0}'' in class ''{1}'' with types ''{2}'' and ''{3}''.",
                        property, setter2.getDeclaringClass().getName(), paramType1.getName(), paramType2.getName()));

        setMethods.put(property, invoker);
        Type[] paramTypes = TypeParameterResolver.resolveParamTypes(setter1, type);
        setTypes.put(property, typeToClass(paramTypes[0]));
        return null;
    }

    private Class<?> typeToClass(Type src) {
        Class<?> result = null;
        if (src instanceof Class) {
            result = (Class<?>) src;
        } else if (src instanceof ParameterizedType) {
            result = (Class<?>) ((ParameterizedType) src).getRawType();
        } else if (src instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) src).getGenericComponentType();
            if (componentType instanceof Class) {
                result = Array.newInstance((Class<?>) componentType, 0).getClass();
            } else {
                Class<?> componentClass = typeToClass(componentType);
                result = Array.newInstance(componentClass, 0).getClass();
            }
        }
        if (result == null) {
            result = Object.class;
        }
        return result;
    }


    /**
     * name不以$开头，不是serialVersionUID，不是class
     * @param name
     * @return
     */
    private boolean isValidPropertyName(String name)
    {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    /**
     * 获取类上所有的方法
     * @param clazz
     * @return
     */
    private Method[] getClassMethods(Class<?> clazz) {
        Map<String, Method> uniqueMethods = new HashMap<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class){
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> aninterface : interfaces)
            {
                addUniqueMethods(uniqueMethods, aninterface.getMethods());
            }

            currentClass = currentClass.getSuperclass();
        }

        Collection<Method> methods = uniqueMethods.values();

        return methods.toArray(new Method[0]);
    }


    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods){
        for (Method currentMethod : methods)
        {
            //桥接方法是 JDK 1.5 引入泛型后，为了使Java的泛型方法生成的字节码和 1.5 版本前的字节码相兼容，由编译器自动生成的方法
            if (!currentMethod.isBridge())
            {
                String signature = getSignature(currentMethod);

                if (!uniqueMethods.containsKey(signature))
                {
                    uniqueMethods.put(signature, currentMethod);
                }
            }


        }
    }


    /**
     * String doSomeThing(String action,String time) --> String#doSomeThing:String,String
     * @param currentMethod
     * @return
     */
    private String getSignature(Method currentMethod) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = currentMethod.getReturnType();
        if (returnType != null)
        {
            sb.append(returnType.getName()).append("#");
        }
        sb.append(currentMethod.getName());
        Class<?>[] parameters = currentMethod.getParameterTypes();
        for (int i = 0; i < parameters.length; i++){
            sb.append(i == 0 ? ":" : ",").append(parameters[i].getName());
        }
        return sb.toString();
    }

    public static boolean canControlMemberAccessible()
    {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager)
            {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        }catch (SecurityException e)
        {
            return false;
        }
        return true;
    }

    /**
     * Gets the name of the class the instance provides information for.
     *
     * @return The class name
     */
    public Class<?> getType() {
        return type;
    }

    public Constructor<?> getDefaultConstructor() {
        if (defaultConstructor != null) {
            return defaultConstructor;
        } else {
            throw new ReflectionException("There is no default constructor for " + type);
        }
    }

    public boolean hasDefaultConstructor() {
        return defaultConstructor != null;
    }

    public Invoker getSetInvoker(String propertyName) {
        Invoker method = setMethods.get(propertyName);
        if (method == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    public Invoker getGetInvoker(String propertyName) {
        Invoker method = getMethods.get(propertyName);
        if (method == null) {
            throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return method;
    }

    /**
     * Gets the type for a property setter.
     *
     * @param propertyName - the name of the property
     * @return The Class of the property setter
     */
    public Class<?> getSetterType(String propertyName) {
        Class<?> clazz = setTypes.get(propertyName);
        if (clazz == null) {
            throw new ReflectionException("There is no setter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /**
     * Gets the type for a property getter.
     *
     * @param propertyName - the name of the property
     * @return The Class of the property getter
     */
    public Class<?> getGetterType(String propertyName) {
        Class<?> clazz = getTypes.get(propertyName);
        if (clazz == null) {
            throw new ReflectionException("There is no getter for property named '" + propertyName + "' in '" + type + "'");
        }
        return clazz;
    }

    /**
     * Gets an array of the readable properties for an object.
     *
     * @return The array
     */
    public String[] getGetablePropertyNames() {
        return readablePropertyNames;
    }

    /**
     * Gets an array of the writable properties for an object.
     *
     * @return The array
     */
    public String[] getSetablePropertyNames() {
        return writablePropertyNames;
    }

    /**
     * Check to see if a class has a writable property by name.
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a writable property by the name
     */
    public boolean hasSetter(String propertyName) {
        return setMethods.containsKey(propertyName);
    }

    /**
     * Check to see if a class has a readable property by name.
     *
     * @param propertyName - the name of the property to check
     * @return True if the object has a readable property by the name
     */
    public boolean hasGetter(String propertyName) {
        return getMethods.containsKey(propertyName);
    }

    public String findPropertyName(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }
}
