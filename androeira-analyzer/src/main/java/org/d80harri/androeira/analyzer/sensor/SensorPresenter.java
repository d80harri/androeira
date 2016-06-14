package org.d80harri.androeira.analyzer.sensor;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class SensorPresenter implements Initializable {
    @FXML
    private LineChart xyChart;

    @FXML
    private LineChart yzChart;

    @FXML
    private LineChart xzChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        XYChart.Series<Float, Float> xySeries = new XYChart.Series<>();
        XYChart.Series<Float, Float> yzSeries = new XYChart.Series<>();
        XYChart.Series<Float, Float> xzSeries = new XYChart.Series<>();

        xyChart.getData().add(xySeries);
        yzChart.getData().add(yzSeries);
        xzChart.getData().add(xzSeries);

        InputStream dataStream = SensorPresenter.class.getResourceAsStream("/example_data/perfect_circles.linearacc.raw.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(dataStream));

        String line;
        try {
            float[] currentLocation = new float[]{0, 0, 0};
            float[] currentSpeed = new float[]{0,0,0};
            Long lastTs = null;

            while ((line = reader.readLine()) != null) {
                String[] lineSplit = line.split(":");
                long t = Long.parseLong(lineSplit[0]);
                float xAccel = Float.parseFloat(lineSplit[1]);
                float yAccel = Float.parseFloat(lineSplit[2]);
                float zAccel = Float.parseFloat(lineSplit[3]);
                float[] currentAccel = new float[]{xAccel, yAccel, zAccel};
                if (lastTs != null) {
                    float timeDeltaInSec = (t - lastTs) / 1000000000f;
                    currentSpeed = add(mult(timeDeltaInSec, currentAccel), currentSpeed);

                    float[] displacement = mult(timeDeltaInSec, currentSpeed);

                    currentLocation = add(currentLocation, displacement);

                    System.out.println(timeDeltaInSec + "," + Arrays.toString(currentLocation));

                    xySeries.getData().add(new XYChart.Data<>(currentLocation[0], currentLocation[1]));
                    xzSeries.getData().add(new XYChart.Data<>(currentLocation[0], currentLocation[2]));
                    yzSeries.getData().add(new XYChart.Data<>(currentLocation[2], currentLocation[1]));
                }
                lastTs = t;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private float[] add(float[] localSpeed, float[] currentSpeed) {
        if (localSpeed.length != currentSpeed.length) throw new IllegalArgumentException("Cannot add two vectors of different size.");

        float[] result = new float[localSpeed.length];
        for (int i=0; i<localSpeed.length; i++) {
            result[i] = localSpeed[i] +currentSpeed[i];
        }
        return result;
    }

    private float[] mult(float l, float[] currentAccel) {
        float[] result = new float[currentAccel.length];

        for (int i=0; i<currentAccel.length; i++) {
            result[i] =currentAccel[i]*l;
        }

        return result;
    }
}
