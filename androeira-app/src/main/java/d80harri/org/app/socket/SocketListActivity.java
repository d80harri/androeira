package d80harri.org.app.socket;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import d80harri.org.app.R;
import org.d80harri.androeira.socket.client.ServiceLocator;
import org.d80harri.androeira.socket.intf.ServiceLocation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by d80harri on 18.06.16.
 */
public class SocketListActivity extends ListActivity {
    private ServiceLocator serviceLocator = new ServiceLocator();

    private ArrayAdapter<ServiceLocation> adapter;
    private TextView text;
    private List<ServiceLocation> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.socket_list);


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

        text = (TextView) findViewById(R.id.mainText);

        // initiate the listadapter
        adapter = new ArrayAdapter<ServiceLocation>(this,
                R.layout.socket_list_row, R.id.listText, locations);


        // assign the list adapter
        setListAdapter(adapter);
    }

    private void onServiceRemoved(ServiceLocation serviceLocation) {
        System.out.println("here");
    }

    private void onServiceAdded(ServiceLocation serviceLocation) {
        Snackbar.make(text, "Server added (socketList) " + serviceLocation.getName(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        locations.add(serviceLocation);
        runOnUiThread(() ->
                adapter.notifyDataSetChanged());
    }

    // when an item of the list is clicked
    @Override
    protected void onListItemClick(ListView list, View view, int position, long id) {
        super.onListItemClick(list, view, position, id);

        String selectedItem = (String) getListView().getItemAtPosition(position);
        //String selectedItem = (String) getListAdapter().getItem(position);

        text.setText("You clicked " + selectedItem + " at position " + position);
    }


}
