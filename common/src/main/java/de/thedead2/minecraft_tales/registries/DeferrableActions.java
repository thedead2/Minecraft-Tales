package de.thedead2.minecraft_tales.registries;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.api.DeferrableAction;
import de.thedead2.minecraft_tales.player.MTPlayer;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;


public class DeferrableActions {
    private static final Registry<RegisteredAction<?>> ACTION_REGISTRY = new MappedRegistry<>(ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "deferrable_actions_registry")), Lifecycle.stable());

    public static <T> RegisteredAction<T> register(String id, Codec<T> argsCodec, DeferrableAction<T> action) {
        ResourceLocation resourceLocation = createId(id);
        return Registry.register(ACTION_REGISTRY, id, new RegisteredAction<>(resourceLocation, action, argsCodec));
    }

    private static ResourceLocation createId(String id) {
        return ResourceLocation.fromNamespaceAndPath(MTGlobalConstants.MOD_ID, "actions/" + id);
    }

    public static <T> RegisteredAction<T> getAction(String id) {
        return getAction(createId(id));
    }

    @SuppressWarnings("unchecked")
    public static <T> RegisteredAction<T> getAction(ResourceLocation id) {
        return (RegisteredAction<T>) ACTION_REGISTRY.get(id);
    }

    @SuppressWarnings("unchecked")
    public static <T> Codec<RegisteredAction<T>> getActionCodec() {
        return (Codec<RegisteredAction<T>>) (Object) ACTION_REGISTRY.byNameCodec();
    }

    public static <T> DeferrableActionInstance<T> getActionAsInstance(String id, T arg) {
        return new DeferrableActionInstance<>(getAction(id), arg);
    }

    public static <T> DeferrableActionInstance<T> getActionAsInstance(ResourceLocation id, T arg) {
        return new DeferrableActionInstance<>(getAction(id), arg);
    }


    public record RegisteredAction<T>(ResourceLocation id, DeferrableAction<T> action, Codec<T> argsCodec) implements DeferrableAction<T> {

        public boolean execute(MTPlayer player, T arg) {
            return this.action.execute(player, arg);
        }

        public DeferrableActionInstance<T> asInstance(T arg) {
            return new DeferrableActionInstance<>(this, arg);
        }
    }


    public static final class DeferrableActionInstance<T> {

        //Due to Compiler constrains this has to be Codec<DeferrableActionInstance<Object>> and can not be Codec<DeferrableActionInstance<?>>
        public static final Codec<DeferrableActionInstance<Object>> CODEC = getActionCodec().dispatch(
                DeferrableActionInstance::getAction,
                action -> RecordCodecBuilder.mapCodec(instance -> instance.group(
                        getActionCodec().fieldOf("action").forGetter(DeferrableActionInstance::getAction),
                        action.argsCodec().fieldOf("actionArg").forGetter(DeferrableActionInstance::getActionArg)
                ).apply(instance, DeferrableActionInstance::new))
        );

        private final RegisteredAction<T> deferrableAction;

        private final T actionArg;


        public DeferrableActionInstance(RegisteredAction<T> deferrableAction, T actionArg) {
            this.deferrableAction = deferrableAction;
            this.actionArg = actionArg;
        }


        public boolean execute(MTPlayer player) {
            return this.deferrableAction.execute(player, this.actionArg);
        }


        public RegisteredAction<T> getAction() {
            return this.deferrableAction;
        }


        public T getActionArg() {
            return this.actionArg;
        }
    }
}
