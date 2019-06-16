package dtg.dogretriever.Controller;


import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import dtg.dogretriever.Model.Beacon;


public class BeaconScannerService extends Service {

    private static final String TAG = "BeaconScannerService";

    private static final ParcelUuid UID_SERVICE =
            ParcelUuid.fromString("0000feaa-0000-1000-8000-00805f9b34fb");


    //default namespace id for beacon
    private static final byte[] NAMESPACE_FILTER = {
            0x00, //Frame type
            0x00, //TX power
            (byte)0xd8, (byte)0x9b, (byte)0xed, (byte)0x6e, (byte)0x13,
            (byte)0x0e, (byte)0xe5, (byte)0xcf, (byte)0x1b, (byte)0xa1,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    // Force frame type and namespace id to match
    private static final byte[] NAMESPACE_FILTER_MASK = {
            (byte)0xFF,
            0x00,
            (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
            (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };



    // Eddystone frame types
    private static final byte TYPE_UID = 0x00;


    //Callback interface for the UI
    public interface OnBeaconEventListener {
        void onBeaconIdentifier(String deviceAddress, int rssi, String instanceId);
        void onBeaconTelemetry(String deviceAddress, float battery, float temperature);
    }

    private BluetoothLeScanner mBluetoothLeScanner;
    private OnBeaconEventListener mBeaconEventListener;

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothManager manager =
                (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothLeScanner = manager.getAdapter().getBluetoothLeScanner();

        startScanning();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScanning();
    }

    private void stopScanning() {
        mBluetoothLeScanner.stopScan(mScanCallback);
        Log.println(Log.ASSERT,TAG, "Scanning stopped…");
    }

    private void startScanning() {
        Log.println(Log.ASSERT,TAG, "startScan");
        ScanFilter beaconFilter = new ScanFilter.Builder()
                .setServiceUuid(UID_SERVICE)
                .setServiceData(UID_SERVICE, NAMESPACE_FILTER, NAMESPACE_FILTER_MASK)
                .build();

        List<ScanFilter> filters = new ArrayList<>();
        filters.add(beaconFilter);

        ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .build();
        //mBluetoothLeScanner.startScan(filters, settings, mScanCallback);

        mBluetoothLeScanner.startScan(mScanCallback);
        Log.d(TAG, "Scanning started…");
    }

    /* Handle UID packet discovery on the main thread */
    private void processUidPacket(String deviceAddress, int rssi, String id) {
        if (mBeaconEventListener != null) {
            mBeaconEventListener.onBeaconIdentifier(deviceAddress, rssi, id);
        }
    }

    public void setBeaconEventListener(OnBeaconEventListener listener) {
        mBeaconEventListener = listener;
    }

    /* Using as a bound service to allow event callbacks */
    private LocalBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
        public BeaconScannerService getService() {
            return BeaconScannerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.println(Log.ASSERT,TAG, "onbind");
        return mBinder;
    }
    /* Process each unique BLE scan result */
    private ScanCallback mScanCallback = new ScanCallback() {
        private Handler mCallbackHandler =
                new Handler(Looper.getMainLooper());

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            processResult(result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.w(TAG, "Scan Error Code: " + errorCode);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        private void processResult(ScanResult result) {
            byte[] data = result.getScanRecord().getServiceData(UID_SERVICE);

            if (data == null) {
                return;
            }

            final String deviceAddress = result.getDevice().getAddress();
            final int rssi = result.getRssi();
            byte frameType = data[0];

            switch (frameType) {
                case TYPE_UID:
                    final String id = Beacon.getInstanceId(data);
                    mCallbackHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            processUidPacket(deviceAddress, rssi, id);
                        }
                    });
                    break;
                default:
                    Log.w(TAG, "Invalid Eddystone scan result. ");
            }
        }
    };

}
