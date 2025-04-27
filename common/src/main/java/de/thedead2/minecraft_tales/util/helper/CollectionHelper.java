package de.thedead2.minecraft_tales.util.helper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@MethodsReturnNonnullByDefault
public class CollectionHelper {

    private CollectionHelper() {}

    public static <T, R, V> HashMap<R, V> convertMapKeys(Map<T, V> oldMap, Function<T, R> keyConverter) {
        return convertMapKeys(oldMap, Maps::newHashMapWithExpectedSize, keyConverter);
    }


    public static <T, R, V, U extends Map<R, V>> U convertMapKeys(Map<T, V> oldMap, @Nonnull IntFunction<U> mapFactory, Function<T, R> keyConverter) {
        U newMap = mapFactory.apply(oldMap.size());

        oldMap.forEach((t, v) -> {
            R r = keyConverter.apply(t);
            newMap.put(r, v);
        });

        return newMap;
    }


    public static <T, R, V> HashMap<T, V> convertMapValues(Map<T, R> oldMap, Function<R, V> valueConverter) {
        return convertMapValues(oldMap, Maps::newHashMapWithExpectedSize, valueConverter);
    }


    public static <T, R, V, U extends Map<T, V>> U convertMapValues(Map<T, R> oldMap, @Nonnull IntFunction<U> mapFactory, Function<R, V> valueConverter) {
        U newMap = mapFactory.apply(oldMap.size());

        oldMap.forEach((t, r) -> {
            V v = valueConverter.apply(r);
            newMap.put(t, v);
        });

        return newMap;
    }


    public static <T, R, V, W> HashMap<T, R> convertMap(Map<V, W> oldMap, Function<V, T> keyConverter, Function<W, R> valueConverter) {
        return convertMap(oldMap, Maps::newHashMapWithExpectedSize, keyConverter, valueConverter);
    }


    public static <T, R, V, W, X extends Map<T, R>> X convertMap(Map<V, W> oldMap, @Nonnull IntFunction<X> mapFactory, Function<V, T> keyConverter, Function<W, R> valueConverter) {
        X newMap = mapFactory.apply(oldMap.size());

        oldMap.forEach((v, w) -> {
            T t = keyConverter.apply(v);
            R r = valueConverter.apply(w);

            newMap.put(t, r);
        });

        return newMap;
    }


    public static <T, V> List<V> convertCollection(Collection<T> oldCollection, Function<T, V> valueConverter) {
        return convertCollection(oldCollection, Lists::newArrayListWithExpectedSize, valueConverter);
    }


    public static <T, V, R extends Collection<V>> R convertCollection(Collection<T> oldCollection, @Nonnull IntFunction<R> collectionFactory, Function<T, V> valueConverter) {
        R newCollection = collectionFactory.apply(oldCollection.size());

        oldCollection.forEach(t -> {
            V v = valueConverter.apply(t);
            newCollection.add(v);
        });

        return newCollection;
    }


    public static <T, V extends Collection<T>> V filterCollection(V collection, Predicate<T> filter) {
        collection.removeIf(t -> !filter.test(t));

        return collection;
    }


    public static <T, V, R extends Map<T, V>> R filterMap(R map, BiPredicate<T, V> filter) {
        map.entrySet().removeIf(entry -> !filter.test(entry.getKey(), entry.getValue()));

        return map;
    }


    public static <T> Optional<T> findObjectWithHighestCount(Collection<T> collection) {
        Map<T, Long> objectGroups = collection.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return objectGroups.entrySet()
                           .stream()
                           .sorted(Map.Entry.<T, Long>comparingByValue().reversed())
                           .limit(1)
                           .map(Map.Entry::getKey)
                           .findFirst();
    }


    @SafeVarargs
    public static <T> Collection<T> concatenate(Collection<T> targetCollection, Collection<T>... collections) {
        for(Collection<T> collection : collections) {
            targetCollection.addAll(collection);
        }

        return targetCollection;
    }


    @SafeVarargs
    public static <T, V> Map<T, V> concatenate(Map<T, V> targetMap, Map<T, V>... maps) {
        for(Map<T, V> map : maps) {
            targetMap.putAll(map);
        }

        return targetMap;
    }
}
