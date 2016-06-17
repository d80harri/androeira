package org.d80harri.androeira.analyzer.sensor;

/**
 * Created by d80harri on 14.06.16.
 */
public interface IConverter {
    void process(long t, float[] vector);

    ConverterCallback getCallback();

    void setCallback(ConverterCallback callback);

    boolean isTimed();

    public static interface ConverterCallback {
        public void introduce(long t, float[] converted);
    }
}
