package modulators;

import interfaces.FractalSubject;
import javafx.animation.AnimationTimer;
import javafx.geometry.Point2D;

/**
 *
 */
public class Orbiter extends Modulator {
    private Point2D offset;
    private final FractalSubject subject;

    public Orbiter(FractalSubject subject){
        this.subject    = subject;
        this.offset     = subject.getOrigin();

        this.timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (usableFrame(now)){
                    orbit((now - startTime)/1000000000.0);
                }
            }
        };
    }

    public double getX(){
        return this.offset.getX();
    }

    public double getY(){
        return this.offset.getY();
    }

    public void start(){
        offset = new Point2D(subject.getOrigin().getX() * -1, subject.getOrigin().getY() *-1);
        System.out.println("\n-----\n\noriginal offset: " + offset.toString());
        super.start();
    }

    public void stop(){
        kill();
    }

    /**
     *
     * @param elapsedTime   elapsed time in seconds
     */
    public void orbit(double elapsedTime){
        // dont think this does what I want it to, but whatever
        double xOffset = (Math.sin(elapsedTime) * 50) - subject.getOrigin().getX();
        double yOffset = (Math.sin(elapsedTime) * -37) - subject.getOrigin().getY();

        offset = new Point2D(xOffset, yOffset);

        System.out.println(" - new Offset: ".concat(Double.toString(xOffset)).concat(", ")
                .concat(Double.toString(yOffset)));


        subject.setOriginOffset(xOffset, yOffset);
        subject.notifyObservers();
    }
}
