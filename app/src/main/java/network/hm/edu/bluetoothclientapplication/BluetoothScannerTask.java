package network.hm.edu.bluetoothclientapplication;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Bernd on 02.06.2015.
 */
public class BluetoothScannerTask {

    private final Context context;
    private final BluetoothAdapter bluetoothAdapter;
    private final Handler bluetoothScannerFinishedHandler;
    private final BluetoothDiscoverBroadcastReceiver broadcastReceiver;

    private final Handler outputHandler;

    public BluetoothScannerTask(Context context, Handler outputHandler, Handler bluetoothScannerFinishedHandler, BluetoothAdapter bluetoothAdapter) {
        this.context = context;
        this.outputHandler = outputHandler;
        this.bluetoothAdapter = bluetoothAdapter;
        this.bluetoothScannerFinishedHandler = bluetoothScannerFinishedHandler;
        this.broadcastReceiver = new BluetoothDiscoverBroadcastReceiver();
    }

    protected void execute(Void... params) {
        // Registriere Broadcast Action Receives
        final IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(broadcastReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }

    private class BluetoothDiscoverBroadcastReceiver extends BroadcastReceiver {

        private List<String> discoveredDevices = new ArrayList<>();

        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discoveredDevices.add(device.getAddress());
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d("a", "Discovery ended");
                outputHandler.obtainMessage(0, "Found " + discoveredDevices.size() + " devices").sendToTarget();
                context.unregisterReceiver(broadcastReceiver);
                bluetoothScannerFinishedHandler.obtainMessage(0, discoveredDevices).sendToTarget();
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                discoveredDevices.clear();
                outputHandler.obtainMessage(0, "BT Scan runs").sendToTarget();
            }
        }
    }

    public Context getContext() {
        return context;
    }
}
