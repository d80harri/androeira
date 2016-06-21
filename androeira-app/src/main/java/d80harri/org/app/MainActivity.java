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
import android.widget.Toast;
import org.d80harri.androeira.socket.client.Client;
import org.d80harri.androeira.socket.intf.AcceloratorRawData;
import org.d80harri.androeira.socket.intf.ServiceLocation;
import d80harri.org.app.socket.SocketListActivity;
import org.d80harri.androeira.socket.client.ServiceLocator;
import org.d80harri.androeira.socket.server.Service;

import java.io.IOException;
import java.io.Serializable;

public class MainActivity extends AppCompatActivity {
    private static final int CHOOSE_SOCKET_REQUEST = 1;

    private boolean started = false;
    private Button serviceList;
    private ServiceLocator serviceLocator = new ServiceLocator();
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        serviceList = (Button) findViewById(R.id.serviceList);
        serviceList.setOnClickListener(this::findService);

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
                AccLogService.Callback callback = this::onSensorDataChanged;
                intent.putExtra(AccLogService.CALLBACK_PARAM, callback);
                startService(intent);
            }
            started = !started;
            serviceStarted.setText("Service started: " + started);
        });
        serviceLocator.setServiceAddedListener(this::onServiceAdded);
        serviceLocator.setServiceRemovedListener(this::onServiceRemoved);
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    serviceLocator.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    private void onSensorDataChanged(AcceloratorRawData acceloratorRawData) {
        try {
            client.post(acceloratorRawData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findService(View view) {
        Intent intent = new Intent(this, SocketListActivity.class);

        startActivityForResult(intent, CHOOSE_SOCKET_REQUEST);
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
        Snackbar.make(serviceList, "Server added (main)", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        System.out.println(location.getAddress() + ": " + location.getPort());

    }

    private void onServiceRemoved(ServiceLocation location) {
        System.out.println(location.getAddress() + ": " + location.getPort());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_SOCKET_REQUEST:
                ServiceLocation selectedLocation = (ServiceLocation) data.getSerializableExtra(SocketListActivity.LOCATION_RESULT);
                client = new Client(selectedLocation.getAddress(), selectedLocation.getPort());
                try {
                    client.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, selectedLocation.getName(), Toast.LENGTH_LONG).show();
                serviceList.setText(selectedLocation.getName());
                break;
        }
    }
}
