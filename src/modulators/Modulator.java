package modulators;

import javafx.animation.AnimationTimer;

/**
 * An abstract class that is the basis for modulator classes.
 */
public abstract class Modulator {
    /*
        Something I never quite understood - why do I want to avoid using the protected
        accessor for variables like this? I want them defined here and accessible in the
        children. Is there a better way to handle that?

        todo: Just learned about package-private (no access keyword) -
            is this what I want here?
     */


    /** The AnimationTimer that handles this modulator */
    protected AnimationTimer timer;
    /** Whether or not this modulator is currently running */
    protected boolean running;
    /** Whether or not a stop action has been requested */
    protected boolean stopRequested;
    /** Start time of modulator in nanoseconds */
    protected long startTime;


    public static final int FPS_CAP = 60;

    protected int frameCount;
    protected long lastFrame;

    protected boolean usableFrame(long now){
        if ((now - lastFrame) >= (1000000000 / FPS_CAP)){
            lastFrame = now;
            frameCount++;

            return true;
        }

        return false;
    }

    /**
     * Starts a stopped animation.
     */
    public void start(){
        if (!running){
            startTime   = System.nanoTime();
            lastFrame   = startTime;
            running     = true;
            timer.start();
        }
    }

    /**
     * Stops a running animation
     *
     * @param finishCurrent     Whether or not to allow it to animate back to the base value
     */
    public void stop(boolean finishCurrent){
        if (!stopRequested) {
            if (running && finishCurrent) {
                this.stopRequested = true;
            } else if (running) {
                running = false;
                timer.stop();
            }
        }
    }

    /**
     * Default stop - allow animation to finish
     */
    public void stop(){
        stop(true);
    }

    /**
     * Kill current animation regardless of defaultStop operation
     */
    public void kill(){
        this.stopRequested = false;
        this.running = false;
        timer.stop();
    }

    /**
     * Toggles the animation.
     */
    public void toggle(){
        if (running) {
            stop(false);
        } else {
            start();
        }
    }

    /**
     * Whether or not the modulator is currently running
     *
     * @return  whether or not this modulator is running
     */
    public boolean isRunning(){
        return this.running;
    }

    /**
     * Whether or not the modulator is currently in the process of stopping
     *
     * @return  whether or not this modulator is currently stopping
     */
    public boolean isStopping() {
        return this.stopRequested;
    }
}
