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
import org.d80harri.androeira.socket.client.Client;
import org.d80harri.androeira.socket.intf.ServiceLocation;
import d80harri.org.app.socket.SocketListActivity;
import org.d80harri.androeira.socket.client.ServiceLocator;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private boolean started = false;
    private Button serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serviceList = (Button) findViewById(R.id.serviceList);
        serviceList.setOnClickListener(this::startService);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final TextView serviceStarted = (TextView) findViewById(R.id.serivceStarted);
        serviceStarted.setText("Service started: " + started);

        fab.setOnClickListener(view -> {
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
            serviceStarted.setText("Service started: " + started);
        });
    }

    private void startService(View view) {
        Intent intent = new Intent(this, SocketListActivity.class);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
