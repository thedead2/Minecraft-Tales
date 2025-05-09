package de.thedead2.minecraft_tales.util.misc;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Multimap;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;


public class HashBiSetMultiMap<R, V> {

    private final BiMap<R, Set<V>> map;


    public HashBiSetMultiMap(Map<R, Set<V>> map) {
        this();
        this.putAll(map);
    }


    public HashBiSetMultiMap() {
        this(HashBiMap.create());
    }

    
    public void putAll(Map<R, Set<V>> map) {
        this.map.putAll(map);
    }


    public HashBiSetMultiMap(BiMap<R, Set<V>> map) {
        this.map = HashBiMap.create(map);
    }


    public int size() {
        return this.map.size();
    }


    public boolean isEmpty() {
        return this.map.isEmpty();
    }


    public boolean containsKey(R key) {
        return this.map.containsKey(key);
    }


    public boolean containsValue(V value) {
        boolean flag = false;
        for(Collection<V> vs : map.values()) {
            if(vs.contains(value)) {
                flag = true;
                break;
            }
        }
        return flag;
    }


    public boolean containsEntry(@Nullable R key, @Nullable V value) {
        boolean flag = false;
        for(Map.Entry<R, Set<V>> entry : this.entrySet()) {
            flag = entry.getKey().equals(key) && entry.getValue().contains(value);
            if(flag) {
                break;
            }
        }
        return flag;
    }


    public Set<Map.Entry<R, Set<V>>> entrySet() {
        return this.map.entrySet();
    }


    public boolean containsEntry(@Nullable R key, @Nullable Set<V> value) {
        boolean flag = false;
        for(Map.Entry<R, Set<V>> entry : this.entrySet()) {
            flag = entry.getKey().equals(key) && entry.getValue().equals(value);
            if(flag) {
                break;
            }
        }
        return flag;
    }


    public @Nonnull Set<V> get(R key) {
        return this.map.get(key);
    }


    
    public boolean put(R key, V value) {
        Collection<V> vs = this.map.get(key);
        return Objects.requireNonNullElseGet(vs, () -> this.map.computeIfAbsent(key, r -> new HashSet<>())).add(value);
    }


    
    public Set<V> remove(R key) {
        return this.map.remove(key);
    }


    @Nullable
    
    public Set<V> forcePut(R key, Set<V> value) {
        return this.map.forcePut(key, value);
    }


    public void clear() {
        this.map.clear();
    }


    @Nonnull
    public Set<R> keySet() {
        return this.map.keySet();
    }


    public Set<V> getOrDefault(R key, Set<V> defaultValue) {
        return this.map.getOrDefault(key, defaultValue);
    }


    public void forEach(BiConsumer<R, Set<V>> action) {
        this.map.forEach(action);
    }


    public @Nonnull Map<R, Set<V>> asMap() {
        return new HashMap<>(this.map);
    }


    public void replaceAll(BiFunction<R, Set<V>, Set<V>> function) {
        this.map.replaceAll(function);
    }


    @Nullable
    
    public Collection<V> putIfAbsent(R key, V value) {
        Set<V> vs = this.map.get(key);
        if(vs == null) {
            vs = new HashSet<>();
        }
        if(vs.add(value)) {
            return null;
        }
        return this.map.putIfAbsent(key, vs);
    }


    
    public boolean remove(R key, Set<V> value) {
        return this.map.remove(key, value);
    }


    
    public boolean putAll(R key, @Nonnull Iterable<V> values) {
        Set<V> set = new HashSet<>();
        values.forEach(set::add);
        return this.map.put(key, set) != null;
    }


    
    public boolean putAll(Multimap<R, V> multimap) {
        boolean flag = false;
        for(R r : multimap.keySet()) {
            Set<V> values = new HashSet<>(multimap.get(r));
            flag = this.map.put(r, values) != null;
        }
        return flag;
    }


    
    public @Nonnull Set<V> replaceValues(R key, @Nonnull Iterable<V> values) {
        Set<V> vs = new HashSet<>();
        values.forEach(vs::add);
        var set = this.map.replace(key, vs);
        return set != null ? set : new HashSet<>();
    }


    
    public @Nonnull Set<V> removeAll(@Nullable R key) {
        return this.map.remove(key);
    }


    
    public boolean replace(R key, Set<V> oldValue, Set<V> newValue) {
        return this.map.replace(key, oldValue, newValue);
    }


    
    public boolean replace(R key, V value) {
        var vs = this.map.get(key);
        if(vs != null) {
            return vs.add(value);
        }
        return false;
    }


    public Set<V> computeIfAbsent(R key, @Nonnull Function<R, Set<V>> mappingFunction) {
        return this.map.computeIfAbsent(key, mappingFunction);
    }


    public Set<V> computeIfPresent(R key, @Nonnull BiFunction<R, Set<V>, Set<V>> remappingFunction) {
        return this.map.computeIfPresent(key, remappingFunction);
    }


    public Set<V> compute(R key, @Nonnull BiFunction<R, Set<V>, Set<V>> remappingFunction) {
        return this.map.compute(key, remappingFunction);
    }


    public Set<V> merge(R key, @Nonnull Set<V> value, @Nonnull BiFunction<Set<V>, Set<V>, Set<V>> remappingFunction) {
        return this.map.merge(key, value, remappingFunction);
    }


    public BiMap<V, R> inverse() {
        BiMap<V, R> map = HashBiMap.create();
        this.map.forEach((r, vs) -> vs.forEach(v -> map.put(v, r)));
        return map;
    }
}
