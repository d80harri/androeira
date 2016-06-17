package org.d80harri.androeira.analyzer.sensor;

/**
 * Created by d80harri on 14.06.16.
 */
public class ThresholdConverter extends ConverterBase {
    private float p = 0.0f;
    private float[] thresholds = new float[3];

    ThresholdConverter() {
        this.timed = true;
    }

    @Override
    protected float[] calculateProcessedVector(long t, float[] v) {
        thresholds = add(multiply(v, p), multiply(thresholds, 1-p));
        return threshold(v);
    }

    private float[] threshold(float[] v) {
        float[] result = new float[v.length];

        for (int i=0; i<result.length; i++) {
            result[i] = v[i] < thresholds[i] ? 1 : 0;
        }

        return result;
    }

    private float[] add(float[] multiply, float[] multiply1) {
        if (multiply.length != multiply1.length) throw new IllegalArgumentException();
        float[] result = new float[multiply.length];

        for (int i=0; i<multiply.length; i++) {
            result[i] = multiply[i] + multiply1[i];
        }

        return result;
    }

    private float[] multiply(float[] thresholds, float v) {
        float[] res = new float[thresholds.length];

        for (int i=0; i<thresholds.length; i++) {
            res[i] = thresholds[i] *v;
        }

        return res;
    }
}
