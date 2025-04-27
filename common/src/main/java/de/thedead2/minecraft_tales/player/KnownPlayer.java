package de.thedead2.minecraft_tales.player;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.util.helper.SerializationHelper;
import net.minecraft.world.entity.player.Player;

import java.time.LocalDateTime;
import java.util.UUID;


public record KnownPlayer(UUID uuid, String name, LocalDateTime lastOnline) {

    public static final Codec<KnownPlayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SerializationHelper.UUID_CODEC.fieldOf("uuid").forGetter(KnownPlayer::uuid),
            Codec.STRING.fieldOf("name").forGetter(KnownPlayer::name),
            SerializationHelper.DATE_TIME_CODEC.fieldOf("lastOnline").forGetter(KnownPlayer::lastOnline)
    ).apply(instance, KnownPlayer::new));


    public static KnownPlayer fromPlayer(Player player) {
        return new KnownPlayer(player.getUUID(), player.getScoreboardName(), LocalDateTime.now());
    }

    public static KnownPlayer fromMTPlayer(MTPlayer MTPlayer) {
        return new KnownPlayer(MTPlayer.getUUID(), MTPlayer.getName(), LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        KnownPlayer that = (KnownPlayer) o;
        return Objects.equal(uuid, that.uuid) && Objects.equal(name, that.name);
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(uuid, name);
    }


    @Override
    public String toString() {
        return this.uuid.toString() + ":" + this.name + "/" + MTGlobalConstants.DATE_TIME_FORMATTER.format(this.lastOnline);
    }
}
