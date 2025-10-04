package net.pwing.itemattributes.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ReflectionUtils {

    public static List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotation, boolean searchSupers, boolean ignoreAccess) {
        List<Class<?>> classes = (searchSupers ? getAllSuperclassesAndInterfaces(clazz) : new ArrayList<>());
        classes.add(0, clazz);
        List<Method> annotatedMethods = new ArrayList<>();
        for (Class<?> aclazz : classes) {
            Method[] methods = (ignoreAccess ? aclazz.getDeclaredMethods() : aclazz.getMethods());
            for (Method method : methods) {
                if (method.getAnnotation(annotation) != null) {
                    annotatedMethods.add(method);
                }
            }
        }
        
        return annotatedMethods;
    }

    private static List<Class<?>> getAllSuperclassesAndInterfaces(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        List<Class<?>> allSuperClassesAndInterfaces = new ArrayList<>();
        List<Class<?>> allSuperclasses = getAllSuperclasses(clazz);
        int superClassIndex = 0;
        List<Class<?>> allInterfaces = getAllInterfaces(clazz);
        int interfaceIndex = 0;
        while (interfaceIndex < allInterfaces.size() || superClassIndex < allSuperclasses.size()) {
            Class<?> aclazz;
            if (interfaceIndex >= allInterfaces.size()) {
                aclazz = allSuperclasses.get(superClassIndex++);
            } else if ((superClassIndex >= allSuperclasses.size()) || !(superClassIndex < interfaceIndex)) {
                aclazz = allInterfaces.get(interfaceIndex++);
            } else {
                aclazz = allSuperclasses.get(superClassIndex++);
            }

            allSuperClassesAndInterfaces.add(aclazz);
        }

        return allSuperClassesAndInterfaces;
    }

    public static List<Class<?>> getAllSuperclasses(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        
        List<Class<?>> classes = new ArrayList<>();
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null) {
            classes.add(superclass);
            superclass = superclass.getSuperclass();
        }
        
        return classes;
    }

    public static List<Class<?>> getAllInterfaces(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        Set<Class<?>> interfacesFound = new LinkedHashSet<>();
        getAllInterfaces(clazz, interfacesFound);

        return new ArrayList<>(interfacesFound);
    }

    private static void getAllInterfaces(Class<?> clazz, Set<Class<?>> interfacesFound) {
        while (clazz != null) {
            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> i : interfaces) {
                if (interfacesFound.add(i)) {
                    getAllInterfaces(i, interfacesFound);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }
}
