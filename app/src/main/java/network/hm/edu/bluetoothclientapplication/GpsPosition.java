package network.hm.edu.bluetoothclientapplication;

/**
 * Created by Bernd on 16.06.2015.
 */
public class GpsPosition {

    private double longitude;
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