import interfaces.FractalSubject;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class GlossDrop extends Application {
    private final boolean DEBUG = true;

    private FractalSubject subject;
    private FractalGenGUIController controller;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                getClass().getClassLoader().getResource("FractalGenGUI.fxml")));


        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);

        subject     = new FractalGenerator();
        controller  = loader.getController();
        subject.attach(controller);
        controller.passSubject(subject);
        controller.passScene(scene);

        stage.setScene(scene);
        stage.setTitle("glossDrop");
        stage.show();

        stage.heightProperty().addListener(new stageSizeListener());
        stage.widthProperty().addListener(new stageSizeListener());
    }

    @Override
    public void stop() throws Exception{
        controller.writeSerializer();
    }

    private class stageSizeListener implements ChangeListener<Number> {
        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldDim, Number newDim) {
            System.out.println("oldDim: ".concat(oldDim.toString()));
            System.out.println("newDim: ".concat(newDim.toString()));

            double calcDim = (double)newDim - (double)oldDim;

            if (((ReadOnlyDoubleProperty) observableValue).getName().equals("width")){
                System.out.println("width changed: ".concat(Double.toString(calcDim)));
                controller.setCanvasSize(controller.getCanvasWidth() + calcDim, controller.getCanvasHeight());
                //controller.setCanvasSize();
            } else {
                System.out.println("height changed: ".concat(Double.toString(calcDim)));
                controller.setCanvasSize(controller.getCanvasWidth(), controller.getCanvasHeight() + calcDim);
            }
        }
    }
}
