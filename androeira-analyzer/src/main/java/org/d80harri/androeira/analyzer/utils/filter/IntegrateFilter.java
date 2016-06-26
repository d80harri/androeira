package org.d80harri.androeira.analyzer.utils.filter;

import org.d80harri.androeira.socket.intf.AcceloratorRawData;

/**
 * Created by d80harri on 24.06.16.
 */
public class IntegrateFilter implements IFilter {
    private AcceloratorRawData sum;
    private AcceloratorRawData lastValue;
    private final boolean interpolate;

    public IntegrateFilter(boolean interpolate) {
        this.interpolate = interpolate;
    }

    @Override
    public AcceloratorRawData filter(AcceloratorRawData value) {
        if (sum == null) {
            sum = new AcceloratorRawData(value.getTimestamp(), 0, 0, 0);
        } else {
            AcceloratorRawData tmp = new AcceloratorRawData(value.getTimestamp());
            tmp.setX(calculateArea(lastValue.getTimestamp(), value.getTimestamp(), lastValue.getX(), value.getX()));
            tmp.setY(calculateArea(lastValue.getTimestamp(), value.getTimestamp(), lastValue.getY(), value.getY()));
            tmp.setZ(calculateArea(lastValue.getTimestamp(), value.getTimestamp(), lastValue.getZ(), value.getZ()));
            sum = tmp;
        }
        lastValue = value;
        return sum;
    }

    private float calculateArea(long ts1, long ts2, float v1, float v2) {
        long timeDelta = ts2 - ts1;
        float result = timeDelta * v1;

        if (interpolate) {
            result += timeDelta * (v2-v1)/2;
        }

        return result;
    }
}
