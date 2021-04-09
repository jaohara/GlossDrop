import elements.Circle;
import elements.Line;
import interfaces.FractalElement;
import interfaces.FractalObserver;
import interfaces.FractalSubject;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import modulators.Orbiter;

import java.util.ArrayList;
import java.util.Random;

/**
 * FractalGenerator - A class that generates a fractal comprised of elements.Circle
 *                    and elements.Line FractalElements. Notifies observers upon state
 *                    change.
 *
 * @author      John O'Hara
 * @version     3/23/2021
 */
public class FractalGenerator implements FractalSubject {
    /** Collection of observers watching this subject.*/
    private final ArrayList<FractalObserver> observers;
    /** Whether or not the colors decay as depth increases */
    private boolean colorDecay;
    /** Whether or not elements are filled */
    private boolean fillElements;
    /** Whether nor not to draw the lines connecting the circles */
    private boolean hideTethers;
    /** Whether or not color decay is enabled */
    private boolean opacityDecay;
    /** Whether or not colors are randomized */
    private boolean randomColors;
    /** The color (or base decay color) of generated FractalElements*/
    private Color fgColor;
    /** The bgColor of the canvas */
    private Color bgColor;
    /** The number of children for each interfaces.FractalElement */
    private int childCount;
    /** Number of recursion levels to render*/
    private int recursionDepth;
    /** The ratio of the child radius to parent radius, as percentage */
    private double childRatio;
    /** The final opacity */
    private double finalOpacity;
    /** Radius of the initial fractalElement*/
    private double initialRadius;
    /** width of line to use when drawing outlined shapes */
    private double strokeWidth;

    private final Random rand;

    /** collection of FractalElements generated but not rendered */
    private ArrayList<FractalElement> fractalElements;
    /** count of objects actually drawn in last draw operation */
    private int drawCount;
    private Point2D origin;
    private double rotationOffset;
    private double zoomScale;

    public FractalGenerator(){
        this.rand       = new Random();
        this.origin     = new Point2D(0,0);
        this.zoomScale  = 1.0;
        this.observers  = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     * @param observer  An object to observe state change in this object.
     */
    @Override
    public void attach(FractalObserver observer) {
        this.observers.add(observer);
    }

    /**
     * {@inheritDoc}
     * @param observer  An object to be removed from the collection of observers.
     */
    @Override
    public void detach(FractalObserver observer) {
        this.observers.remove(observer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyObservers() {
        for (FractalObserver observer : this.observers){
            observer.update();
        }
    }

    /**
     * {@inheritDoc}
     * @param childCount        int of number of children each fractal element has
     * @param childRatio        double of the ratio of the child radius to the parent radius as a percentage
     * @param initialRadius     double of the initial radius for the first fractal element
     * @param recursionDepth    int of number of fractal levels to generate
     * @param strokeWidth       double of the width of the stroke used to draw the elements
     * @param finalOpacity      double of the final opacity reached for color decay of fractal elements as percentage
     * @param opacityDecay      boolean for whether or not the fractal element colors decay
     * @param fillElements      boolean for whether or not to fill or stroke the elements
     * @param randomColors      boolean for whether or not colors are randomized
     * @param fgColor           the color of the generated fractal elements
     * @param bgColor           the color of the canvas background
     */
    public void setData(int childCount, double childRatio, double initialRadius, int recursionDepth,
                        double strokeWidth, double finalOpacity, boolean opacityDecay, boolean colorDecay,
                        boolean fillElements, boolean randomColors, boolean hideTethers, Color fgColor,
                        Color bgColor) {
        this.childCount     = childCount;
        this.childRatio     = childRatio;
        this.initialRadius  = initialRadius;
        this.recursionDepth = recursionDepth;
        this.strokeWidth    = strokeWidth;
        this.finalOpacity   = finalOpacity;
        this.opacityDecay   = opacityDecay;
        this.colorDecay     = colorDecay;
        this.fillElements   = fillElements;
        this.randomColors   = randomColors;
        this.hideTethers    = hideTethers;
        this.fgColor        = fgColor;
        this.bgColor        = bgColor;
    }

    /**
     * {@inheritDoc}
     * @return      Current zoom level, 1.0 being 100%
     */
    @Override
    public double getZoomScale() {
        return zoomScale;
    }

    /**
     * {@inheritDoc}
     * @param zoom  New value for the level of zoom, 1.0 being 100%
     */
    public void setZoomScale(double zoom){
        this.zoomScale = zoom;
    }

    @Override
    public Point2D getOrigin(){
        return origin;
    }

    @Override
    public Point2D getRawOrigin(Canvas canvas) {
        Point2D relativeOrigin = this.getOrigin();

        return new Point2D((canvas.getWidth() / 2) + relativeOrigin.getX(),
                (canvas.getHeight() / 2) - relativeOrigin.getY());
    }

    @Override
    public void setRawOrigin(Point2D rawOrigin, Canvas canvas){
        this.origin = new Point2D(rawOrigin.getX() - (canvas.getWidth()/2),
                (rawOrigin.getY() - (canvas.getHeight()/2)) * -1);
    }

    /**
     * {@inheritDoc}
     * @param x     x-offset from origin
     * @param y     y-offset from origin
     */
    @Override
    public void setOriginOffset(double x, double y){
        origin = new Point2D(origin.getX() + x, origin.getY() + y);
        // todo: return to remove this
        System.out.println("Origin: " + origin.toString());
    }

    /**
     * {@inheritDoc}
     * @param rotationOffset    Rotation offset as a double
     */
    public void setRotationOffset(double rotationOffset){
        this.rotationOffset = rotationOffset;
    }

    /**
     * {@inheritDoc}
     * @return      Rotation offset as a double.
     */
    public double getRotationOffset(){
        return this.rotationOffset;
    }

    @Override
    public int getChildCount() {
        return this.childCount;
    }

    @Override
    public double getChildRatio() {
        return this.childRatio;
    }

    @Override
    public double getInitialRadius() {
        return this.initialRadius;
    }

    @Override
    public double getZoomedInitialRadius() {
        return this.initialRadius * this.zoomScale;
    }

    @Override
    public int getRecursionDepth() {
        return this.recursionDepth;
    }

    @Override
    public double getStrokeWidth() {
        return this.strokeWidth;
    }

    @Override
    public double getFinalOpacity() {
        return this.finalOpacity;
    }

    @Override
    public boolean getOpacityDecay() {
        return this.opacityDecay;
    }

    @Override
    public boolean getFillElements() {
        return this.fillElements;
    }

    @Override
    public boolean getRandomColors() {
        return this.randomColors;
    }

    @Override
    public boolean getHideTethers(){
        return this.hideTethers;
    }

    @Override
    public Color getFgColor() {
        return this.fgColor;
    }

    @Override
    public Color getBgColor() {
        return this.bgColor;
    }

    /**
     * {@inheritDoc}
     * @return  An ArrayList of the FractalElements that comprise the fractal.
     */
    @Override
    public ArrayList<FractalElement> getData() {
        // todo: kinda want to remove this whole implementation, but will it come in handy later?
        this.fractalElements = new ArrayList<>();
        generateFractal(recursionDepth, origin.getX(), origin.getY(),
                initialRadius * zoomScale, false, null);
        return this.fractalElements;
    }

    @Override
    public void renderFractal(GraphicsContext gc) {
        drawCount = 0;
        generateFractal(recursionDepth, origin.getX(), origin.getY(),
                initialRadius * zoomScale, true, gc);
    }

    /**
     * Private helper method to recursively generate the Fractal.
     *
     * @param remainingLevels   Remaining levels of recursion
     * @param centerX           int of x-value of the center of this level's base circle
     * @param centerY           int of y-value of the center of this level's base circle
     * @param radius            double of the radius of this level's base circle
     * @param render            whether to render directly or generate the ArrayList
     * @param gc                GraphicsContext for direct rendering
     */
    private void generateFractal(int remainingLevels, double centerX, double centerY,
                                 double radius, boolean render, GraphicsContext gc){
        //just to be safe
        render = render && gc != null;
        Color elementColor = this.fgColor;

        if (gc != null) {
            gc.setLineWidth(strokeWidth);
        }

        if (colorDecay){
            double saturationStep   = elementColor.getSaturation() / (recursionDepth - 1);
            double saturationValue  = elementColor.getSaturation() -
                    ((recursionDepth - remainingLevels) * saturationStep);
            elementColor = Color.hsb(elementColor.getHue(), saturationValue, elementColor.getBrightness());
        }

        if (randomColors){
            // might want to make this prefer colors similar to picked color
            elementColor = new Color(rand.nextDouble(), rand.nextDouble(), rand.nextDouble(), 1.0);
        }

        if (opacityDecay){
            double finalAlpha = 1.0 - (((double) finalOpacity) / 100);
            double alphaValue = 1.0 - ((recursionDepth - remainingLevels) * (finalAlpha)/(recursionDepth-1));

            elementColor = new Color(elementColor.getRed(), elementColor.getGreen(),
                    elementColor.getBlue(), alphaValue);
        }

        if (render) {
            // draw directly and increment drawCount on success
            drawCount += Circle.drawDirect(gc, gc.getCanvas().getWidth(), gc.getCanvas().getHeight(), centerX,
                    centerY * -1, radius, elementColor, fillElements)? 1 : 0;
        } else {
            this.fractalElements.add(new Circle((int)centerX, (int)centerY * -1, radius, elementColor));
        }

        if (remainingLevels > 1){
            for (int child = 1; child <= childCount; child++) {
                double childAngle =
                        (Math.PI / 2) + ((2 * Math.PI * (child - 1)) / childCount) + rotationOffset;



                double childRadius = radius * (childRatio * .01);
                double lineXOffset = radius * Math.cos(childAngle);
                double lineYOffset = radius * Math.sin(childAngle);

                // skip lines if they won't be visible
                if (childRatio < 100 && !hideTethers) {
                    if (render) {
                        drawCount += Line.drawDirect(gc, gc.getCanvas().getWidth(), gc.getCanvas().getHeight(),
                                Math.round(centerX + lineXOffset),
                                (centerY + lineYOffset) * -1,
                                radius - childRadius, childAngle, elementColor) ? 1 : 0;
                    } else {
                        this.fractalElements.add(new Line((int) Math.round(centerX + lineXOffset),
                                (int) (centerY + lineYOffset) * -1,
                                radius - childRadius, childAngle, elementColor));
                    }
                }

                generateFractal(remainingLevels - 1,
                        (int)Math.round(centerX + (2 * lineXOffset)),
                        (int)Math.round(centerY + (2 * lineYOffset)),
                        childRadius, render, gc);
            }
        }
    }

    public int getDrawCount(){
        return this.drawCount;
    }

    //  Various Getters
}
