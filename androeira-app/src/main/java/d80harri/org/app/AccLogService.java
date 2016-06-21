package d80harri.org.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.telecom.Call;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;

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
    public static final String CALLBACK_PARAM = "callback";

    static final DateFormat df;


    static {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        df.setTimeZone(tz);
    }

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Set<Callback> callbacks = new HashSet<>();
    private boolean stopped = false;

    public AccLogService() throws FileNotFoundException {
        super("AccLogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Callback callback = (Callback) intent.getSerializableExtra(CALLBACK_PARAM);
        callbacks.add(callback);
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

        stopped = true;
    }

    private void initialize() throws IOException {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        for (Callback callback : callbacks) {
            callback.onSensorChanged(new AcceloratorRawData(event.timestamp, event.values[0], event.values[1], event.values[2]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface Callback extends Serializable {
        public void onSensorChanged(AcceloratorRawData data);
    }
}
