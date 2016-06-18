package org.d80harri.androeira.socket.intf;

import java.io.Serializable;

/**
 * Created by d80harri on 17.06.16.
 */
public class AcceloratorRawData implements Serializable {
    private long timestamp;
    private float x;
    private float y;
    private float z;

    public AcceloratorRawData(long timestamp, float x, float y, float z) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
