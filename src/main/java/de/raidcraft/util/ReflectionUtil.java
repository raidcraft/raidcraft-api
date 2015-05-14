package de.raidcraft.util;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class ReflectionUtil {

    /**
     * When {@code Type} initialized with a value of an object, its fully qualified class name
     * will be prefixed with this.
     *
     * @see {@link ReflectionUtil#getClassName(Type)}
     */
    private static final String TYPE_CLASS_NAME_PREFIX = "class ";
    private static final String TYPE_INTERFACE_NAME_PREFIX = "interface ";

    /*
     *  Utility class with static access methods, no need for constructor.
     */
    private ReflectionUtil() {}

    /**
     * {@link Type#toString()} value is the fully qualified class name prefixed
     * with {@link ReflectionUtil#TYPE_CLASS_NAME_PREFIX}. This method will substring it, for it to be eligible
     * for {@link Class#forName(String)}.
     *
     * @param type the {@code Type} value whose class name is needed.
     * @return {@code String} class name of the invoked {@code type}.
     *
     * @see {@link ReflectionUtil#getClass()}
     */
    public static String getClassName(Type type) {
        if (type==null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_CLASS_NAME_PREFIX)) {
            className = className.substring(TYPE_CLASS_NAME_PREFIX.length());
        } else if (className.startsWith(TYPE_INTERFACE_NAME_PREFIX)) {
            className = className.substring(TYPE_INTERFACE_NAME_PREFIX.length());
        }
        return className;
    }

    /**
     * Returns the {@code Class} object associated with the given {@link Type}
     * depending on its fully qualified name.
     *
     * @param type the {@code Type} whose {@code Class} is needed.
     * @return the {@code Class} object for the class with the specified name.
     *
     * @throws ClassNotFoundException if the class cannot be located.
     *
     * @see {@link ReflectionUtil#getClassName(Type)}
     */
    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className==null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    /**
     * Creates a new instance of the class represented by this {@code Type} object.
     *
     * @param type the {@code Type} object whose its representing {@code Class} object
     * 		will be instantiated.
     * @return a newly allocated instance of the class represented by
     * 		the invoked {@code Type} object.
     *
     * @throws ClassNotFoundException if the class represented by this {@code Type} object
     * 			cannot be located.
     * @throws InstantiationException if this {@code Type} represents an abstract class,
     *             an interface, an array class, a primitive type, or void;
     *             or if the class has no nullary constructor;
     *             or if the instantiation fails for some other reason.
     * @throws IllegalAccessException if the class or its nullary constructor is not accessible.
     *
     * @see {@link Class#newInstance()}
     */
    public static Object newInstance(Type type)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> clazz = getClass(type);
        if (clazz==null) {
            return null;
        }
        return clazz.newInstance();
    }

    /**
     * Returns an array of {@code Type} objects representing the actual type
     * arguments to this object.
     * If the returned value is null, then this object represents a non-parameterized
     * object.
     *
     * @param object the {@code object} whose type arguments are needed.
     * @return an array of {@code Type} objects representing the actual type
     * 		arguments to this object.
     *
     * @see {@link Class#getGenericSuperclass()}
     * @see {@link ParameterizedType#getActualTypeArguments()}
     */
    public static Type[] getParameterizedTypes(Object object) {

        Type superclassType = object.getClass().getGenericSuperclass();
        if (!ParameterizedType.class.isAssignableFrom(superclassType.getClass())) {
            return null;
        }

        return ((ParameterizedType)superclassType).getActualTypeArguments();
    }

    /**
     * Checks whether a {@code Constructor} object with no parameter types is specified
     * by the invoked {@code Class} object or not.
     *
     * @param clazz the {@code Class} object whose constructors are checked.
     * @return {@code true} if a {@code Constructor} object with no parameter types is specified.
     * @throws SecurityException If a security manager, <i>s</i> is present and any of the
     *         following conditions is met:
     *			<ul>
     *             <li> invocation of
     *             {@link SecurityManager#checkMemberAccess
     *             s.checkMemberAccess(this, Member.PUBLIC)} denies
     *             access to the constructor
     *
     *             <li> the caller's class loader is not the same as or an
     *             ancestor of the class loader for the current class and
     *             invocation of {@link SecurityManager#checkPackageAccess
     *             s.checkPackageAccess()} denies access to the package
     *             of this class
     *         </ul>
     *
     * @see {@link Class#getConstructor(Class...)}
     */
    public static boolean hasDefaultConstructor(Class<?> clazz) throws SecurityException {
        Class<?>[] empty = {};
        try {
            clazz.getConstructor(empty);
        } catch (NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class for the field represented by the given {@code String name} parameter inside
     * the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared fields to be
     * 		checked for a certain field.
     * @param name the field name as {@code String} to be
     * 		compared with {@link Field#getName()}
     * @return the {@code Class} object representing the type of given field name.
     *
     * @see {@link Class#getDeclaredFields()}
     * @see {@link Field#getType()}
     */
    public static Class<?> getFieldClass(Class<?> clazz, String name) {
        if (clazz==null || name==null || name.isEmpty()) {
            return null;
        }

        Class<?> propertyClass = null;

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(name)) {
                propertyClass = field.getType();
                break;
            }
        }

        return propertyClass;
    }

    /**
     * Returns a {@code Class} object that identifies the
     * declared class as a return type for the method represented by the given
     * {@code String name} parameter inside the invoked {@code Class<?> clazz} parameter.
     *
     * @param clazz the {@code Class} object whose declared methods to be
     * 		checked for the wanted method name.
     * @param name the method name as {@code String} to be
     * 		compared with {@link Method#getName()}
     * @return the {@code Class} object representing the return type of the given method name.
     *
     * @see {@link Class#getDeclaredMethods()}
     * @see {@link Method#getReturnType()}
     */
    public static Class<?> getMethodReturnType(Class<?> clazz, String name) {
        if (clazz==null || name==null || name.isEmpty()) {
            return null;
        }

        name = name.toLowerCase();
        Class<?> returnType = null;

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(name)) {
                returnType = method.getReturnType();
                break;
            }
        }

        return returnType;
    }

    /**
     * Extracts the enum constant of the specified enum class with the
     * specified name. The name must match exactly an identifier used
     * to declare an enum constant in the given class.
     *
     * @param clazz the {@code Class} object of the enum type from which
     * 		to return a constant.
     * @param name the name of the constant to return.
     * @return the enum constant of the specified enum type with the
     *      specified name.
     *
     * @throws IllegalArgumentException if the specified enum type has
     *         no constant with the specified name, or the specified
     *         class object does not represent an enum type.
     *
     * @see {@link Enum#valueOf(Class, String)}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object getEnumConstant(Class<?> clazz, String name) {
        if (clazz==null || name==null || name.isEmpty()) {
            return null;
        }
        return Enum.valueOf((Class<Enum>)clazz, name);
    }

    private static void extractTypeArguments(Map<Type, Type> typeMap, Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return;
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
        Type[] typeParameter = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
        Type[] actualTypeArgument = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < typeParameter.length; i++) {
            if(typeMap.containsKey(actualTypeArgument[i])) {
                actualTypeArgument[i] = typeMap.get(actualTypeArgument[i]);
            }
            typeMap.put(typeParameter[i], actualTypeArgument[i]);
        }
    }

    public static Class<?> findSubClassParameterType(Object instance, Class<?> classOfInterest, int parameterIndex) {
        Map<Type, Type> typeMap = new HashMap<Type, Type>();
        Class<?> instanceClass = instance.getClass();
        while (classOfInterest != instanceClass.getSuperclass()) {
            extractTypeArguments(typeMap, instanceClass);
            instanceClass = instanceClass.getSuperclass();
            if (instanceClass == null) throw new IllegalArgumentException();
        }

        ParameterizedType parameterizedType = (ParameterizedType) instanceClass.getGenericSuperclass();
        Type actualType = parameterizedType.getActualTypeArguments()[parameterIndex];
        if (typeMap.containsKey(actualType)) {
            actualType = typeMap.get(actualType);
        }

        if (actualType instanceof Class) {
            return (Class<?>) actualType;
        } else if (actualType instanceof TypeVariable) {
            return browseNestedTypes(instance, (TypeVariable<?>) actualType);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static Class<?> browseNestedTypes(Object instance, TypeVariable<?> actualType) {
        Class<?> instanceClass = instance.getClass();
        List<Class<?>> nestedOuterTypes = new LinkedList<Class<?>>();
        for (
                Class<?> enclosingClass = instanceClass.getEnclosingClass();
                enclosingClass != null;
                enclosingClass = enclosingClass.getEnclosingClass()) {
            try {
                Field this$0 = instanceClass.getDeclaredField("this$0");
                Object outerInstance = this$0.get(instance);
                Class<?> outerClass = outerInstance.getClass();
                nestedOuterTypes.add(outerClass);
                Map<Type, Type> outerTypeMap = new HashMap<Type, Type>();
                extractTypeArguments(outerTypeMap, outerClass);
                for (Map.Entry<Type, Type> entry : outerTypeMap.entrySet()) {
                    if (!(entry.getKey() instanceof TypeVariable)) {
                        continue;
                    }
                    TypeVariable<?> foundType = (TypeVariable<?>) entry.getKey();
                    if (foundType.getName().equals(actualType.getName())
                            && isInnerClass(foundType.getGenericDeclaration(), actualType.getGenericDeclaration())) {
                        if (entry.getValue() instanceof Class) {
                            return (Class<?>) entry.getValue();
                        }
                        actualType = (TypeVariable<?>) entry.getValue();
                    }
                }
            } catch (NoSuchFieldException e) { /* this should never happen */ } catch (IllegalAccessException e) { /* this might happen */}

        }
        throw new IllegalArgumentException();
    }

    private static boolean isInnerClass(GenericDeclaration outerDeclaration, GenericDeclaration innerDeclaration) {
        if (!(outerDeclaration instanceof Class) || !(innerDeclaration instanceof Class)) {
            throw new IllegalArgumentException();
        }
        Class<?> outerClass = (Class<?>) outerDeclaration;
        Class<?> innerClass = (Class<?>) innerDeclaration;
        while ((innerClass = innerClass.getEnclosingClass()) != null) {
            if (innerClass == outerClass) {
                return true;
            }
        }
        return false;
    }

    public static Class<?> getNmsClass(String basePackage, String clazzName) {

        return getNmsClass(basePackage, "", clazzName);
    }

    public static Class<?> getNmsClass(String basePackage, String detailPackage, String clazzName) {

        try {
            String mcVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
            Class<?> clazz = Class.forName(basePackage + "." + mcVersion + "." + detailPackage
                    + (detailPackage == null || detailPackage.equals("") ? "" : ".") + clazzName);
            return clazz;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object getPrivateField(String fieldName, Class clazz, Object object) {

        Field field;
        Object o = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            o = field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return o;
    }
}