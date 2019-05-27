package dtg.dogretriever.Model;


import android.util.Log;

public class Beacon{
    private String deviceAddress;
    private String id;
    private int latestRssi;
    private long lastDetectedTimestamp;

    public Beacon(String address, int rssi, String identifier,long lastDetectedTimestamp) {
        this.deviceAddress = address;
        this.latestRssi = rssi;
        this.id = identifier;
        this.lastDetectedTimestamp = lastDetectedTimestamp;
    }

    public void update(String address, int rssi, long lastDetectedTimestamp) {
        this.deviceAddress = address;
        this.latestRssi = rssi;
        this.lastDetectedTimestamp = lastDetectedTimestamp;
    }


    // Parse the instance id out of a UID packet
    public static String getInstanceId(byte[] data) {
        StringBuilder sb = new StringBuilder();
        Log.e("tal", "data length: " +  data.length);
        //UID packets are always 18 bytes in length
        //Parse out the last 6 bytes for the id
        int packetLength = 18;
        int offset = packetLength - 6;
        for (int i=offset; i < packetLength; i++) {
            sb.append(Integer.toHexString(data[i] & 0xFF));
        }

        return sb.toString();
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLatestRssi() {
        return latestRssi;
    }

    public void setLatestRssi(int latestRssi) {
        this.latestRssi = latestRssi;
    }

    public long getLastDetectedTimestamp() {
        return lastDetectedTimestamp;
    }

    public void setLastDetectedTimestamp(long lastDetectedTimestamp) {
        this.lastDetectedTimestamp = lastDetectedTimestamp;
    }


}
