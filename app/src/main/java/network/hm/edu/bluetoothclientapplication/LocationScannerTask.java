package network.hm.edu.bluetoothclientapplication;

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
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;

/**
 * Created by Bernd on 02.06.2015.
 */
public class LocationScannerTask implements LocationListener {

    private final Context context;
    private Handler locationScannerFinishedHandler;
    private RestTask.RestTaskContainer data;

    private final Handler outputHandler;


    public LocationScannerTask(Context context, Handler outputHandler, Handler locationScannerFinishedHandler) {
        this.context = context;
        this.outputHandler = outputHandler;
        this.locationScannerFinishedHandler = locationScannerFinishedHandler;
    }

    protected Void execute(RestTask.RestTaskContainer params) {
        data = params;
        getLocationByDevice();
        return null;
    }

    private void getLocationByDevice() {

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        // TODO: glaube nicht, dass das so funktioniert.
        String provider = locationManager.getBestProvider(criteria, false);
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.getMainLooper());
        outputHandler.obtainMessage(0, "GPS-Search runs").sendToTarget();
    }

    @Override
    public void onLocationChanged(Location location) {
        data.location = location;
        outputHandler.obtainMessage(1, String.valueOf(location.getLatitude())).sendToTarget();
        outputHandler.obtainMessage(2, String.valueOf(location.getLongitude())).sendToTarget();
        outputHandler.obtainMessage(0, "Location determined").sendToTarget();
        locationScannerFinishedHandler.obtainMessage(0, data).sendToTarget();
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
}
