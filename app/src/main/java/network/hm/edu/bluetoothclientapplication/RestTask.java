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
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Bernd on 02.06.2015.
 */
public class RestTask extends AsyncTask<RestTask.RestTaskContainer, Void, Void> {

    private final RestAdapter restAdapter;

    private final Handler restFinishedHandler;
    private final Handler outputHandler;


    public RestTask(Handler outputHandler, Handler restFinishedHandler) {
        this.outputHandler = outputHandler;
        this.restFinishedHandler = restFinishedHandler;
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint("http://simon-holzmann.de:8080/btl/rest/")
                .build();
    }

    @Override
    protected Void doInBackground(RestTaskContainer... params) {
        // 1. get all Devices
        final RestTaskContainer data = params[0];
        if (data.location == null) {
            // 1. Request: Versuch, die Position über den Server zu bestimmen
            sendDeviceInfo(data);
        } else {
            // 2. Request: Sende die Position aus GPS an den Server
            sendMyPosition(data);
        }
        return null;
    }

    private void sendMyPosition(RestTaskContainer data) {
        LocationService locationServiceRest = restAdapter.create(LocationService.class);
        outputHandler.obtainMessage(0, "Transmit position").sendToTarget();
        locationServiceRest.saveMyPosition(new LocationService.InsertOwnLocationRequest(new GpsPosition(data.location.getLatitude(), data.location.getLongitude()), data.myDeviceAddress));
        outputHandler.obtainMessage(0, "End").sendToTarget();
        // ENDE
    }

    private void sendDeviceInfo(final RestTaskContainer data) {
        LocationService locationServiceRest = restAdapter.create(LocationService.class);
        outputHandler.obtainMessage(0, "Transmit BT-Devices").sendToTarget();
        locationServiceRest.tryToDeterminateLocation(new LocationService.GetGpsPositionFromDeviceListRequest(data.deviceAddresses, data.myDeviceAddress), new Callback<GpsPosition>() {
            @Override
            public void success(GpsPosition gpsPosition, Response response) {
                if (gpsPosition == null) {
                    // Position konnte nicht bestimmt werden.
                    data.location = null;
                    outputHandler.obtainMessage(0, "Unable to determine position").sendToTarget();
                } else {
                    // Position konnte bestimmt werden.
                    data.location = new Location("BluetoothLocalisationProvider");
                    data.location.setLatitude(gpsPosition.getLatitude());
                    data.location.setLongitude(gpsPosition.getLongitude());
                    outputHandler.obtainMessage(0, "Position successfully determined").sendToTarget();
                    outputHandler.obtainMessage(1, String.valueOf(gpsPosition.getLatitude())).sendToTarget();
                    outputHandler.obtainMessage(2, String.valueOf(gpsPosition.getLongitude())).sendToTarget();
                }
                restFinishedHandler.obtainMessage(0, data).sendToTarget();
            }

            @Override
            public void failure(RetrofitError error) {
                outputHandler.obtainMessage(0, "Rest-Service Error").sendToTarget();
                throw new RuntimeException("Rest Failure!");
            }
        });
    }

    static class RestTaskContainer {
        public final String myDeviceAddress;
        public final ArrayList<String> deviceAddresses;
        public Location location = null;

        RestTaskContainer(String myDeviceAddress, ArrayList<String> deviceAddresses) {
            this.deviceAddresses = deviceAddresses;
            this.myDeviceAddress = myDeviceAddress;
        }

        RestTaskContainer() {
            myDeviceAddress = "";
            deviceAddresses = new ArrayList<>();
        }
    }

}
