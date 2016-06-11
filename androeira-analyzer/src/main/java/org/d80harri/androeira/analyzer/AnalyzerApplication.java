package org.d80harri.androeira.analyzer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.d80harri.androeira.analyzer.sensor.SensorView;

public class AnalyzerApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        SensorView appView = new SensorView();
        Scene scene = new Scene(appView.getView());
        stage.setTitle("followme.fx");
        final String uri = getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        stage.show();
    }
}
