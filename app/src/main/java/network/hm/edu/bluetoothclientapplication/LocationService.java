package network.hm.edu.bluetoothclientapplication;

import java.util.List;

import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Created by Bernd on 16.06.2015.
 */
public interface LocationService {
    @POST("/location/getGpsPositionFromDeviceList")
    GpsPosition tryToDeterminateLocation(@Body List<String> deviceList, @Body String deviceId);

    @POST("/location/saveLocation")
    void saveMyPosition(@Body GpsPosition gpsPosition, @Body String deviceId);
}
