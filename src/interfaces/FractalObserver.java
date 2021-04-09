package interfaces;

/**
 * interfaces.FractalObserver - Interface for classes that observe the updates of a interfaces.FractalSubject.
 *
 * @author      John O'Hara
 * @version     3/4/2021
 */
public interface FractalObserver {
    /**
     *  The action performed when this class has been notified of a state change in its subject.
     */
    public void update();
}
