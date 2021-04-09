package modulators;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Spinner;

import static javafx.scene.control.SpinnerValueFactory.*;

/**
 * A class that oscillates the valueProperty of a javaFX Spinner representing
 * a double.
 *
 * @author      John O'Hara
 * @version     3/24/2021
 */
public class Glosscillator extends Modulator {
    /** magnitude of change in each direction */
    private double amplitude;
    /** the target's base value */
    private double baseline;
    /** speed of animation */
    private double frequency;   //todo: I don't know how I'd use this properly
    /** Reference to event listener to modify the baseline */
    private ChangeListener<Double> newBaselineListener;
    /** Reference to the target UI element */
    private final Spinner<Double> target;
    /** the target's max value */
    private final double targetMax;
    /** the target's min value */
    private final double targetMin;

    /**
     * Constructs a new Glosscilator object.
     *
     * @param target        Reference to the spinner UI element
     * @param amplitude     the magnitude of change in each direction
     * @param frequency     speed of the animation
     * @param autostart     whether or not to autostart animation
     */
    public Glosscillator(Spinner<Double> target, double amplitude, double frequency, boolean autostart){
        DoubleSpinnerValueFactory valueFactory = (DoubleSpinnerValueFactory) target.getValueFactory();

        this.target     = target;
        //this.targetMin  = valueFactory.getMin();
        this.targetMin  = 0;
        this.targetMax  = valueFactory.getMax();
        this.amplitude  = amplitude;
        this.frequency  = frequency;
        this.running    = false;

        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (usableFrame(now)) {
                    glosscillate(now);
                }

                if (stopRequested){
                    // this seems to miss the value and not stop if I get more precise
                    if (Math.abs(target.getValue() - baseline) < 0.1 ){
                        target.getValueFactory().setValue(baseline);
                        stopRequested = false;
                        running = false;
                        this.stop();
                    }
                }
            }
        };

        this.newBaselineListener = (obs, old, newValue) -> {
            if (!this.isRunning()) {
                this.baseline = newValue;
            }
        };

        this.target.valueProperty().addListener(newBaselineListener);

        this.stopRequested = false;
        this.baseline   = target.getValue();

        if (autostart) {
            start();
        }
    }

    /**
     * Constructs a new modulators.Glosscillator without starting it.
     *
     * @param target        Reference to the spinner UI element
     * @param amplitude     the magnitude of change in each direction
     * @param frequency     speed of the animation
     */
    public Glosscillator(Spinner<Double> target, double amplitude, double frequency){
        this(target, amplitude, frequency, false);
    }

    /**
     * Constructs a new Glosscillator object with only the reference to the Spinner being
     * modulated.
     *
     * @param target        Reference to the spinner UI element
     */
    public Glosscillator(Spinner<Double> target) {
        this(target, 0.0, 0.0);
    }

    /**
     * Calculates the current value of the target based on the parameters. Called
     * by the handle() method of AnimationTimer.
     *
     * @param now       Current time in nanoseconds, passed from the AnimationTimer
     */
    private void glosscillate(long now){
        // what do I end up doing with this? I need to get this to a value that can be safely
        // used as input for sin
        double elapsedTime = (now - startTime)/1000000000.0;


        double newValue = (Math.sin(elapsedTime) * amplitude) + baseline;

        // check if it is within bounds
        newValue = newValue > targetMax ? targetMax : Math.max(targetMin, newValue);

        target.getValueFactory().setValue(newValue);
    }

    /**
     * Method for removing the event listener
     */
    public void removeBaselineListener(){
        this.target.valueProperty().removeListener(newBaselineListener);
    }

    /**
     * Returns a reference to the spinner being modulated
     *
     * @return      The Spinner this is currently being modulated
     */
    public Spinner<Double> getTarget(){
        return this.target;
    }

    /**
     * Accessor for the glosscillator's amplitude
     *
     * @return      Amplitude of the glosscillator as a double
     */
    public double getAmplitude(){
        return this.amplitude;
    }

    /**
     * Accessor for the glosscillator's frequency
     *
     *
     * @return      Frequency of the glosscillator as a double
     */
    public double getFrequency(){
        return this.frequency;
    }

    /**
     * Accessor for the target spinner's min value
     *
     * @return      Target spinner's min value as a double
     */
    public double getTargetMin(){
        return this.targetMin;
    }

    /**
     * Accessor for the target spinner's max value
     *
     * @return      Target spinner's max value as a double
     */
    public double getTargetMax() {
        return this.targetMax;
    }

    /**
     * Mutators for values that are changed via GUI.
     *
     * @param amplitude     The magnitude of the change in each direction
     * @param frequency     speed of the animation
     */
    public void setData(double amplitude, double frequency){
        // maybe not while running?
        //if (!isRunning()) {
            this.amplitude = amplitude;
            this.frequency = frequency;
        //}
    }

    public String toString(){
        return "modulators.Glosscillator for " + target.getId() + ": base/amp = " + baseline
                + "/" + amplitude;
    }
}