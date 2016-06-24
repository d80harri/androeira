package d80harri.org.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import org.d80harri.androeira.socket.client.Client;
import org.d80harri.androeira.socket.intf.ServiceLocation;
import d80harri.org.app.socket.SocketListActivity;
import org.d80harri.androeira.socket.client.ServiceLocator;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private boolean started = false;
    private ToggleButton serviceStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serviceStarted = (ToggleButton) findViewById(R.id.startService);
        serviceStarted.setOnClickListener(this::startService);

        serviceStarted.setOnClickListener( view -> {
            if (started) {
                Snackbar.make(view, "Logging stopped", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, AccLogService.class);
                stopService(intent);
            } else {
                Snackbar.make(view, "Logging started", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                Intent intent = new Intent(MainActivity.this, AccLogService.class);

                startService(intent);
            }
            started = !started;
        });
    }

    private void startService(View view) {
        Intent intent = new Intent(this, SocketListActivity.class);

        startActivity(intent);
    }

}
