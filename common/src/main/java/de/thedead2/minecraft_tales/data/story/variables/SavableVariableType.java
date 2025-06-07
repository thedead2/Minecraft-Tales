package de.thedead2.minecraft_tales.data.story.variables;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;


public final class SavableVariableType<T> {

    static final Registry<SavableVariableType<?>> VARIABLE_TYPES = new MappedRegistry<>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "variable_types_reg")), Lifecycle.stable());

    public static final SavableVariableType<Integer> INT_TYPE = register("int", Codec.INT);

    public static final SavableVariableType<Long> LONG_TYPE = register("long", Codec.LONG);

    public static final SavableVariableType<Double> DOUBLE_TYPE = register("double", Codec.DOUBLE);

    public static final SavableVariableType<String> STRING_TYPE = register("string", Codec.STRING);

    public static final SavableVariableType<Float> FLOAT_TYPE = register("float", Codec.FLOAT);

    public static final SavableVariableType<Byte> BYTE_TYPE = register("byte", Codec.BYTE);

    public static final SavableVariableType<Short> SHORT_TYPE = register("short", Codec.SHORT);

    public static final SavableVariableType<Boolean> BOOLEAN_TYPE = register("boolean", Codec.BOOL);



    private final Codec<T> codec;


    private SavableVariableType(Codec<T> codec) {
        this.codec = codec;
    }


    public static <T> SavableVariableType<T> register(String id, Codec<T> codec) {
        return Registry.register(VARIABLE_TYPES, ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "variable_types/" + id), new SavableVariableType<>(codec));
    }


    public static Codec<SavableVariableType<?>> getCodec() {
        return VARIABLE_TYPES.byNameCodec();
    }


    public Codec<T> codec() {
        return codec;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (SavableVariableType<?>) obj;
        return Objects.equals(this.codec, that.codec);
    }


    @Override
    public int hashCode() {
        return Objects.hash(codec);
    }


    @Override
    public String toString() {
        return "SavableVariableType[" +
                "codec=" + codec + ']';
    }
}
