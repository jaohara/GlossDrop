package elements;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import interfaces.FractalElement;

/**
 * elements.Line - A interfaces.FractalElement represented as a elements.Line, used to tether child Circles
 *        of the subsequent Fractal layer. Ported to JavaFX!
 *
 * @author      John O'Hara
 * @version     3/20/2021
 */
public class Line implements FractalElement {
    /** Base Color of the rendered elements.Line */
    private Color color;
    /** ending x-coord of the line (in px), relative to Cartesian origin */
    private final int endX;
    /** ending y-coord of the line (in px), relative to Cartesian origin */
    private final int endY;
    /** starting x-coord of the line (in px), relative to Cartesian origin */
    private final int startX;
    /** starting y-coord of the line (in px), relative to Cartesian origin */
    private final int startY;

    /**
     * Constructor for a new elements.Line
     *
     * @param x         int of starting x-coord of line, relative to Cartesian origin
     * @param y         int of starting y-coord of line, relative to Cartesian origin
     * @param length    double of the length of the line
     * @param angle     double of the angle offset of the line from Pi/2 radians
     * @param color     base Color of the rendered elements.Line
     */
    public Line(int x, int y, double length, double angle, Color color){
        this.color = color;
        this.startX = x;
        this.startY = y;
        this.endX = x + (int)Math.round(length * (Math.cos(angle)));
        this.endY = y + (int)Math.round(length * (Math.sin(angle))) * -1;
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
        gc.strokeLine(this.startX + (canvasWidth / 2), this.startY + (canvasHeight / 2),
                this.endX + (canvasWidth / 2), this.endY + (canvasHeight / 2));
    }

    public static boolean drawDirect(GraphicsContext gc, double canvasWidth, double canvasHeight,
                                  double startX, double startY,
                                  double length, double angle, Color color){
        startX += (canvasWidth/2);
        startY += (canvasHeight/2);
        double endX = startX + Math.round(length * (Math.cos(angle)));
        double endY = startY + Math.round(length * (Math.sin(angle))) * -1;

        boolean withinBounds = (startY < canvasWidth || endX < canvasWidth) &&
                (startY < canvasHeight || endY < canvasHeight) &&
                (startY >= 0 || endY >= 0) && (startX >= 0 || endX >= 0);
        boolean isVisible = length >= 1;

        if (withinBounds && isVisible) {
            gc.setStroke(color);
            gc.strokeLine(startX, startY, endX, endY);
        }

        return withinBounds && isVisible;
    }
}
