package de.thedead2.minecraft_tales.event;

import com.google.common.reflect.ClassPath;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.api.GameSide;
import de.thedead2.minecraft_tales.event.types.MTEvent;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.Function;

import static java.lang.annotation.RetentionPolicy.RUNTIME;


public class MTEventBus {
    private final ConcurrentHashMap<Class<? extends MTEvent>, PriorityBlockingQueue<MTEventListener.RegisteredListener<? extends MTEvent>>> listeners = new ConcurrentHashMap<>();

    public MTEventBus() {
        try {
            ClassPath.from(this.getClass().getClassLoader())
                     .getAllClasses()
                     .stream()
                     .map(ClassPath.ClassInfo::load)
                     .forEach(clazz -> {
                        if(Modifier.isPublic(clazz.getModifiers()) && clazz.isAnnotationPresent(EvenBusSubscriber.class)) {
                            if(MTGlobalConstants.PLATFORM.getGameSide() == clazz.getDeclaredAnnotation(EvenBusSubscriber.class).value())
                                this.registerClass(clazz);
                        }
                    });
        }
        catch (IOException e) {
            MTGlobalConstants.LOGGER.error("Couldn't auto-register event listeners of classes with @MTEventBus.EvenBusSubscriber annotation!", e);
        }
    }


    public <T extends MTEvent> MTEventListener.RegisteredListener<T> registerListener(Class<T> eventType, MTEventListener<T> listener) {
        return registerListener(eventType, listener, MTEvent.Priority.NORMAL, GameSide.BOTH);
    }

    public <T extends MTEvent> MTEventListener.RegisteredListener<T> registerListener(Class<T> eventType, MTEventListener<T> listener, MTEvent.Priority priority, GameSide effectiveSide) {
        return addListener(eventType, type -> new MTEventListener.RegisteredListener<>(type, listener, priority, effectiveSide));
    }

    private <T extends MTEvent> MTEventListener.RegisteredListener<T> addListener(Class<T> eventType, Function<Class<T>, MTEventListener.RegisteredListener<T>> listenerFactory) {
        var listener = listenerFactory.apply(eventType);
        var queue = this.listeners.computeIfAbsent(eventType, c -> new PriorityBlockingQueue<>());

        if(!queue.contains(listener)) {
            queue.add(listener);
        }
        else {
            MTGlobalConstants.LOGGER.warn("Ignored duplicate event listener: {}", listener.toString());
        }

        return listener;
    }

    public <T extends MTEvent> boolean unregisterListener(Class<T> eventType, MTEventListener.RegisteredListener<T> listener) {
        if (!this.listeners.containsKey(eventType)) throw new IllegalArgumentException("Can't unregister event listener as there are no listeners for event:" + eventType.getName());

        return this.listeners.get(eventType).remove(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends MTEvent> boolean post(T event) {
        PriorityBlockingQueue<MTEventListener.RegisteredListener<? extends MTEvent>> listeners = this.listeners.getOrDefault(event.getClass(), new PriorityBlockingQueue<>());

        listeners.stream()
                 .filter(registeredListener -> registeredListener.effectiveSide() == GameSide.BOTH || registeredListener.effectiveSide() == MTGlobalConstants.PLATFORM.getGameSide())
                 .forEach(listener -> {
            try {
                ((MTEventListener.RegisteredListener<T>) listener).invoke(event);
            }
            catch (Throwable e) {
                MTGlobalConstants.LOGGER.error(String.valueOf(new MTEventBusError(e, event, listeners.toArray(MTEventListener.RegisteredListener<?>[]::new))));
            }
        });

        return event.isCanceled();
    }

    public List<MTEventListener.RegisteredListener<?>> register(final Object target) {
        if (target.getClass() == Class.class)
            return registerClass((Class<?>) target);
        else
            return registerObject(target);
    }


    private List<MTEventListener.RegisteredListener<?>> registerObject(final Object target) {
        Method[] methods = target.getClass().getMethods();
        List<MTEventListener.RegisteredListener<?>> listeners = new ArrayList<>();

        for (var method : methods) {
            if (Modifier.isStatic(method.getModifiers()))
                continue;
            if (method.isAnnotationPresent(MTEvent.Subscriber.class))
                listeners.add(registerInternal(target, method));
        }

        return listeners;
    }


    private List<MTEventListener.RegisteredListener<?>> registerClass(Class<?> target) {
        List<MTEventListener.RegisteredListener<?>> listeners = new ArrayList<>();
        for (var method : target.getMethods()) {
            if (!Modifier.isStatic(method.getModifiers()) || !method.isAnnotationPresent(MTEvent.Subscriber.class))
                continue;
            listeners.add(registerInternal(target, method));
        }

        return listeners;
    }

    @SuppressWarnings("unchecked")
    private <T extends MTEvent> MTEventListener.RegisteredListener<T> registerInternal(final Object target, final Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();

        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException(
                    "Method " + method + " has @MTEvent.Subscriber annotation. " +
                            "It has " + parameterTypes.length + " arguments, " +
                            "but event handler methods require a single argument only."
            );
        }

        Class<?> eventType = parameterTypes[0];

        if (!MTEvent.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(
                    "Method " + method + " has @MTEvent.Subscriber annotation, " +
                            "but takes an argument that is not an Event subtype : " + eventType);
        }

        if (!Modifier.isPublic(method.getModifiers()))
            throw new IllegalArgumentException("Failed to create EventListener for " + target.getClass().getName() + "." + method.getName() + " it is not public!");

        MTEventListener.RegisteredListener<T> listener = addListener((Class<T>) eventType, type -> new MTEventListener.DynamicRegisteredListener<>(type, target, method));

        MTGlobalConstants.LOGGER.debug("Registered event handler: {}.{}", target.getClass().getSimpleName(), method.getName());

        return listener;
    }


    private record MTEventBusError(Throwable throwable, MTEvent event, MTEventListener.RegisteredListener<?>[] listeners) {

        @Override
        public String toString() {
            StringBuilder buffer = new StringBuilder();
            buffer.append("Exception caught during firing event: ").append(throwable.getMessage()).append('\n')
                  .append("Event: ").append(event.getClass().getSimpleName()).append('\n')
                  .append("\tListeners:\n");
            for (int x = 0; x < listeners.length; x++) {
                MTEventListener.RegisteredListener<?> listener = listeners[x];
                buffer.append("\t\t").append(x).append(": ").append(listener.listener().listenerName()).append(" --> Priority: ").append(listener.priority()).append(" (Side: ").append(listener.effectiveSide()).append(")").append('\n');
            }

            final StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            buffer.append(sw.getBuffer());

            return buffer.toString();
        }
    }

    @Retention(value = RUNTIME)
    @Target(ElementType.TYPE)
    public @interface EvenBusSubscriber {
        GameSide value() default GameSide.BOTH;
    }
}
