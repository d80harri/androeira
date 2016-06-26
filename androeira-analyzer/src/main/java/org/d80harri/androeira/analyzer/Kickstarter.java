package org.d80harri.androeira.analyzer;

import com.airhacks.afterburner.views.FXMLView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.d80harri.androeira.analyzer.filter.integrate.IntegrateFilterView;
import org.d80harri.androeira.analyzer.sensor.SensorView;

/**
 * Created by d80harri on 24.06.16.
 */
public class Kickstarter extends Application {
    private static final String VIEW = "VIEW";
    private FXMLView view;

    public FXMLView getView() {
        if (view == null) {
            String viewName = this.getParameters().getNamed().get(VIEW);
            try {
                view = (FXMLView) Class.forName(viewName).newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    public void start(Class<? extends FXMLView> viewClass) {
        Kickstarter.launch(new String[]{"--"+VIEW + "=" + viewClass.getName()});
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        Scene scene = new Scene(getView().getView());
        stage.setTitle("followme.fx");
        final String uri = getClass().getResource("app.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        stage.show();
    }
}
