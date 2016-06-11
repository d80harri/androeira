package org.d80harri.androeira.analyzer.sensor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.ResourceBundle;

public class SensorPresenter implements Initializable {
    @FXML
    private LineChart chart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        XYChart.Series<Double, Double> series1 = new XYChart.Series<>();
        XYChart.Series<Double, Double> series2 = new XYChart.Series<>();

        for (double i=0; i<9; i+=0.1) {
            series1.getData().add(new XYChart.Data<>(i, Math.sin(i)));
        }

        for (double i=0; i<9; i+=0.1) {
            series2.getData().add(new XYChart.Data<>(i, Math.cos(i)));
        }

        chart.getData().addAll(series1, series2);
    }
}
