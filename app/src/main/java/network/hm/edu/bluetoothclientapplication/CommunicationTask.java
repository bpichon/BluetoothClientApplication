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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Bernd on 02.06.2015.
 */
public class CommunicationTask extends AsyncTask<Void, Void, Position> implements LocationListener {

    RestAdapter restAdapter;
    String myDeviceAddress;

    private final Context context;
    private BluetoothAdapter bluetoothAdapter;

    public CommunicationTask(Context context) {
        this.context = context;
    }

    @Override
    protected Position doInBackground(Void... params) {
        // TODO: impl
        // 1. get all Devices
        restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://simon-holzmann.de:8080/")
                .build();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myDeviceAddress = bluetoothAdapter.getAddress();
        BluetoothDiscoverBroadcastReceiver broadcastReceiver = new BluetoothDiscoverBroadcastReceiver();
        bluetoothAdapter.startDiscovery();

        // Registriere Broadcast Action Receives
        final IntentFilter intentFilter= new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(broadcastReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();

        return null;
    }

    private void scanFinished(final List<String> deviceAddresses) {
        sendDeviceInfo(deviceAddresses);
    }

    private void sendDeviceInfo(List<String> deviceAddresses) {
        /* 2. Create request */

        LocationService locationServiceRest = restAdapter.create(LocationService.class);
        GpsPosition position = locationServiceRest.tryToDeterminateLocation(deviceAddresses, myDeviceAddress);

        if (position == null) {
            // Position konnte nicht bestimmt werden.
            getLocationByDevice();
        } else {
            // TODO: Position weiterverarbeiten.
            Log.d("POSITION", "my Position (via BT): [" +
                    position.getLatitude()+ ", " +
                    position.getLongitude() + "]");
        }
    }

    private void sendMyPosition(Location location) {
        LocationService locationServiceRest = restAdapter.create(LocationService.class);
        locationServiceRest.saveMyPosition(new GpsPosition(location.getLatitude(), location.getLongitude()), myDeviceAddress);
    }

    private void getLocationByDevice() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        // TODO: glaube nicht, dass das so funktioniert.
        String provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestSingleUpdate(provider, this, null);
    }

    @Override
    public void onLocationChanged(Location location) {
        sendMyPosition(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // erstmal nix
    }

    @Override
    public void onProviderEnabled(String provider) {
        // erstmal nix
    }

    @Override
    public void onProviderDisabled(String provider) {
        // erstmal nix
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
                context.unregisterReceiver(this); // Deregistrieren, wenn fertig
                scanFinished(discoveredDevices);
            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                discoveredDevices.clear();
            }
        }
    }
}
