package network.hm.edu.bluetoothclientapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernd on 02.06.2015.
 */
public class CommunicationTask extends AsyncTask<Void, Void, Position> {

    private final Context context;

    public CommunicationTask(Context context) {
        this.context = context;
    }

    @Override
    protected Position doInBackground(Void... params) {
        // TODO: impl
        // 1. get all Devices
        BluetoothDiscoverBroadcastReceiver receiver = new BluetoothDiscoverBroadcastReceiver();
        return null;
    }

    private void scanFinished(final List<String> deviceAddresses) {
        // 2. Send request to Server
        // 3. Get Response and handle
        // (4. get GPS position)
        // (5. Send GPS to Server)
        // (6. Wait for response)
        //
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
                scanFinished(discoveredDevices);
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                discoveredDevices.clear();
            }
        }
    }
}
