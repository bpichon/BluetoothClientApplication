package network.hm.edu.bluetoothclientapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements Button.OnClickListener {

    private String myDeviceAddress;
    private BluetoothScannerTask bluetoothScannerTask;
    private LocationScannerTask locationScannerTask;
    private RestTask restTask;
    private Handler bluetoothScannerFinishedHandler;
    private Handler locationScannerFinishedHandler;
    private Handler restFinishedHandler;
    private Handler outputHandler;

    // UI-Elemente
    private TextView longValueLabel;
    private TextView latValueLabel;
    private TextView statusValueLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDeviceAddress = BluetoothAdapter.getDefaultAdapter().getAddress();

        bluetoothScannerFinishedHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ArrayList<String> deviceAddresses = (ArrayList<String>) msg.obj;
                Log.d("BLUETOOTH", "Amount of found devices: " + deviceAddresses.size());
                final RestTask.RestTaskContainer container = new RestTask.RestTaskContainer(myDeviceAddress, deviceAddresses);
                new RestTask(outputHandler, restFinishedHandler).execute(container);
            }
        };

        restFinishedHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                RestTask.RestTaskContainer data = (RestTask.RestTaskContainer) msg.obj;
                if (data.location == null) {
                    // Location über Server nicht gefunden. -> Nutze herkömmliche Methoden
                    locationScannerTask.execute(data);
                } else {
                    // Location per Bluetooth gefunden.
                    Log.d("POSITION", "my Position (via BT): [" +
                            data.location.getLatitude()+ ", " +
                            data.location.getLongitude() + "]");
                }
            }
        };

        locationScannerFinishedHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                final RestTask.RestTaskContainer data = (RestTask.RestTaskContainer) msg.obj;
                if (data.location == null) {
                    // TODO: was eigentlich dann?
                    System.err.println("Position konnte nicht über herkömmliche Wege bestimmt werden!");
                } else {
                    new RestTask(outputHandler, restFinishedHandler).execute(data);
                }
            }
        };

        outputHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                // msg.what = 1 -> long, 2 -> lat, 0 status
                if (msg.what == 1) {
                    latValueLabel.setText((String) msg.obj);
                } else if (msg.what == 2) {
                    longValueLabel.setText((String) msg.obj);
                } else if (msg.what == 0) {
                    statusValueLabel.setText((String) msg.obj);
                }
            }
        };


        bluetoothScannerTask = new BluetoothScannerTask(this, outputHandler, bluetoothScannerFinishedHandler,  BluetoothAdapter.getDefaultAdapter());
        locationScannerTask = new LocationScannerTask(this, outputHandler, locationScannerFinishedHandler);

        Button startButton = (Button) findViewById(R.id.button);
        startButton.setOnClickListener(this);

        longValueLabel = (TextView) findViewById(R.id.longValue);
        latValueLabel = (TextView) findViewById(R.id.latValue);

        statusValueLabel = (TextView) findViewById(R.id.statusValue);
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

    @Override
    public void onClick(View v) {
        // Beim start Felder leeren.
        outputHandler.obtainMessage(1, "").sendToTarget();
        outputHandler.obtainMessage(2, "").sendToTarget();
        bluetoothScannerTask.execute();
    }
}
