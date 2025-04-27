package de.thedead2.minecraft_tales.util.helper;

import com.google.common.reflect.ClassPath;
import net.minecraft.MethodsReturnNonnullByDefault;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


@MethodsReturnNonnullByDefault
public class ReflectionHelper {

    private ReflectionHelper() {}

    public static Set<Class<?>> findClassesInPackage(String packageName) {
        return findClassesInPackage(packageName, ClassLoader.getSystemClassLoader());
    }


    public static Set<Class<?>> findClassesInPackage(String packageName, ClassLoader classLoader) {
        try {
            return ClassPath.from(classLoader)
                            .getAllClasses()
                            .stream()
                            .filter(clazz -> clazz.getPackageName().equalsIgnoreCase(packageName))
                            .map(classInfo -> {
                                try {
                                    return classLoader.loadClass(classInfo.getName());
                                }
                                catch(ClassNotFoundException e) {
                                    return classInfo.load();
                                }
                            })
                            .collect(Collectors.toSet());
        }
        catch(IOException e) {
            return Collections.emptySet();
        }
    }


    public static InputStream findResourceAsStream(String path) {
        InputStream stream = ReflectionHelper.class.getClassLoader().getResourceAsStream(path);
        if(stream == null) {
            stream = InputStream.nullInputStream();
        }
        return stream;
    }

    public static URL findResource(String path) {
        URL url = ReflectionHelper.class.getClassLoader().getResource(path);
        if(url == null) {
            throw new IllegalStateException("Could not find resource: " + path);
        }
        return url;
    }


    @SuppressWarnings("unchecked")
    public static <T> Class<T> changeClassLoader(Class<T> aClass, ClassLoader classLoader) {
        try {
            return (Class<T>) classLoader.loadClass(aClass.getName());
        }
        catch(ClassNotFoundException e) {
            return aClass;
        }
    }


    public static Collection<Class<?>> findMatchingClasses(Class<?> baseClass) {
        return findClassesInPackage(baseClass.getPackageName())
                .stream()
                .filter(aClass -> checkForSuperClassOrInterface(aClass, baseClass))
                .collect(Collectors.toSet());
    }


    private static boolean checkForSuperClassOrInterface(Class<?> aClass, Class<?> baseClass) {
        Class<?> superClass = aClass.getSuperclass();
        if(superClass == null || !superClass.getName().equals(baseClass.getName())) {
            if(!Modifier.isAbstract(aClass.getModifiers())) {
                if(Arrays.stream(aClass.getInterfaces()).noneMatch(aClass1 -> aClass1.getName().equals(baseClass.getName()))) {
                    if(superClass != null) {
                        return checkForSuperClassOrInterface(superClass, baseClass);
                    }
                }
                return Arrays.stream(aClass.getInterfaces()).anyMatch(aClass1 -> aClass1.getName().equals(baseClass.getName()));
            }
            return false;
        }
        return true;
    }


    public static Class<?> findClassWithName(String className) {
        try {
            return ReflectionHelper.class.getClassLoader().loadClass(className);
        }
        catch(ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
