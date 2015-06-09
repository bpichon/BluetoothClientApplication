package network.hm.edu.bluetoothclientapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bernd on 02.06.2015.
 */
public class CommunicationTask extends AsyncTask<Void, Void, Position> {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private final Context context;
    private BluetoothAdapter bluetoothAdapter;

    public CommunicationTask(Context context) {
        this.context = context;
    }

    @Override
    protected Position doInBackground(Void... params) {
        // TODO: impl
        // 1. get all Devices
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDiscoverBroadcastReceiver receiver = new BluetoothDiscoverBroadcastReceiver();
        return null;
    }

    private void scanFinished(final List<String> deviceAddresses) {
        /* 1. Open connection */
        socket = new Socket("localhost", 80);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        /* 2. Create request */
        final String myDeviceAddress = bluetoothAdapter.getAddress();

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("requestCode", 0);
        requestJson.addProperty("deviceId", myDeviceAddress);
        JsonArray deviceArray = new JsonArray();
        for (String deviceAddress : deviceAddresses) {
            deviceArray.add(new JsonPrimitive(deviceAddress));
        }
        requestJson.add("deviceIds", deviceArray);

        /* 3. Send Request */
        out.write(requestJson.toString());
        out.newLine();
        out.flush();

        String response = in.readLine();

        // 3. Get Response and handle
        JsonObject result = new JsonParser().parse(response).getAsJsonObject();
        int errorCode = result.get("returnCode").getAsInt();

        if (errorCode == 0) {
            // success
            double long_ = result.get("long").getAsDouble();
            double lat = result.get("lat").getAsDouble();
            Log.d("POSITION", "The current Position is: lat:" + lat + " | long:" + long_);
            // TODO: as static var speichern oder direkt in das Textfeld schreiben.
            return;
        } else if (errorCode == 3) {
            // Request konnte nicht geparst werden.
            return;
        }
        // erroroCode ist 2 -> nicht genug Geräte -> herkömmliche Lokalisierung verwenden.

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
