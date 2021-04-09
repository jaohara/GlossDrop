package modulators;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GlosscillatorUI extends VBox{
    private final Label amplitudeLabel;
    private final Slider amplitudeSlider;
    private final Glosscillator glosscillator;
    private final Label glosscillatorLabel;
    private final String glosscillatorName;
    private final ChoiceBox<String> parentChoiceBox;
    private final Button startButton;
    private final Button removeButton;

    public GlosscillatorUI(Glosscillator glosscillator, String label,
                           ChoiceBox<String> parentChoiceBox){
        this.getStyleClass().add("glosscillator-pane");
        VBox.setMargin(this, new Insets(15,0,0,0));

        this.parentChoiceBox    = parentChoiceBox;
        this.parentChoiceBox.getItems().remove(label);

        this.amplitudeSlider    = new Slider(glosscillator.getTargetMin(),
                glosscillator.getTargetMax() / 2, glosscillator.getAmplitude());
        this.amplitudeLabel     = new Label();
        this.amplitudeLabel.getStyleClass().add("amplitude-label");

        this.glosscillator      = glosscillator;
        this.glosscillatorName  = label;
        this.glosscillatorLabel = new Label(this.glosscillatorName);
        this.glosscillatorLabel.getStyleClass().add("glosscillator-label");

        this.startButton        = new Button("Play");
        this.removeButton       = new Button("Remove");
        HBox buttonHBox         = new HBox();

        buttonHBox.getChildren().addAll(startButton, removeButton);

        updateLabel();

        this.getChildren().addAll(glosscillatorLabel, amplitudeLabel, amplitudeSlider, buttonHBox);

        // Event listeners
        amplitudeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            glosscillator.setData((Double)newValue, glosscillator.getFrequency());
            System.out.println(glosscillator.getAmplitude());
            updateLabel();
        });

        startButton.setOnAction(actionEvent -> {
            if (!glosscillator.isStopping()) {
                glosscillator.toggle();
                glosscillator.getTarget().setDisable(glosscillator.isRunning());
                startButton.setText((glosscillator.isRunning() && !glosscillator.isStopping()) ?
                        "Stop" : "Start");
            }
        });

        removeButton.setOnAction(actionEvent -> {
            glosscillator.kill();
            glosscillator.removeBaselineListener();
            glosscillator.getTarget().setDisable(false);

            ((VBox) this.getParent()).getChildren().remove(this);
            parentChoiceBox.getItems().add(glosscillatorName);
        });
    }

    public GlosscillatorUI(Spinner<Double> glosscillatorTarget, String label,
                           ChoiceBox<String> parentChoiceBox) {
        this(new Glosscillator(glosscillatorTarget), label, parentChoiceBox);
    }

    private void updateLabel(){
        amplitudeLabel.setText(String.format("Amplitude: %.2f", glosscillator.getAmplitude()));
    }
}
