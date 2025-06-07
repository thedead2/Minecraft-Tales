package de.thedead2.minecraft_tales.data.story.timer;

import com.google.common.collect.Maps;
import de.thedead2.minecraft_tales.MTGlobalConstants;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;


public class TimeKeeper extends Thread {

    private final ConcurrentMap<ResourceLocation, TrackedTimer> timers = Maps.newConcurrentMap();

    private boolean keepAlive = true;


    public TimeKeeper(UUID playerId) {
        super(playerId + "-timekeeper");
        this.setDaemon(true);
        this.setPriority(2);
        if(MTGlobalConstants.PLATFORM.isServer()) {
            MTGlobalConstants.LOGGER.debug("Starting timekeeper {}", this.getName());
            this.start();
        }
    }


    public synchronized void startListening(ResourceLocation objective, TickTimer taskTimer, Runnable onFinish) {
        this.timers.put(objective, new TrackedTimer(taskTimer, onFinish));
        MTGlobalConstants.LOGGER.debug("Starting to track time for timer of objective {}", objective);
        this.notify();
    }


    public synchronized void stopGracefully() {
        this.keepAlive = false;
        this.notify();
    }


    @Override
    public void run() {
        try {
            while(true) {
                synchronized(this) {
                    while(this.timers.isEmpty() && this.keepAlive) {
                        this.wait();
                    }

                    if(!this.keepAlive) {
                        this.timers.clear();
                        MTGlobalConstants.LOGGER.debug("Stopping timekeeper {}", this.getName());
                        break;
                    }

                    for(Map.Entry<ResourceLocation, TrackedTimer> entry : this.timers.entrySet()) {
                        TrackedTimer timer = entry.getValue();

                        timer.updateTime();

                        if(timer.isFinished()) {
                            timer.finish();

                            ResourceLocation objectiveId = entry.getKey();

                            this.stopListening(objectiveId);
                        }
                    }
                }
            }
        }
        catch(InterruptedException ignored) {
        }
        catch(Throwable e) {
            MTGlobalConstants.LOGGER.error("An unexpected error occurred while timekeeping!", e);
        }
    }


    public synchronized void stopListening(ResourceLocation objective) {
        this.timers.remove(objective);
        this.notify();
    }


    public record TrackedTimer(TickTimer timer, Runnable onFinish) {

        public void updateTime() {
            this.timer.updateTime();
        }

        public boolean isFinished() {
            return this.timer.isFinished();
        }

        public void finish() {
            this.onFinish.run();
        }
    }
}
