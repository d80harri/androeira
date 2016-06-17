package org.d80harri.androeira.analyzer.sensor;

import javafx.scene.chart.XYChart;

import java.math.BigDecimal;
import java.util.function.Supplier;

/**
 * Created by d80harri on 14.06.16.
 */
public class Converter extends ConverterBase {
    Long lastTs = null;
    BigDecimal[] currentSpeed = newVector(3);
    BigDecimal[] currentLocation = newVector(3);

    @Override
    protected float[] calculateProcessedVector(long t, float[] vector) {
        float[] result = null;

        BigDecimal[] currentAccel = new BigDecimal[]{BigDecimal.valueOf(vector[0]), BigDecimal.valueOf(vector[1]), BigDecimal.valueOf(vector[2])};
        if (lastTs != null) {
            BigDecimal timeDeltaInSec = BigDecimal.valueOf((t - lastTs)/1000000000f);
            currentSpeed = add(mult(timeDeltaInSec, currentAccel), currentSpeed);
            BigDecimal[] displacement = mult(timeDeltaInSec, currentSpeed);
            currentLocation = add(currentLocation, displacement);
            result = new float[]{currentLocation[0].floatValue(), currentLocation[1].floatValue(), currentLocation[2].floatValue()};
        }
        lastTs = t;
        return result;
    }

    private BigDecimal[] add(BigDecimal[] localSpeed, BigDecimal[] currentSpeed) {
        if (localSpeed.length != currentSpeed.length) throw new IllegalArgumentException("Cannot add two vectors of different size.");

        BigDecimal[] result = new BigDecimal[localSpeed.length];
        for (int i=0; i<localSpeed.length; i++) {
            result[i] = localSpeed[i].add(currentSpeed[i]);
        }
        return result;
    }

    private BigDecimal[] mult(BigDecimal l, BigDecimal[] currentAccel) {
        BigDecimal[] result = new BigDecimal[currentAccel.length];

        for (int i=0; i<currentAccel.length; i++) {
            result[i] =currentAccel[i].multiply(l);
        }

        return result;
    }

    private BigDecimal[] newVector(int len) {
        BigDecimal[] result = new BigDecimal[len];
        for (int i=0; i<len; i++) {
            result[i] = BigDecimal.ZERO;
        }
        return result;
    }

}
