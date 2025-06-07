package de.thedead2.minecraft_tales.data.story.variables;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;

import javax.annotation.Nullable;
import java.util.UUID;


public record SavableVariable<T>(UUID uuid, SavableVariableType<T> type, T value) {

    @SuppressWarnings("unchecked")
    public static <T> Codec<SavableVariable<T>> codec() {
        return SavableVariableType.getCodec().dispatch(
                SavableVariable::type,
                type ->
                        RecordCodecBuilder.mapCodec(instance -> instance.group(
                                UUIDUtil.CODEC.fieldOf("id").forGetter(SavableVariable::uuid),
                                SavableVariableType.getCodec().fieldOf("type").forGetter(SavableVariable::type),
                                ((SavableVariableType<T>) type).codec().fieldOf("value").forGetter(SavableVariable::value)
                        ).apply(instance, ((uuid, savableVariableType, t) -> new SavableVariable<>(uuid, (SavableVariableType<T>) savableVariableType, t)))
                )
        );
    }

    public boolean valueEqual(@Nullable SavableVariable<?> other) {
        return other != null && value.equals(other.value);
    }
}
