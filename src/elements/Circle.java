package elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import interfaces.FractalElement;

/**
 * elements.Circle - A interfaces.FractalElement represented as a circle, used as the base element of a new
 *          Fractal layer. Ported to JavaFX!
 *
 * @author      John O'Hara
 * @version     3/20/2021
 */
public class Circle implements FractalElement {
    /** Base Color of the rendered elements.Circle */
    private Color color;
    /** the Diameter (in px) of the elements.Circle */
    private final int diameter;
    /** top-left y coordinate (relative to cartesian origin) for drawing the elements.Circle */
    private final int y;
    /** top-left x coordinate (relative to cartesian origin) for drawing the elements.Circle */
    private final int x;

    /**
     * Constructor for a new elements.Circle.
     *
     * @param centerX   int of x-value of elements.Circle's center, relative to Cartesian origin
     * @param centerY   int of y-value of elements.Circle's center, relative to Cartesian origin
     * @param radius    double of the elements.Circle's radius
     * @param color     base Color of the rendered elements.Circle
     */
    public Circle(int centerX, int centerY, double radius, Color color){
        this.color = color;
        this.diameter = (int)Math.round(2 * radius);
        this.x = (int)Math.round(centerX - radius);
        this.y = (int)Math.round(centerY - radius);
    }

    /**
     * {@inheritDoc}
     * @param color             The desired color
     */
    public void setColor(Color color){
        this.color = color;
    }

    /**
     * {@inheritDoc}
     * @param gc                 A reference to the destination canvas GraphicsContext.
     * @param canvasWidth        The width (in px) of the destination panel.
     * @param canvasHeight       The height (in px) of the destination panel.
     */
    public void draw(GraphicsContext gc, int canvasWidth, int canvasHeight) {
        gc.setStroke(this.color);
        gc.strokeOval(this.x + (canvasWidth / 2), this.y + (canvasHeight / 2),
                this.diameter, this.diameter);
    }

    /**
     * Static method to directly draw the circle.
     *
     * @param gc                The destination GraphicsContext.
     * @param canvasWidth       The width (in px) of the destination panel.
     * @param canvasHeight      The height (in px) of the destination panel.
     * @param centerX           int of x-value of elements.Circle's center, relative to Cartesian origin
     * @param centerY           int of y-value of elements.Circle's center, relative to Cartesian origin
     * @param radius            double of the elements.Circle's radius
     * @param color             color of the rendered elements.Circle
     */
    public static boolean drawDirect(GraphicsContext gc, double canvasWidth, double canvasHeight,
                                  double centerX, double centerY, double radius, Color color, boolean fill){
        double x            = (Math.round(centerX - radius)) + (canvasWidth / 2);
        double y            = Math.round(centerY - radius) + (canvasHeight / 2);
        double diameter     = Math.round(2 * radius);

        boolean withinBounds = x < canvasWidth && y < canvasHeight &&
                y + diameter >= 0 && x + diameter >= 0;
        boolean isVisible = diameter >= 1;

        // don't draw if it won't be seen
        if (withinBounds && isVisible) {
            if (fill){
                gc.setFill(color);
                gc.fillOval(x, y, diameter, diameter);
            }
            else {
                gc.setStroke(color);
                gc.strokeOval(x, y, diameter, diameter);
            }
        }

        return withinBounds && isVisible;
    }
}
