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
import android.widget.ListView;
import android.widget.TextView;
import d80harri.org.app.socket.ServiceLocation;
import d80harri.org.app.socket.ServiceProvider;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private boolean started = false;
    private ListView serviceList;
    private ServiceProvider serviceProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.serviceList = (ListView) findViewById(R.id.serviceList);
        this.serviceProvider = new ServiceProvider();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final TextView serviceStarted = (TextView) findViewById(R.id.serivceStarted);
        serviceStarted.setText("Service started: " + started);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
            serviceProvider.setServiceAddedListener(this::onServiceAdded);
            serviceProvider.setServiceRemovedListener(this::onServiceRemoved);
            new AsyncTask<Void, Void, Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        serviceProvider.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();

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

    private void onServiceAdded(ServiceLocation location) {
        System.out.println(location.getAddress() + ": " + location.getPort());

    }

    private void onServiceRemoved(ServiceLocation location) {
        System.out.println(location.getAddress() + ": " + location.getPort());

    }
}
