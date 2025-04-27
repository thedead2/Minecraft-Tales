package de.thedead2.minecraft_tales.event.types;


import de.thedead2.minecraft_tales.MTGlobalConstants;
import de.thedead2.minecraft_tales.api.GameSide;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


public abstract class MTEvent {

    private boolean isCanceled;

    protected MTEvent() {

    }


    public boolean isCanceled() {
        return isCanceled;
    }

    public boolean isCancelable() {
        return false;
    }

    public void setCanceled(boolean canceled) {
        if(!this.isCancelable()) throw new UnsupportedOperationException("Attempted to call Event#setCanceled() on a non-cancelable event of type: " + this.getClass().getCanonicalName());

        isCanceled = canceled;
    }

    public GameSide getEffectiveSide() {
        return MTGlobalConstants.PLATFORM.getGameSide();
    }


    public enum Priority {
        HIGHEST, //First to execute
        HIGH,
        NORMAL,
        LOW,
        LOWEST
    }



    @Retention(value = RUNTIME)
    @Target(value = METHOD)
    public @interface Subscriber {

        Priority priority() default Priority.NORMAL;

        GameSide effectiveSide() default GameSide.BOTH;
    }
}
