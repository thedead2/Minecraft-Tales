package de.thedead2.minecraft_tales.data.predicates;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Function;


public class MinMax<T extends Number & Comparable<T>> {

    public static final MinMax<Integer> ANY_INT = new MinMax<>(null, null, i -> i * i);
    public static final MinMax<Double> ANY_DOUBLE = new MinMax<>(null, null, i -> i * i);


    @Nullable
    private final T min;

    @Nullable
    private final T max;

    @Nullable
    private final T minSq;

    @Nullable
    private final T maxSq;

    private final Comparator<T> comparator;
    private final Function<T, T> squared;


    private MinMax(@Nullable T min, @Nullable T max, Function<T, T> squared) {
        this.min = min;
        this.max = max;
        this.comparator = Comparator.naturalOrder();
        this.squared = squared;
        this.minSq = min == null ? null : squared.apply(min);
        this.maxSq = max == null ? null : squared.apply(max);
    }

    public boolean matches(T value) {
        if (min != null && comparator.compare(value, min) < 0) return false;
        return max == null || comparator.compare(value, max) <= 0;
    }

    public boolean matchesSqr(T value) {
        T squared = this.squared.apply(value);
        if (minSq != null && comparator.compare(squared, minSq) < 0) return false;
        return maxSq == null || comparator.compare(squared, maxSq) <= 0;
    }


    @Nullable
    public T getMin() {
        return this.min;
    }


    @Nullable
    public T getMax() {
        return this.max;
    }


    @Nullable
    public T getMinSq() {
        return minSq;
    }


    @Nullable
    public T getMaxSq() {
        return maxSq;
    }


    public boolean isAny() {
        return this.min == null && this.max == null;
    }


    public static <T extends Number & Comparable<T>> MinMax<T> exactly(T value, Function<T, T> squared) {
        return new MinMax<>(value, value, squared);
    }

    public static <T extends Number & Comparable<T>> MinMax<T> between(T min, T max, Function<T, T> squared) {
        return new MinMax<>(min, max, squared);
    }

    public static <T extends Number & Comparable<T>> MinMax<T> atLeast(T min, Function<T, T> squared) {
        return new MinMax<>(min, null, squared);
    }

    public static <T extends Number & Comparable<T>> MinMax<T> atMost(T max, Function<T, T> squared) {
        return new MinMax<>(null, max, squared);
    }
}