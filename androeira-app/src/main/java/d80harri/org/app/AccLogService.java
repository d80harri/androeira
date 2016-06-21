package d80harri.org.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.telecom.Call;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.d80harri.androeira.socket.server.Service;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class AccLogService extends IntentService implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private boolean stopped = false;
    private Service service = new Service(Build.MODEL);

    public AccLogService() throws FileNotFoundException {
        super("AccLogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            service.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!stopped) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);

        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        senSensorManager.unregisterListener(this);
        try {
            service.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopped = true;
    }

    private void initialize() throws IOException {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        AcceloratorRawData acceloratorRawData = new AcceloratorRawData(event.timestamp, event.values[0], event.values[1], event.values[2]);
        try {
            service.post(acceloratorRawData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
