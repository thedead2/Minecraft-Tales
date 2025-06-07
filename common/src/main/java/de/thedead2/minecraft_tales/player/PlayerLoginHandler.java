package de.thedead2.minecraft_tales.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import de.thedead2.minecraft_tales.registries.DeferrableActions;
import de.thedead2.minecraft_tales.util.helper.CollectionHelper;
import de.thedead2.minecraft_tales.util.helper.IOHelper;
import net.minecraft.core.UUIDUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;


public class PlayerLoginHandler {

    @SuppressWarnings("unchecked")
    private static final UnboundedMapCodec<UUID, List<DeferrableActions.DeferrableActionInstance<?>>> mapCodec = (UnboundedMapCodec<UUID, List<DeferrableActions.DeferrableActionInstance<?>>>) (Object) Codec.unboundedMap(UUIDUtil.CODEC, DeferrableActions.DeferrableActionInstance.CODEC.listOf());

    private final Map<UUID, Queue<DeferrableActions.DeferrableActionInstance<?>>> deferredActions;

    PlayerLoginHandler() {
        this.deferredActions = new HashMap<>();
    }


    public boolean dispatchForPlayer(MTPlayer mtPlayer) {
        var data = deferredActions.remove(mtPlayer.getUUID());

        if(data != null) {
            boolean bool = false;

            for(DeferrableActions.DeferrableActionInstance<?> action : data) {
                bool = action.execute(mtPlayer);
            }

            return bool;
        }

        return false;
    }

    public <T> boolean scheduleActionForPlayer(KnownPlayer player, DeferrableActions.DeferrableActionInstance<T> deferrableActionInstance) {
        return this.deferredActions.computeIfAbsent(player.uuid(), uuid -> new ArrayDeque<>())
                                   .offer(deferrableActionInstance);
    }

    public void save(Path path) throws IOException {
        IOHelper.saveToFile(CollectionHelper.convertMapValues(this.deferredActions, List::copyOf), mapCodec, path, false);
    }

    public void load(Path path) throws IOException {
        var map = IOHelper.loadFromFile(mapCodec, path, false);

        map.ifPresent(map1 -> this.deferredActions.putAll(CollectionHelper.convertMapValues(map1, ArrayDeque::new)));
    }


}
