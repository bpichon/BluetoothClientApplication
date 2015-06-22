package network.hm.edu.bluetoothclientapplication;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernd on 16.06.2015.
 */
public class GpsPosition {

    @SerializedName(value="longitude")
    private double longitude;
    @SerializedName(value="latitude")
    private double latitude;

    public GpsPosition(final double latitude, final double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}