package org.d80harri.androeira.analyzer.sensor;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.input.ScrollEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SensorPresenter implements Initializable {
    @FXML
    private LineChart xyChart;
    private XYChart.Series<Float, Float> xySeries;

    @FXML
    private LineChart yzChart;
    private XYChart.Series<Float, Float> yzSeries;

    @FXML
    private LineChart xzChart;
    private XYChart.Series<Float, Float> xzSeries;

    private IConverter converter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.converter = new ThresholdConverter();
        this.converter.setCallback(this::introduce);
        xySeries = new XYChart.Series<>();
        yzSeries = new XYChart.Series<>();
        xzSeries = new XYChart.Series<>();

        xyChart.getData().add(xySeries);
        yzChart.getData().add(yzSeries);
        xzChart.getData().add(xzSeries);

        InputStream dataStream = SensorPresenter.class.getResourceAsStream("/example_data/some_circle.linearacc.raw.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));

        String line;
        try {

            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split(":");
                long t = Long.parseLong(lineSplit[0]);
                this.converter.process(t, new float[]{Float.parseFloat(lineSplit[1]),
                        Float.parseFloat(lineSplit[2]),
                        Float.parseFloat(lineSplit[3])});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void introduce(long t, float[] converted) {
        System.out.println(t / 1000000 + " " + Arrays.toString(converted));

        if (converter.isTimed()) {
            xySeries.getData().add(new XYChart.Data<>((float)t, converted[0]));
            xzSeries.getData().add(new XYChart.Data<>((float)t, converted[1]));
            yzSeries.getData().add(new XYChart.Data<>((float)t, converted[2]));
        } else {
            xySeries.getData().add(new XYChart.Data<>(converted[0], converted[1]));
            xzSeries.getData().add(new XYChart.Data<>(converted[0], converted[2]));
            yzSeries.getData().add(new XYChart.Data<>(converted[2], converted[1]));
        }
    }

}
