package interfaces;

import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * interfaces.FractalSubject - A class that makes a fractal based on a given set of parameters.
 *                  Uses the observer design pattern, storing a collection of
 *                  observer objects that receive the fractal data as an ArrayList
 *                  of FractalElements.
 *
 * @author      John O'Hara
 * @version     3/9/2021
 */
public interface FractalSubject {
    /**
     * Attaches a new observer.
     *
     * @param observer  An object to observe state change in this object.
     */
    public void attach(FractalObserver observer);

    /**
     * Removes an observer if it exists.
     *
     * @param observer  An object to be removed from the collection of observers.
     */
    public void detach(FractalObserver observer);

    /**
     * Notifies all registered observers of a state change.
     */
    public void notifyObservers();

    /**
     * Modifies the state of the properties that the fractal is generated from.
     *
     * @param childCount        int of number of children each fractal element has
     * @param childRatio        int of the ratio of the child radius to the parent radius as a percentage
     * @param initialRadius     int of the initial radius for the first fractal element
     * @param recursionDepth    int of number of fractal levels to generate
     * @param strokeWidth       width of the stroke used to draw the elements
     * @param finalOpacity      int of the final opacity reached for color decay of fractal elements as percentage
     * @param opacityDecay      boolean for whether or not the fractal element opacity decays
     * @param colorDecay        boolean for whether or nor the fractal element colors decay
     * @param fillElements      boolean for whether or not to fill or stroke the elements
     * @param randomColors      boolean for whether or not colors are randomized
     * @param fgColor           the color of the generated fractal elements
     * @param bgColor           the color of the canvas background
     */
    public void setData(int childCount, double childRatio, double initialRadius, int recursionDepth,
                        double strokeWidth, double finalOpacity, boolean opacityDecay, boolean colorDecay,
                        boolean fillElements, boolean randomColors, boolean hideTethers, Color fgColor,
                        Color bgColor);

    /**
     * Generates a fractal of the current settings, returning the results as an ArrayList
     * of FractalElements.
     *
     * @return      An ArrayList of the FractalElements that comprise the fractal.
     */
    public ArrayList<FractalElement> getData();

    /**
     * Renders the fractal with the current settings directly to the provided GraphicsContext.
     *
     * @param gc    The GraphicsContext object of the output Canvas
     */
    public void renderFractal(GraphicsContext gc);

    /**
     * Returns the number of drawn elements from the previous renderFractal call
     *
     * @return      Number of elements drawn as an integer
     */
    public int getDrawCount();

    /**
     * Gets the current origin.
     *
     * @return      Origin as a Point2D
     */
    public Point2D getOrigin();

    /**
     * Gets the current origin's actual position in pixels on the canvas
     *
     * @param canvas        The canvas that the origin is in reference to
     * @return              The position of the origin in relation to the canvas origin
     */
    public Point2D getRawOrigin(Canvas canvas);

    /**
     * Sets an absolute origin to a relative origin.
     *
     * @param rawOrigin     The absolute origin on the canvas
     * @param canvas        The canvas that the abosolute origin is referring to
     */
    public void setRawOrigin(Point2D rawOrigin, Canvas canvas);

    /**
     * Sends the offset values to calculate the location of the new origin.
     *
     * @param x     x-offset from origin
     * @param y     y-offset from origin
     */
    public void setOriginOffset(double x, double y);

    /**
     * Gets the current rotation offset.
     *
     * @return  Rotation offset as a double
     */
    public double getRotationOffset();

    /**
     * Sets the new rotation offset
     *
     * @param rotationOffset    Rotation offset as a double
     */
    public void setRotationOffset(double rotationOffset);

    /**
     * Returns current zoom level
     *
     * @return      Current zoom level, 1.0 being 100%
     */
    public double getZoomScale();

    /**
     * Sets the current zoom level
     *
     * @param zoom  New value for the level of zoom, 1.0 being 100%
     */
    public void setZoomScale(double zoom);

    public int getChildCount();
    public double getChildRatio();
    public double getInitialRadius();
    public double getZoomedInitialRadius();
    public int getRecursionDepth();
    public double getStrokeWidth();
    public double getFinalOpacity();
    public boolean getOpacityDecay();
    public boolean getFillElements();
    public boolean getRandomColors();
    public boolean getHideTethers();
    public Color getFgColor();
    public Color getBgColor();
}
