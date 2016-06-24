package d80harri.org.app;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import org.apache.log4j.Logger;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.d80harri.androeira.socket.server.Service;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class AccLogService extends IntentService implements SensorEventListener {

    private static final Logger logger = Logger.getLogger(AccLogService.class);

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
            logger.error(e.getMessage(), e);
        }
        while (!stopped) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
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
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
