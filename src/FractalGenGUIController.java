import interfaces.FractalObserver;
import interfaces.FractalSubject;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import modulators.Glosscillator;
import modulators.GlosscillatorUI;
import modulators.Orbiter;
import serialization.FractalGUISerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


@SuppressWarnings("SpellCheckingInspection")
public class FractalGenGUIController implements FractalObserver {

    // UI Elements from FXML
    @FXML
    private Canvas canvas;
    @FXML
    private Spinner<Integer> childCountSpinner;
    @FXML
    private Spinner<Integer> recursionDepthSpinner;
    @FXML
    private Spinner<Double> childRatioSpinner;
    @FXML
    private Spinner<Double> initialRadiusSpinner;
    @FXML
    private Spinner<Double> strokeWidthSpinner;
    @FXML
    private Spinner<Double> decayOpacitySpinner;
    @FXML
    private ToggleButton pauseRenderToggleButton;
    @FXML
    private ToggleButton glosscillatorsToggleButton;
    @FXML
    private CheckBox fillElementsCheckBox;
    @FXML
    private CheckBox decayColorCheckBox;
    @FXML
    private CheckBox decayOpacityCheckBox;
    @FXML
    private CheckBox electricKoolAidCheckBox;
    @FXML
    private CheckBox hideTethersCheckBox;
    @FXML
    private ChoiceBox<String> glosscillatorsChoiceBox;
    @FXML
    private Button glosscillatorAddButton;
    @FXML
    private ColorPicker fgColorPicker;
    @FXML
    private ColorPicker bgColorPicker;
    @FXML
    private Label elementCountLabel;
    @FXML
    private Label drawCountLabel;
    @FXML
    private Label zoomLabel;
    @FXML
    private VBox glosscillatorsVBox;

    // Controller State fields
    private boolean pauseRender;
    private Color bgColor;
    private ColorCycler fgColorCycler;
    private ColorCycler bgColorCycler;
    private FractalSubject subject;
    private FractalGUISerializer serializer;
    private GraphicsContext gc;
    private HashMap<String, Spinner<Double>> glosscillatorChoiceBoxOptions;
    private Orbiter orbiter;
    private Point2D dragStartPosition;
    private Scene scene;
    private double rotation;

    // some global settings
    private final boolean DEBUG             = false;
    private final double ZOOM_SENSITIVITY   = .1;
    private final double ROTATION_DELTA     = .005;

    /*
        TODO - MASTER LIST
            1. Figure out a GUI solution for the glosscilators.
            2. Figure out a GUI solution for the color changers (with above?)
            3. Implement a few effects and figure out a GUI solution for them.
    */

    //  ======================
    //  Initialization Methods
    //  ======================

    @FXML
    public void initialize(){
        bgColorCycler                   = new ColorCycler(bgColorPicker);
        fgColorCycler                   = new ColorCycler(fgColorPicker);
        gc                              = canvas.getGraphicsContext2D();
        glosscillatorChoiceBoxOptions   = new HashMap<>();
        pauseRender                     = pauseRenderToggleButton.isSelected();

        initSpinners();
        initChoiceBox();

        // this seems hacky, but doing this prevents the first TextField from being focused
        // by default. I reuse this to blur currently focused text boxes.
        Platform.runLater(() -> canvas.requestFocus());


        // debug line
        System.out.println(glosscillatorsVBox.getChildren().size());
    }

    public void passScene(Scene scene){
        this.scene = scene;
        bindHotkeys();
    }

    public void passSubject(FractalSubject subject) {
        this.subject    = subject;
        // create the orbiter now that you have the subject
        orbiter         = new Orbiter(subject);

        initSerializer();
        updateSubject();
    }

    /**
     * Initialize the serializer object for the fractal values and set initial control values
     * to whatever was last saved
     */
    private void initSerializer(){
        serializer = new FractalGUISerializer(subject);

        if (serializer.isInitializedFromFile()){
            childCountSpinner.getValueFactory().setValue(serializer.getChildCount());
            childRatioSpinner.getValueFactory().setValue(serializer.getChildRatio());
            initialRadiusSpinner.getValueFactory().setValue(serializer.getInitialRadius());
            recursionDepthSpinner.getValueFactory().setValue(serializer.getRecursionDepth());
            strokeWidthSpinner.getValueFactory().setValue(serializer.getStrokeWidth());
            decayOpacitySpinner.getValueFactory().setValue(serializer.getFinalOpacity());

            decayOpacityCheckBox.setSelected(serializer.isOpacityDecay());
            decayOpacitySpinner.setDisable(!serializer.isOpacityDecay());
            fillElementsCheckBox.setSelected(serializer.isFillElements());
            electricKoolAidCheckBox.setSelected(serializer.isRandomColors());
            hideTethersCheckBox.setSelected(serializer.isHideTethers());

            fgColorPicker.setValue(serializer.getFgColor());
            bgColorPicker.setValue(serializer.getBgColor());

            Point2D savedOrigin = serializer.getOrigin();
            subject.setOriginOffset(savedOrigin.getX(), savedOrigin.getY());
            subject.setZoomScale(serializer.getZoomScale());
        }

        subject.attach(serializer);
    }

    /**
     * Writes the serializer data to disk, to be called on application stop.
     */
    public void writeSerializer(){
        if (serializer != null){
            try {
                serializer.writeToDisk();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindHotkeys(){
        if (scene != null) {
            scene.setOnKeyPressed(keyEvent -> {
                if (keyEvent.getCode() == KeyCode.D) {
                    toggleDecay();
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.F) {
                    toggleFill();
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.E) {
                    toggleElectricKoolAid();
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.P) {
                    togglePauseRender();
                    if (!pauseRender) {
                        updateSubject();
                    }
                }
                if (keyEvent.getCode() == KeyCode.PLUS || keyEvent.getCode() == KeyCode.EQUALS) {
                    changeZoom(ZOOM_SENSITIVITY);
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.MINUS) {
                    changeZoom(ZOOM_SENSITIVITY * -1);
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT0) {
                    subject.setZoomScale(1.0);
                    updateZoomLabel();
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    Platform.runLater(() -> canvas.requestFocus());
                }

                // orbiter
                if (keyEvent.getCode() == KeyCode.O){
                    if (orbiter != null) {
                        orbiter.toggle();
                    }
                }


                // Glosscillator Menu
                if (keyEvent.getCode() == KeyCode.G){
                    toggleGlosscillatorVisibility();
                }


                // ColorCycler keys
                if (keyEvent.getCode() == KeyCode.V){
                    // foreground
                    fgColorCycler.toggle();
                }
                if (keyEvent.getCode() == KeyCode.B){
                    // background
                    bgColorCycler.toggle();
                }



                //  =================
                //      EFFECTS
                //  =================
                /*
                    todo: I don't really want these to be handled here. Ideally they'd be
                        configurable from some sort of dropdown list of things to toggle - maybe
                        another side pane? - regardless, I want them to be part of the UI, and
                        therefore toggleable from methods that are called via events. This
                        is something to flesh out after I figure out a UI manner for adding
                        glosscilators.

                    These really seem to slow down the drawing, which also slows down the
                    color cycling? That seems to be the first thing to tank

                    todo: These are kind of lamer than I expected. I probably need to tweak the
                        parameters of them, but most aren't worth the performance hit.
                */
                if (keyEvent.getCode() == KeyCode.BACK_SPACE){
                    gc.setEffect(null);
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT1){
                    // slowish, kinda muted by default
                    gc.setEffect(new Bloom(0.1));
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT2){
                    // slower blur
                    gc.setEffect(new GaussianBlur());
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT3){
                    // this is cool and not too bad on performance
                    gc.setEffect(new BoxBlur());
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT4){
                    // this one's also pretty cool but worse on performance than BoxBlur
                    gc.setEffect(new MotionBlur());
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT5){
                    // not too cool. Sorta brightens everything.
                    gc.setEffect(new Glow());
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT6){
                    // Neat but not super noticeable - like a non-linear opacity decay?
                    gc.setEffect(new Blend());
                    updateSubject();
                }
                if (keyEvent.getCode() == KeyCode.DIGIT7){
                    // this is another neat one - makes it easy to see the order of layering
                    gc.setEffect(new DropShadow());
                    updateSubject();
                }

                if (keyEvent.getCode() == KeyCode.RIGHT){
                    subject.setRotationOffset(subject.getRotationOffset() - ROTATION_DELTA);
                    update();
                }
                if (keyEvent.getCode() == KeyCode.LEFT){
                    subject.setRotationOffset(subject.getRotationOffset() + ROTATION_DELTA);
                    update();
                }


            });
        }
    }

    private void initSpinners(){
        // IntegerSpinners (Can't be oscillated)
        childCountSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1,13,5));
        recursionDepthSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2,10,4));
        // DoubleSpinners
        childRatioSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(5.0,150,45,1));
        initialRadiusSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(10,200,70, 1));
        strokeWidthSpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(1.0, 30.0, 1.0, .1));
        decayOpacitySpinner.setValueFactory(
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0,100,20, 1));

        childCountSpinner.valueProperty().addListener(new SpinnerChangeListener());
        recursionDepthSpinner.valueProperty().addListener(new SpinnerChangeListener());
        childRatioSpinner.valueProperty().addListener(new SpinnerChangeListener());
        initialRadiusSpinner.valueProperty().addListener(new SpinnerChangeListener());
        strokeWidthSpinner.valueProperty().addListener(new SpinnerChangeListener());
        decayOpacitySpinner.valueProperty().addListener(new SpinnerChangeListener());

        // make spinners scrollable
        childCountSpinner.setOnScroll(new SpinnerScrollHandler());
        recursionDepthSpinner.setOnScroll(new SpinnerScrollHandler());
        childRatioSpinner.setOnScroll(new SpinnerScrollHandler());
        initialRadiusSpinner.setOnScroll(new SpinnerScrollHandler());
        strokeWidthSpinner.setOnScroll(new SpinnerScrollHandler());
        decayOpacitySpinner.setOnScroll(new SpinnerScrollHandler());
    }

    private void initChoiceBox(){
        // add to ChoiceBox
        glosscillatorChoiceBoxOptions.put("Node Ratio", childRatioSpinner);
        glosscillatorChoiceBoxOptions.put("Initial Radius", initialRadiusSpinner);
        glosscillatorChoiceBoxOptions.put("Line Width", strokeWidthSpinner);
        glosscillatorChoiceBoxOptions.put("Decay Opacity", decayOpacitySpinner);

        for (String key : glosscillatorChoiceBoxOptions.keySet()){
            glosscillatorsChoiceBox.getItems().add(key);
        }
    }

    //  ======================
    //  Accessors and Mutators
    //  ======================

    public double getCanvasHeight(){
        return this.canvas.getHeight();
    }

    public double getCanvasWidth(){
        return this.canvas.getWidth();
    }

    public void setCanvasSize(double width, double height){
        this.canvas.setWidth(width);
        this.canvas.setHeight(height);

        //todo: this might take a while to redraw if the current fractal is complex.
        //  is there a better way to handle this?
        //  Also, canvas resize isn't rendered if !pauseRender. Maybe force drawBg?
        updateSubject();
    }


    //  ===================
    //  Zoom-Related Things
    //  ===================

    @FXML
    private void zoomListener(ScrollEvent event){
        double zoomDelta = ZOOM_SENSITIVITY;

        // todo: I feel like this whole thing could be more clever?
        if (event.getDeltaY() < 0){
            zoomDelta *= -1;
        } else if (event.getDeltaY() == 0){
            zoomDelta *= 0;
        }

        //changeOriginOnZoom(event, zoomDelta);

        Point2D origin      = subject.getRawOrigin(canvas);
        int flip            = event.getX() < origin.getX()? 1 : -1;
        double angle        = Math.atan((event.getY() - origin.getY())/(event.getX() - origin.getX()));
        double distance     = origin.distance(event.getX(), event.getY());
        double travel       = subject.getZoomedInitialRadius() * zoomDelta * flip;
        Point2D newOrigin   =
                new Point2D(origin.getX() + (travel * Math.cos(angle)),
                origin.getY() + (travel * Math.sin(angle)));

        // block of print statements
        {
            System.out.println("\n-----");
            System.out.println("Zoom Event recorded: ");
            System.out.println("\tApprox location: "
                    .concat(Double.toString(event.getX()))
                    .concat(", ").concat(Double.toString(event.getY())));
            System.out.println("Current Origin: ".concat(origin.toString()));
            System.out.println("Angle (rads): ".concat(Double.toString(angle)));
            System.out.println("Distance    : ".concat(Double.toString(distance)));
            System.out.println("Travel      : ".concat(Double.toString(travel)));
            System.out.println("X-Travel    : ".concat(Double.toString(travel * Math.cos(angle))));
            System.out.println("Y-Travel    : ".concat(Double.toString(travel * Math.sin(angle))));
            System.out.println("\nNew origin  : ".concat(newOrigin.toString()));
            System.out.println("-----\n");
        }

        subject.setRawOrigin(newOrigin, canvas);

        changeZoom(zoomDelta);

        // draw the lines

        Color oldColor = (Color)gc.getStroke();
        gc.setStroke(Color.RED);
        gc.strokeLine(origin.getX(), origin.getY(), event.getX(), event.getY());
        gc.setStroke(Color.GREEN);
        gc.strokeLine(origin.getX(), origin.getY(), newOrigin.getX(), newOrigin.getY());
        gc.setStroke(oldColor);
    }

    private void changeZoom(double zoomDelta){
        double newZoom = Math.max(subject.getZoomScale() + zoomDelta, ZOOM_SENSITIVITY);

        if (newZoom != subject.getZoomScale() && newZoom > 0){
            subject.setZoomScale(newZoom);
            updateZoomLabel();
            updateSubject();
        }
    }

    private void changeOriginOnZoom(ScrollEvent event, double zoomDelta){

    }

    //  =====================
    //  Canvas Dragging Stuff
    //  =====================

    @FXML
    private void dragCanvasStart(MouseEvent event){
        this.dragStartPosition = new Point2D(event.getX(), event.getY());
    }

    @FXML
    private void dragCanvasOrigin(MouseEvent event){
        double xOffset = event.getX() - dragStartPosition.getX();
        double yOffset = (event.getY() - dragStartPosition.getY())*-1;

        dragStartPosition = new Point2D(event.getX(), event.getY());

        subject.setOriginOffset(xOffset,yOffset);

        if (DEBUG) {
            System.out.println("Drag event registered on canvas: "
                    .concat(Double.toString(xOffset).concat(", ")
                            .concat(Double.toString(yOffset))));

            System.out.println("Rerendering...");
        }

        updateSubject();
    }


    //  ==============
    //  Hotkey Methods
    //  ==============

    private void toggleDecay() {
        decayOpacityCheckBox.setSelected(!decayOpacityCheckBox.isSelected());
        decayOpacitySpinner.setDisable(!decayOpacityCheckBox.isSelected());
    }

    private void toggleFill() {
        fillElementsCheckBox.setSelected(!fillElementsCheckBox.isSelected());
    }

    private void togglePauseRender() {
        pauseRenderToggleButton.setSelected(!pauseRenderToggleButton.isSelected());
        pauseRender = pauseRenderToggleButton.isSelected();
    }

    private void toggleElectricKoolAid() {
        electricKoolAidCheckBox.setSelected(!electricKoolAidCheckBox.isSelected());
        fgColorPicker.setDisable(electricKoolAidCheckBox.isSelected());
    }


    //  ====================
    //  Event-Related Things
    //  ====================

    @FXML
    private void handleValueChange(ActionEvent event){
        decayOpacitySpinner.setDisable(!decayOpacityCheckBox.isSelected());
        fgColorPicker.setDisable((electricKoolAidCheckBox.isSelected() || fgColorCycler.isRunning()));
        pauseRender = pauseRenderToggleButton.isSelected();

        updateSubject();
    }

    @FXML
    private void addGlosscillator(ActionEvent event){
        String propertyName             = glosscillatorsChoiceBox.getValue();
        Spinner<Double> propertySpinner = glosscillatorChoiceBoxOptions.get(propertyName);

        glosscillatorsVBox.getChildren().add(
                new GlosscillatorUI(propertySpinner, propertyName, glosscillatorsChoiceBox));

        //serializer.setAnimationLock(glosscillatorsVBox.getChildren().contains());

        System.out.println("Current Value: ".concat(propertyName));
        System.out.print("\tRemoving...");
    }

    private class SpinnerChangeListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue observableValue, Object o, Object t1) {
            updateSubject();
        }
    }

    private class SpinnerScrollHandler implements EventHandler<ScrollEvent> {
        @Override
        public void handle(ScrollEvent scrollEvent) {
            if (scrollEvent.getDeltaY() != 0) {
                // something seems _very unsafe_ about how I'm doing this
                Spinner sourceSpinner = (Spinner) scrollEvent.getSource();

                if (scrollEvent.getDeltaY() > 0) {
                    sourceSpinner.increment();
                } else if (scrollEvent.getDeltaY() < 0) {
                    sourceSpinner.decrement();
                }

                updateSubject();
            }
        }
    }

    //  ============
    //  Color Cycler
    //  ============

    private class ColorCycler extends AnimationTimer{
        private static final double COLOR_DELTA = .01;
        private final ColorPicker colorPicker;
        private boolean isRunning;

        public ColorCycler(ColorPicker colorPicker){
            this.colorPicker    = colorPicker;
            this.isRunning      = false;
        }

        @Override
        public void handle(long l) {
            Color currentColor = colorPicker.getValue();
            colorPicker.setValue(currentColor.deriveColor(COLOR_DELTA, 1,1,1));
            updateSubject();
        }

        public void toggle(){
            if (isRunning) {
                colorPicker.setDisable(false);
                stop();
            } else {
                colorPicker.setDisable(true);
                start();
            }

            isRunning = !isRunning;
        }

        public boolean isRunning(){
            return this.isRunning;
        }
    }

    @FXML
    private void toggleFgCycler(){
        fgColorCycler.toggle();
    }

    @FXML
    private void toggleBgCycler(){
        bgColorCycler.toggle();
    }


    //  ========================
    //  Observer-Related Methods
    //  ========================

    @Override
    public void update() {
        updateElementCount();

        if (!pauseRender){
            draw();
        }
    }

    private void updateSubject(){
        bgColor = bgColorPicker.getValue();

        subject.setData(
                childCountSpinner.getValue(),
                childRatioSpinner.getValue(),
                initialRadiusSpinner.getValue(),
                recursionDepthSpinner.getValue(),
                strokeWidthSpinner.getValue(),
                decayOpacitySpinner.getValue(),
                decayOpacityCheckBox.isSelected(),
                decayColorCheckBox.isSelected(),
                fillElementsCheckBox.isSelected(),
                electricKoolAidCheckBox.isSelected(),
                hideTethersCheckBox.isSelected(),
                fgColorPicker.getValue(),
                bgColor
        );

        if (DEBUG) {
            logGUIValues();
        }

        subject.notifyObservers();
    }

    //  =======================
    //  Display-Related Methods
    //  =======================

    private void draw(){
        drawBG();

        subject.renderFractal(gc);
        updateDrawCount();
    }

    private void drawBG(){
        gc.setFill(bgColor);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
    }

    private void updateElementCount(){
        int elementCount = 1;

        for (int i = 1; i < recursionDepthSpinner.getValue(); i++){
            elementCount += (int)(Math.pow(childCountSpinner.getValue(), i))*2;
        }

        elementCountLabel.setText(Integer.toString(elementCount).concat(" elements"));
    }

    private void updateDrawCount(){
        drawCountLabel.setText(Integer.toString(subject.getDrawCount()).concat(" drawn"));
    }

    private void updateZoomLabel(){
        zoomLabel.setText(Integer.toString((int)(subject.getZoomScale()*100)).concat("% Zoom"));
    }

    @FXML
    private void toggleGlosscillatorVisibility(){
        if (glosscillatorsToggleButton.isSelected()){
            System.out.println("I'm visible, right?");
            glosscillatorsVBox.setVisible(true);
        } else {
            System.out.println("I should be hidden.");
            glosscillatorsVBox.setVisible(false);
        }
    }

    private void logGUIValues(){
        System.out.println("Current GUI Values:\n");
        System.out.println("\t - childCount: "
                .concat(Integer.toString(childCountSpinner.getValue())));
        System.out.println("\t - recursionDepth: "
                .concat(Integer.toString(recursionDepthSpinner.getValue())));
        System.out.println("\t - childRatio: "
                .concat(Double.toString(childRatioSpinner.getValue())));
        System.out.println("\t - initialRadius: "
                .concat(Double.toString(initialRadiusSpinner.getValue())));
        System.out.println("\t - strokeWidth: "
                .concat(Double.toString(strokeWidthSpinner.getValue())));
        System.out.println("\t - colorDecay: "
                .concat(Double.toString(decayOpacitySpinner.getValue())));
        System.out.println("\t - pauseRender: "
                .concat(Boolean.toString(pauseRenderToggleButton.isSelected())));
    }
}