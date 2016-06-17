package d80harri.org.app;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class AccLogService extends IntentService implements SensorEventListener {
    static final DateFormat df;


    static {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        df.setTimeZone(tz);
    }

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private OutputStreamWriter writer;
    private boolean stopped = false;

    public AccLogService() throws FileNotFoundException {
        super("AccLogService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
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
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stopped = true;
    }

    private void initialize() throws IOException {
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        File folder = new File(Environment.getExternalStorageDirectory(), "androeira");
        folder.mkdirs();
        File file = new File(folder, df.format(new Date()) + ".acc.csv");
        if (!file.exists())
            file.createNewFile();

        writer = new OutputStreamWriter(new FileOutputStream(file, true));

        senSensorManager.registerListener(this, senAccelerometer , SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(event.timestamp);
        for (float val : event.values) {
            buffer.append(":" + val);
        }

        try {
            writer.write(buffer.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace(); // TODO
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
