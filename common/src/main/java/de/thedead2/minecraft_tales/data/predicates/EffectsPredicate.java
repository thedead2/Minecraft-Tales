package de.thedead2.minecraft_tales.data.predicates;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;


public class EffectsPredicate implements SimpleTriggerPredicate<Map<MobEffect, MobEffectInstance>> {

    public static final EffectsPredicate ANY = new EffectsPredicate(Collections.emptyMap());

    private final Map<MobEffect, MobEffectInstancePredicate> effects;


    public EffectsPredicate(Map<MobEffect, MobEffectInstancePredicate> effects) {
        this.effects = effects;
    }


    @Override
    public boolean matches(Map<MobEffect, MobEffectInstance> effects) {
        if(this != ANY) {
            for(Map.Entry<MobEffect, MobEffectInstancePredicate> entry : this.effects.entrySet()) {
                MobEffectInstance mobeffectinstance = effects.get(entry.getKey());
                if(!entry.getValue().matches(mobeffectinstance)) {
                    return false;
                }
            }

        }
        return true;
    }


    public static class MobEffectInstancePredicate implements SimpleTriggerPredicate<MobEffectInstance> {

        public static final MobEffectInstancePredicate ANY = new MobEffectInstancePredicate(MinMax.ANY_INT, MinMax.ANY_INT);

        private final MinMax<Integer> amplifier;

        private final MinMax<Integer> duration;


        public MobEffectInstancePredicate(MinMax<Integer> amplifier, MinMax<Integer> duration) {
            this.amplifier = amplifier;
            this.duration = duration;
        }


        public boolean matches(@Nullable MobEffectInstance effect) {
            if(effect == null) {
                return false;
            }
            else if(!this.amplifier.matches(effect.getAmplifier())) {
                return false;
            }
            else {
                return this.duration.matches(effect.getDuration());
            }
        }
    }
}
