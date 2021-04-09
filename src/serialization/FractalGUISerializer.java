package serialization;

import interfaces.FractalObserver;
import interfaces.FractalSubject;

import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FractalGUISerializer implements FractalObserver {
    private boolean initializedFromFile;
    private boolean animationLock;

    private FractalGUIData data;
    private final FractalSubject subject;

    private final String FILE_NAME   = "fractal_settings";


    // todo: Remove all println debug statements when we sort everything out

    // todo: Make it save zoom/position as well

    // todo: How do I stop this from updating during an animation? How would I save
    //  the currently running glosscillators?

    /*
        This doesn't seem like the best approach, but here are some ideas:

        I don't really know how to check whether or not a glosscillator is running because
        of how I create them. I could, however, check to see if one of the inputs that
        can be modulated is currently disabled, indicating that it was locked by the glosscillator.

        Further, I could also check whether or not the color cycle boxes are currently toggled.
        I'm not sure, but I *think* that that would be all I would need to indicate that the
        fractal is currently being animated and needs to be locked. I could check for this in the
        FractalGenGUIController on an update call and then have it set the lock on the serializer
        if these conditions are met.

        Todo:
            Even better - What if I assigned a style class to the modulated properties when they
            are actively being animated? I could then do a search of all children in the scene
            that have that style class and have animationLock be set to whether or not that
            collection is > 0.
     */


    // Another idea - Do I just want to have this called when the program closes?

    public FractalGUISerializer(FractalSubject subject){
        this.data = new FractalGUIData();
        this.subject = subject;

        if (fileExists()){
            try {
                readFromDisk();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void update() {
        data.childCount     = subject.getChildCount();
        data.childRatio     = subject.getChildRatio();
        data.initialRadius  = subject.getInitialRadius();
        data.recursionDepth = subject.getRecursionDepth();
        data.strokeWidth    = subject.getStrokeWidth();
        data.finalOpacity   = subject.getFinalOpacity();
        data.opacityDecay   = subject.getOpacityDecay();
        data.fillElements   = subject.getFillElements();
        data.randomColors   = subject.getRandomColors();
        data.hideTethers    = subject.getHideTethers();
        data.zoomScale      = subject.getZoomScale();

        Color fgColor = subject.getFgColor();
        Color bgColor = subject.getBgColor();

        data.fgColorComponents =
                new double[]{fgColor.getRed(), fgColor.getGreen(), fgColor.getBlue()};
        data.bgColorComponents =
                new double[]{bgColor.getRed(), bgColor.getGreen(), bgColor.getBlue()};
        data.origin =
                new double[]{subject.getOrigin().getX(), subject.getOrigin().getY()};
    }

    public boolean fileExists(){
        return Files.exists(Paths.get(FILE_NAME));
    }

    public void writeToDisk() throws IOException {
        System.out.println("\tWriting to disk...".concat(data.toString()));
        ObjectOutputStream ooStream =
                new ObjectOutputStream(new FileOutputStream(FILE_NAME));

        ooStream.writeObject(data);
        ooStream.close();
    }

    private void readFromDisk() throws ClassNotFoundException, IOException{
        System.out.println("\tReading from disk...");
        ObjectInputStream oiStream =
                new ObjectInputStream(new FileInputStream(FILE_NAME));

        FractalGUIData source = (FractalGUIData) oiStream.readObject();

        oiStream.close();

        System.out.println("\tRead ".concat(source.toString()));

        setValuesFromSource(source);
        this.initializedFromFile = true;
    }

    private void setValuesFromSource(FractalGUIData source){
        System.out.println("Setting values from source...");

        data.childCount         = source.childCount;
        data.childRatio         = source.childRatio;
        data.initialRadius      = source.initialRadius;
        data.recursionDepth     = source.recursionDepth;
        data.strokeWidth        = source.strokeWidth;
        data.finalOpacity       = source.finalOpacity;
        data.opacityDecay       = source.opacityDecay;
        data.fillElements       = source.fillElements;
        data.randomColors       = source.randomColors;
        data.hideTethers        = source.hideTethers;
        data.fgColorComponents  = source.fgColorComponents;
        data.bgColorComponents  = source.bgColorComponents;
        data.origin             = source.origin;
        data.zoomScale          = source.zoomScale;

        System.out.println("\tSource: ".concat(source.toString()));
        System.out.println("\tData: ".concat(data.toString()));
    }

    public void setAnimationLock(boolean lock){
        this.animationLock = lock;
    }

    public boolean isInitializedFromFile(){
        return this.initializedFromFile;
    }

    public int getChildCount(){
        return data.childCount;
    }

    public double getChildRatio(){
        return data.childRatio;
    }

    public double getInitialRadius(){
        return data.initialRadius;
    }

    public int getRecursionDepth(){
        return data.recursionDepth;
    }

    public double getStrokeWidth(){
        return data.strokeWidth;
    }

    public double getFinalOpacity(){
        return data.finalOpacity;
    }

    public double getZoomScale() {
        return data.zoomScale;
    }

    public boolean isOpacityDecay(){
        return data.opacityDecay;
    }

    public boolean isFillElements() {
        return data.fillElements;
    }

    public boolean isRandomColors() {
        return data.randomColors;
    }

    public boolean isHideTethers() {
        return data.hideTethers;
    }

    public Color getFgColor(){
        return getColor(data.fgColorComponents);
    }

    public Color getBgColor(){
        return getColor(data.bgColorComponents);
    }

    private Color getColor(double[] colorComponents){
        if (colorComponents != null){
            return new Color(colorComponents[0], colorComponents[1], colorComponents[2], 1.0);
        }

        // maybe a better fallback? random color?
        return Color.BLACK;
    }

    public Point2D getOrigin(){
        return new Point2D(data.origin[0], data.origin[1]);
    }


    private static class FractalGUIData implements Serializable{
        public int childCount;
        public double childRatio;
        public double initialRadius;
        public int recursionDepth;
        public double strokeWidth;
        public double finalOpacity;
        public boolean opacityDecay;
        public boolean fillElements;
        public boolean randomColors;
        public boolean hideTethers;
        public double[] fgColorComponents;
        public double[] bgColorComponents;
        public double[] origin; // 0 is x, 1 is y - is this too hacky?
        public double zoomScale;

        public String toString() {
            return "FractalGUIData - " + childCount + ", " + childRatio + ", " + initialRadius
                    + ", " + recursionDepth + ", " + strokeWidth + ", " + finalOpacity + ", " +
                    opacityDecay + ", " + fillElements + ", " + randomColors + ", " + hideTethers;
        }
    }
}
