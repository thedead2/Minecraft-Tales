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

import java.util.Objects;


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


    public static final class RegisteredAction<T> implements DeferrableAction<T> {

        private final ResourceLocation id;

        private final DeferrableAction<T> action;

        private final Codec<T> argsCodec;


        RegisteredAction(ResourceLocation id, DeferrableAction<T> action, Codec<T> argsCodec) {
            this.id = id;
            this.action = action;
            this.argsCodec = argsCodec;
        }


        public boolean execute(MTPlayer player, T arg) {
                return this.action.execute(player, arg);
            }


        public DeferrableActionInstance<T> asInstance(T arg) {
                return new DeferrableActionInstance<>(this, arg);
            }


        public ResourceLocation id() {
            return id;
        }


        public DeferrableAction<T> action() {
            return action;
        }


        public Codec<T> argsCodec() {
            return argsCodec;
        }


        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != this.getClass()) {
                return false;
            }
            var that = (RegisteredAction<?>) obj;
            return Objects.equals(this.id, that.id) &&
                    Objects.equals(this.action, that.action) &&
                    Objects.equals(this.argsCodec, that.argsCodec);
        }


        @Override
        public int hashCode() {
            return Objects.hash(id, action, argsCodec);
        }


        @Override
        public String toString() {
            return "RegisteredAction[" +
                    "id=" + id + ", " +
                    "action=" + action + ", " +
                    "argsCodec=" + argsCodec + ']';
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


        DeferrableActionInstance(RegisteredAction<T> deferrableAction, T actionArg) {
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
