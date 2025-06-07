package de.thedead2.minecraft_tales.data.story.timer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.thedead2.minecraft_tales.util.helper.MathHelper;
import net.minecraft.Util;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.function.Consumer;


public class TickTimer {

    public static final Codec<TickTimer> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.FLOAT.fieldOf("startTime").forGetter(TickTimer::getStartTime),
            Codec.FLOAT.fieldOf("duration").forGetter(TickTimer::getDuration),
            Codec.FLOAT.fieldOf("timeLeft").forGetter(TickTimer::getTimeLeft),
            Codec.FLOAT.fieldOf("startCounter").forGetter(timer -> timer.startCounter),
            Codec.FLOAT.fieldOf("sleepTime").forGetter(timer -> timer.sleepTime),
            Codec.BOOL.fieldOf("inverted").forGetter(TickTimer::isInverted),
            Codec.BOOL.fieldOf("paused").forGetter(TickTimer::isPaused),
            Codec.BOOL.fieldOf("loop").forGetter(TickTimer::isLooping),
            Codec.FLOAT.fieldOf("partialTick").forGetter(timer -> timer.partialTick),
            Codec.FLOAT.fieldOf("tickDelta").forGetter(timer -> timer.tickDelta),
            Codec.LONG.fieldOf("lastMs").forGetter(timer -> timer.lastMs),
            Codec.FLOAT.fieldOf("msPerTick").forGetter(timer -> timer.msPerTick)
    ).apply(instance, (startTime, duration, timeLeft, startCounter, sleepTime, inverted, paused, loop, partialTick, tickDelta, lastMs, msPerTick) ->
            new TickTimer(msPerTick, lastMs, tickDelta, partialTick, loop, paused, inverted, sleepTime, startCounter, timeLeft, duration, startTime)));


    private float startTime;

    private float duration;

    private float timeLeft;

    private float startCounter;

    private float sleepTime;

    private boolean inverted;

    private boolean paused;

    private boolean loop;

    private float partialTick;

    private float tickDelta;

    private long lastMs;

    private final float msPerTick;

    private Consumer<TickTimer> updateListener;


    public TickTimer(float ticksPerSecond, float startTime, float duration, boolean loop) {
        this(ticksPerSecond, startTime, duration, false, false, loop);
    }


    public TickTimer(float ticksPerSecond, float startTime, float duration, boolean inverted, boolean paused, boolean loop) {
        this(ticksPerSecond, startTime, duration, inverted ? 0 : duration, 0, -1, inverted, paused, loop);
    }


    public TickTimer(float ticksPerSecond, float startTime, float duration, float timeLeft, float startCounter, float sleepTime, boolean inverted, boolean paused, boolean loop) {
        this.msPerTick = 1000.0F / ticksPerSecond;
        this.lastMs = Util.getMillis();
        this.startTime = startTime;
        this.duration = duration;
        this.timeLeft = timeLeft;
        this.startCounter = startCounter;
        this.sleepTime = sleepTime;
        this.inverted = inverted;
        this.paused = paused;
        this.loop = loop;
    }


    private TickTimer(float msPerTick, long lastMs, float tickDelta, float partialTick, boolean loop, boolean paused, boolean inverted, float sleepTime, float startCounter, float timeLeft, float duration, float startTime) {
        this.msPerTick = msPerTick;
        this.lastMs = lastMs;
        this.tickDelta = tickDelta;
        this.partialTick = partialTick;
        this.loop = loop;
        this.paused = paused;
        this.inverted = inverted;
        this.sleepTime = sleepTime;
        this.startCounter = startCounter;
        this.timeLeft = timeLeft;
        this.duration = duration;
        this.startTime = startTime;
    }


    public int advanceTime(long currentTime) {
        this.tickDelta = (float) (currentTime - this.lastMs) / this.msPerTick;
        this.lastMs = currentTime;
        this.partialTick += this.tickDelta;
        int i = (int) this.partialTick;
        this.partialTick -= (float) i;

        return i;
    }


    public void updateTime() {
        int j = this.advanceTime(Util.getMillis());

        if(this.paused || this.sleepTime > 0) {
            this.sleepTime -= j;
            return;
        }

        if(!this.isStarted()) {
            this.startCounter += j;
        }
        else if(this.isFinished() && this.loop) {
            this.reset();
        }
        else {
            if(!this.inverted) {
                this.timeLeft -= j;
            }
            else {
                this.timeLeft += j;
            }
        }

        if(this.updateListener != null) {
            this.updateListener.accept(this);
        }
    }

    public TickTimer addUpdateListener(Consumer<TickTimer> updateListener) {
        this.updateListener = updateListener;

        return this;
    }

    public String formatTimeLeft() {
        return DurationFormatUtils.formatDurationHMS(MathHelper.ticksToMillis(this.timeLeft));
    }


    public TickTimer startIfNeeded() {
        if(!this.isStarted()) {
            this.start();
        }
        return this;
    }


    public boolean isStarted() {
        return this.startCounter > this.startTime;
    }


    public boolean isFinished() {
        return this.inverted ? timeLeft >= this.duration : timeLeft <= 0;
    }


    public TickTimer start() {
        this.pause(false);
        this.reset();
        this.startCounter = this.startTime + 1;
        return this;
    }


    public TickTimer pause(boolean pause) {
        if(this.paused != pause) {
            this.advanceTime(Util.getMillis());
        }
        this.paused = pause;
        return this;
    }


    public TickTimer invert(boolean invert) {
        this.inverted = invert;
        return this;
    }


    public TickTimer loop(boolean loop) {
        this.loop = loop;
        return this;
    }


    public boolean isPaused() {
        return this.paused;
    }


    public boolean isLooping() {
        return this.loop;
    }


    public boolean isInverted() {
        return inverted;
    }


    public float getDuration() {
        return duration;
    }


    public TickTimer setDuration(float duration) {
        this.duration = duration;
        return this;
    }


    public float getStartTime() {
        return startTime;
    }


    public TickTimer setStartTime(float startTime) {
        this.startTime = startTime;
        return this;
    }


    /**
     * Resets the timer to start again.
     **/
    public void reset() {
        this.advanceTime(Util.getMillis());
        this.startCounter = 0;
        this.sleepTime = -1;
        this.timeLeft = this.inverted ? 0 : this.duration;
    }


    public TickTimer stop() {
        this.timeLeft = this.inverted ? this.duration + 1 : -1;
        this.pause(true);
        return this;
    }

    public TickTimer stopAndReset() {
        this.stop();
        this.reset();

        return this;
    }


    public TickTimer sleep(float time) {
        if(this.sleepTime < 0) {
            this.sleepTime = time;
        }
        return this;
    }

    public float getTimeLeft() {
        return timeLeft;
    }


    public float getTimePassed() {
        return this.duration - this.timeLeft;
    }
}
