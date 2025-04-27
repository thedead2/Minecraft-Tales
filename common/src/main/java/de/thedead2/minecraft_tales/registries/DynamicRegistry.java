package de.thedead2.minecraft_tales.registries;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.util.helper.IOHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;


public class DynamicRegistry<T> implements Iterable<T> {

    public static <R> Codec<DynamicRegistry<R>> codec(Codec<R> elementsCodec) {
        return RecordCodecBuilder.create((instance) -> instance.group(
                RegistryKeys.getResourceKeyCodec(elementsCodec).fieldOf("registryKey").forGetter(DynamicRegistry::key),
                Codec.INT.fieldOf("size").forGetter(DynamicRegistry::size),
                Reference.codec(elementsCodec).listOf().fieldOf("entries").forGetter(reg -> reg.byId)
        ).apply(instance, (key, size, entries) -> {
            if(size != entries.size()) {
                throw new IllegalStateException("Registry size mismatch!");
            }
            DynamicRegistry<R> reg = new DynamicRegistry<>(key, elementsCodec);

            reg.registerReferences(entries);

            return reg;
        }));
    }

    private final ResourceKey<DynamicRegistry<T>> key;
    private final List<Reference<T>> byId = new ArrayList<>();
    private final Map<ResourceLocation, Reference<T>> byLocation = new HashMap<>(); //FIXME: Use ConcurrentHashMap
    private final Map<MTRegistryKey<T>, Reference<T>> byKey = new HashMap<>();
    private final Map<T, Reference<T>> byValue = new IdentityHashMap<>();

    private final Codec<T> elementsCodec;
    private final Path savePath; // modId/dynamic_registries/name_of_registry/

    private boolean frozen;


    public DynamicRegistry(ResourceKey<DynamicRegistry<T>> key, Codec<T> elementsCodec) {
        this.key = key;
        this.savePath = MTGlobalConstants.SAVE_DIR.resolve(key.location().getPath());
        this.elementsCodec = elementsCodec;
    }

    public ResourceKey<DynamicRegistry<T>> key() {
        return this.key;
    }

    @Override
    public String toString() {
        return "DynamicRegistry[" + this.key + "]";
    }

    private void validateWrite(MTRegistryKey<T> key) {
        if (this.frozen) {
            throw new IllegalStateException("Registry is frozen (trying to add key " + key + ")!");
        }
    }

    private void registerReferences(Collection<Reference<T>> references) {
        for (Reference<T> reference : references) {
            if (reference.key == null || reference.value == null) throw new IllegalArgumentException("Cannot register reference with unknown key or value!");

            this.register(reference.key, reference.value);
        }
    }


    public @NotNull Reference<T> register(@NotNull MTRegistryKey<T> key, @NotNull T value) {
        this.validateWrite(key);
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);

        if (this.byLocation.containsKey(key.key())) {
            throw new IllegalArgumentException("Trying to add duplicate key '" + key + "' to registry!");
        }

        if (this.byValue.containsKey(value)) {
            throw new IllegalArgumentException("Trying to add duplicate value '" + value + "' to registry!");
        }

        Reference<T> reference = this.byKey.computeIfAbsent(key, MTRegistryKey -> new Reference<>(this.key, MTRegistryKey));

        this.byLocation.put(key.key(), reference);
        this.byValue.put(value, reference);
        this.byId.add(reference);

        return reference;
    }

    /**
     * @return the name used to identify the given object within this registry or {@code null} if the object is not within this registry
     */
    @Nullable
    public ResourceLocation getKey(T value) {
        Reference<T> reference = this.byValue.get(value);
        return reference != null ? reference.key().key() : null;
    }


    @Nullable
    public MTRegistryKey<T> getResourceKey(T value) {
        Reference<T> reference = this.byValue.get(value);
        return reference != null ? reference.key() : null;
    }

    @Nullable
    public T get(@Nullable MTRegistryKey<T> key) { //TODO: Maybe optional or Exception
        return getValueFromNullable(this.byKey.get(key));
    }

    @Nullable
    public Reference<T> getReference(ResourceLocation location) {
        return this.byLocation.get(location);
    }


    @Nullable
    public Reference<T> getReference(MTRegistryKey<T> key) {
        return this.byKey.get(key);
    }

    @NotNull
    public Reference<T> wrapAsReference(T value) {
        Reference<T> reference = this.byValue.get(value);
        return reference != null ? reference : Reference.direct(this.key, value);
    }

    Reference<T> getOrCreateReference(MTRegistryKey<T> key) {
        return this.byKey.computeIfAbsent(key, MTRegistryKey -> {
            this.validateWrite(MTRegistryKey);

            return new Reference<>(this.key, MTRegistryKey);
        });
    }


    public int size() {
        return this.byId.size();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return Iterators.transform(this.byId.iterator(), Reference::value);
    }

    @Nullable
    public T get(@Nullable ResourceLocation location) { //TODO: Maybe optional or Exception
        Reference<T> reference = this.byLocation.get(location);
        return getValueFromNullable(reference);
    }

    @Nullable
    private static <T> T getValueFromNullable(@Nullable Reference<T> reference) {
        return reference != null ? reference.value() : null;
    }

    public Set<ResourceLocation> keySet() {
        return Collections.unmodifiableSet(this.byLocation.keySet());
    }

    public Set<MTRegistryKey<T>> resourceKeySet() {
        return Collections.unmodifiableSet(this.byKey.keySet());
    }

    public Set<Map.Entry<MTRegistryKey<T>, T>> entrySet() {
        return Collections.unmodifiableSet(Maps.transformValues(this.byKey, Reference::value).entrySet());
    }


    public Stream<Reference<T>> referencesStream() {
        return this.byId.stream();
    }


    public boolean isEmpty() {
        return this.byKey.isEmpty();
    }


    public boolean containsKey(ResourceLocation location) {
        return this.byLocation.containsKey(location);
    }


    public boolean containsKey(MTRegistryKey<T> key) {
        return this.byKey.containsKey(key);
    }


    public DynamicRegistry<T> freeze() {
        if(this.frozen) {
            throw new IllegalStateException("Registry is already frozen!");
        }

        this.frozen = true;
        this.byValue.forEach((value, reference) -> reference.bindValue(value));
        List<ResourceLocation> list = this.byKey
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isBound())
                .map(entry -> entry.getKey().key())
                .sorted()
                .toList();

        if (!list.isEmpty()) {
            throw new IllegalStateException("Couldn't bind following values in registry " + this.getSimpleName() + ": " + list);
        }

        return this;
    }

    public DynamicRegistry<T> unfreeze() {
        if(!this.frozen) {
            throw new IllegalStateException("Registry is not frozen!");
        }

        this.frozen = false;
        this.byValue.forEach((value, reference) -> reference.unbindValue(value));

        return this;
    }


    public void saveContentsToDisk() {
        for (Reference<T> reference : this.byId) {
            Path filePath = this.savePath.resolve(reference.key().key().getPath());

            try {
                IOHelper.saveToFile(reference.value(), this.elementsCodec, filePath, false);
            }
            catch (IOException e) {
                MTGlobalConstants.LOGGER.error("Couldn't save {} to {}", reference.key().key().getPath(), filePath);
            }
        }
    }

    public void loadContentsFromDisk() {
        Map<MTRegistryKey<T>, T> contents = new HashMap<>();

        IOHelper.readDirectory(this.savePath.toFile(), file -> {
            try {
                Optional<T> val = IOHelper.loadFromFile(this.elementsCodec, file.toPath(), false);

                val.ifPresent(t -> {
                    MTRegistryKey<T> key = MTRegistryKey.fromSavePath(this.savePath, file.toPath());

                    contents.put(key, t);
                });
            }
            catch (IOException e) {
                MTGlobalConstants.LOGGER.error("Couldn't load {} from disk", file.getPath());
            }
        });

        this.unfreezeForAction(reg -> contents.forEach(reg::register));
    }

    /**
     * @throws IllegalStateException if this Registry is not dynamic
     * */
    public void unfreezeForAction(Consumer<DynamicRegistry<T>> action) {
        boolean frozen = this.isFrozen();

        if (frozen) {
            this.unfreeze();
        }

        action.accept(this);

        if (frozen) {
            this.freeze();
        }
    }


    public boolean isFrozen() {
        return this.frozen;
    }


    public static <T> Optional<DynamicRegistry<T>> loadFromDisk(Path savePath, Codec<T> elementsCodec) throws IOException {
        Codec<DynamicRegistry<T>> codec = DynamicRegistry.codec(elementsCodec);

        return IOHelper.loadFromFile(codec, savePath, true);
    }

    public Path saveToDisk() throws IOException {
        Codec<DynamicRegistry<T>> codec = this.getRegistryCodec();

        IOHelper.saveToFile(this, codec, this.savePath, true);

        return this.savePath;
    }

    public Codec<DynamicRegistry<T>> getRegistryCodec() {
        return codec(this.elementsCodec);
    }

    public String getSimpleName() {
        return this.key().location().toString().replace(":", "_") + "_registry";
    }


    public Codec<T> getElementsCodec() {
        return elementsCodec;
    }


    public static class Reference<T> {

        public static <R> Codec<Reference<R>> codec(Codec<R> elementsCodec) {
            return RecordCodecBuilder.create((instance) -> instance.group(
                    RegistryKeys.getResourceKeyCodec(elementsCodec).fieldOf("registry").forGetter(Reference::getRegistryKey),
                    MTRegistryKey.codec(elementsCodec).optionalFieldOf("key").forGetter(ref -> Optional.ofNullable(ref.key)),
                    elementsCodec.optionalFieldOf("value").forGetter(ref -> Optional.ofNullable(ref.value))
                    ).apply(instance, (registry, key, value) -> {
                        Reference<R> ref;
                        if(key.isPresent()) {
                            ref = new Reference<>(registry, key.get());

                            value.ifPresent(val -> ref.value = val);
                        }
                        else ref = new Reference<>(registry, value.orElseThrow());

                        return ref;
            }));
        }

        private final ResourceKey<DynamicRegistry<T>> owner;

        @Nullable
        private MTRegistryKey<T> key;

        @Nullable
        private T value;


        Reference(ResourceKey<DynamicRegistry<T>> owner, @Nullable MTRegistryKey<T> key) {
            this.owner = owner;
            this.key = key;
        }

        Reference(ResourceKey<DynamicRegistry<T>> owner, @Nullable T value) {
            this.owner = owner;
            this.value = value;
        }


        public static <T> Reference<T> direct(ResourceKey<DynamicRegistry<T>> owner, T value) {
            return new Reference<>(owner, value) {
                @Override
                public boolean isBound() {
                    return true;
                }
            };
        }


        public @NotNull MTRegistryKey<T> key() {
            if (this.key == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.value + "' from registry " + this.owner);
            }
            else {
                return this.key;
            }
        }


        public @NotNull T value() {
            if (this.value == null) {
                throw new IllegalStateException("Trying to access unbound value '" + this.key + "' from registry " + this.owner);
            }
            else {
                return this.value;
            }
        }


        public boolean is(@NotNull ResourceLocation location) {
            return this.key().key().equals(location);
        }


        public boolean is(@NotNull MTRegistryKey<T> key) {
            return this.key().equals(key);
        }


        public boolean is(Predicate<MTRegistryKey<T>> predicate) {
            return predicate.test(this.key());
        }


        public boolean isBound() {
            return this.value != null;
        }


        protected void bindValue(T value) {
            if (this.value != null) {
                throw new IllegalStateException("Can't bind value to key " + this.key + " as it is already bound!");
            }
            else {
                this.value = value;
            }
        }


        public String toString() {
            return "Reference{" + this.key + " = " + this.value + "}";
        }


        protected void unbindValue(T value) {
            if (this.value == null) {
                throw new IllegalStateException("Can't unbind value of key " + this.key + " as it is already unbound!");
            }
            else if (!this.value.equals(value)) {
                throw new IllegalArgumentException("Can't unbind value of key " + this.key + " as it is not " + value);
            }
            else {
                this.value = null;
            }
        }

        protected ResourceKey<DynamicRegistry<T>> getRegistryKey() {
            return this.owner;
        }
    }
}
