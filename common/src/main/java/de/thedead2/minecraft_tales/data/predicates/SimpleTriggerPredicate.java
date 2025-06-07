package de.thedead2.minecraft_tales.data.predicates;

@FunctionalInterface
public interface SimpleTriggerPredicate<T> extends TriggerPredicate<T, Void> {

    boolean matches(T t);

    default boolean matches(T t, Void ignored) {
        return matches(t);
    }
}
