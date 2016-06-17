package org.d80harri.androeira.analyzer.sensor;

/**
 * Created by d80harri on 14.06.16.
 */
public abstract class ConverterBase implements IConverter {
    private ConverterCallback callback;
    boolean timed = false;

    @Override
    public final void process(long t, float[] vector) {
        float[] result = calculateProcessedVector(t, vector);
        if (result != null) {
            callback.introduce(t, result);
        }
    }

    @Override
    public boolean isTimed() {
        return timed;
    }

    @Override
    public ConverterCallback getCallback() {
        return this.callback;
    }

    @Override
    public void setCallback(ConverterCallback callback) {
        this.callback = callback;
    }

    protected abstract float[] calculateProcessedVector(long t, float[] v);
}
