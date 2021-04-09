package interfaces;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * interfaces.FractalElement - Represents an individual element that the generated fractals are
 *                  comprised of. Ported to JavaFX!
 *
 * @author      John O'Hara
 * @version     3/20/2021
 */
public interface FractalElement {
    /**
     * Draws the fractal element.
     *
     * @param gc                 A reference to the destination canvas GraphicsContext.
     * @param canvasWidth        The width (in px) of the destination panel.
     * @param canvasHeight       The height (in px) of the destination panel.
     */
    public void draw(GraphicsContext gc, int canvasWidth, int canvasHeight);

    /**
     * Sets the color of the fractal element.
     *
     * @param color             The desired color
     */
    public void setColor(Color color);
}
