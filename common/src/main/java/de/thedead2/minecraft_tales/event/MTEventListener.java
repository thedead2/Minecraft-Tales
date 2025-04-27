package de.thedead2.minecraft_tales.event;

import de.thedead2.minecraft_tales.api.GameSide;
import de.thedead2.minecraft_tales.event.types.MTEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;


@FunctionalInterface
public interface MTEventListener<T extends MTEvent> {

    void invoke(T event);

    default String listenerName() {
        return this.getClass().getName();
    }


    class RegisteredListener<T extends MTEvent> implements MTEventListener<T>, Comparable<RegisteredListener<T>> {

        protected final Class<T> eventType;

        protected final MTEventListener<T> listener;

        protected final MTEvent.Priority priority;

        protected final GameSide effectiveSide;


        public RegisteredListener(Class<T> eventType, MTEventListener<T> listener, MTEvent.Priority priority, GameSide effectiveSide) {
            this.eventType = eventType;
            this.listener = listener;
            this.priority = priority;
            this.effectiveSide = effectiveSide;
        }

        @Override
        public void invoke(T event) {
                this.listener.invoke(event);
            }


        public Class<T> eventType() {
            return eventType;
        }


        public MTEventListener<T> listener() {
            return listener;
        }


        public MTEvent.Priority priority() {
            return priority;
        }


        public GameSide effectiveSide() {
            return effectiveSide;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (RegisteredListener<?>) obj;
            return Objects.equals(this.eventType, that.eventType) &&
                    Objects.equals(this.listener, that.listener) &&
                    Objects.equals(this.priority, that.priority);
        }


        @Override
        public int hashCode() {
            return Objects.hash(eventType, listener, priority);
        }


        @Override
        public String toString() {
            return "RegisteredListener[" +
                    "eventType=" + eventType + ", " +
                    "listener=" + listener + ", " +
                    "priority=" + priority + ']';
        }


        @Override
        public int compareTo(@NotNull MTEventListener.RegisteredListener<T> o) {
            return Integer.compare(o.priority().ordinal(), this.priority().ordinal());
        }
    }


    final class DynamicRegisteredListener<T extends MTEvent> extends RegisteredListener<T> {

        private final Object target;
        private final Method method;

        public DynamicRegisteredListener(Class<T> eventType, Object target, Method method) {
            super(eventType, t -> {
                try {
                    method.invoke(target.getClass() == Class.class ? null : target, t);
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }},
                  method.getAnnotation(MTEvent.Subscriber.class).priority(),
                  method.getAnnotation(MTEvent.Subscriber.class).effectiveSide()
            );

            this.target = target;
            this.method = method;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DynamicRegisteredListener<?> that)) return false;
            return Objects.equals(target, that.target) && Objects.equals(method, that.method);
        }

        @Override
        public int hashCode() {
            return Objects.hash(target, method);
        }


        @Override
        public String toString() {
            return "DynamicRegisteredListener{" +
                    "target=" + target +
                    ", method=" + method +
                    ", eventType=" + eventType +
                    ", listener=" + listener +
                    ", priority=" + priority +
                    '}';
        }
    }
}
