package network.hm.edu.bluetoothclientapplication;

import java.util.List;

import javax.crypto.spec.GCMParameterSpec;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;

/**
 * Created by Bernd on 16.06.2015.
 */
public interface LocationService {


    @POST("/location/getGpsPositionFromDeviceList")
    void tryToDeterminateLocation(@Body GetGpsPositionFromDeviceListRequest data, Callback<GpsPosition> bluetoothLocalisationProvider);

    @POST("/devices/insertOwnLocation")
    Response saveMyPosition(@Body InsertOwnLocationRequest positionContainer);



    public class InsertOwnLocationRequest {

        private final GpsPosition gpsPosition;
        private final String ownDeviceId;

        public InsertOwnLocationRequest(GpsPosition gpsPosition, String ownDeviceId) {
            this.gpsPosition = gpsPosition;
            this.ownDeviceId = ownDeviceId;
        }

        public GpsPosition getGpsPosition() {
            return gpsPosition;
        }

        public String getOwnDeviceId() {
            return ownDeviceId;
        }
    }

    public class GetGpsPositionFromDeviceListRequest {

        List<String> deviceList;
        String ownDeviceId;

        public GetGpsPositionFromDeviceListRequest(List<String> deviceList, String ownDeviceId) {
            this.deviceList = deviceList;
            this.ownDeviceId = ownDeviceId;
        }

        public List<String> getDeviceList() {
            return deviceList;
        }

        public void setDeviceList(List<String> deviceList) {
            this.deviceList = deviceList;
        }

        public String getOwnDeviceId() {
            return ownDeviceId;
        }

        public void setOwnDeviceId(String ownDeviceId) {
            this.ownDeviceId = ownDeviceId;
        }
    }
}
