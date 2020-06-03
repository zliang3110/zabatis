package reflection;


import reflection.invoker.AmbiguousMethodInvoker;
import reflection.invoker.Invoker;
import reflection.invoker.MethodInvoker;
import reflection.property.PropertyNamer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.*;

/**
 * 这个类代表类定义信息的缓存，
 * 允许方便的将属性名和setter/getter方法之间映射
 */
public class Refelector {

    //类
    private final Class<?> type;
    private final String[] readablePropertyNames;
    private final String[] writablePeopertyNames;
    private final Map<String, Invoker> setMethods = new HashMap<>();
    private final Map<String, Invoker> getMethods = new HashMap<>();
    private final Map<String, Class<?>> setTypes = new HashMap<>();
    private final Map<String, Class<?>> getTypes = new HashMap<>();
    private Constructor<?> defaultConstructor;

    private Map<String, String> caseInsensitivePropertyMap = new HashMap<>();

    public Reflector(Class<?> clazz)
    {
        addDefaultConstructor(clazz);
        addGetMethods(clazz);
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
}
