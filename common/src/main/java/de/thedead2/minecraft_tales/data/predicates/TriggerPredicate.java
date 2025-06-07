package de.thedead2.minecraft_tales.data.predicates;

@FunctionalInterface
public interface TriggerPredicate<T, V> {
    boolean matches(T t, V addArg);
}
