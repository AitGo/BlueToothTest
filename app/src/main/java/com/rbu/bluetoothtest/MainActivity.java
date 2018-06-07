package com.rbu.bluetoothtest;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.beacon.Beacon;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleReadResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;

import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private BluetoothClient mClient;
    private final static int REQUEST_ENABLE_BT = 1;
    private boolean mScanning = false;

    private Button scan;

    private byte[] mBytes;
    private byte[] mBytesTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scan = (Button) findViewById(R.id.scan);
        scan.setOnClickListener(this);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
//            }
//        }
        mClient = BleClientUtils.getInstanceClient(this);
//        mBytes = new byte []{0,127,0,0};

//        for(int i = 0;i < mBytes.length;i++) {
//            mBytes = int2bytes(0xff);
//        }
        Integer b = Integer.parseInt("15",16);
        Integer g = Integer.parseInt("ff",16);
        Integer r = Integer.parseInt("23",16);
        Integer w = new Integer(100);
        mBytes = new byte[]{b.byteValue(),g.byteValue(),r.byteValue(),w.byteValue()};
//        AT+COLOR;
        char a = new Character('A');
        char t = new Character('T');
        char add = new Character('+');
        char c = new Character('C');
        char o = new Character('O');
        char l = new Character('L');
        char rr = new Character('R');

        mBytesTime = new byte[]{(byte)a,(byte)t,(byte)add,(byte)c,(byte)o,(byte)l,(byte)o,(byte)rr};
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onClick(View v) {
        if(mClient.isBluetoothOpened()) {

            SearchRequest request = new SearchRequest.Builder()
                    .searchBluetoothLeDevice(3000, 1)   // 先扫BLE设备3次，每次3s
//                    .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                    .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                    .build();

            mClient.search(request, new SearchResponse() {
                @Override  
                public void onSearchStarted() {

                }

                @Override
                public void onDeviceFounded(SearchResult device) {
                    Beacon beacon = new Beacon(device.scanRecord);
                    BluetoothLog.i(String.format("beacon for %s\n%s", device.getAddress(), beacon.toString()));
                    if(device.getName().equals("BLE-LED")){
                        final String mac = device.getAddress();
                        mClient.connect(mac, new BleConnectResponse() {
                            @Override
                            public void onResponse(int code, BleGattProfile data) {
                                if (code == 0) {
                                    final UUID uuid=UUID.fromString("0000ffb0-0000-1000-8000-00805f9b34fb");
                                    BleGattService service = data.getService(uuid);
                                    List<BleGattCharacter> list = service.getCharacters();
                                    for(BleGattCharacter bleGattCharacter : list) {
                                        if(bleGattCharacter.getUuid().toString().equals("0000ffb2-0000-1000-8000-00805f9b34fb")) {
                                            BluetoothLog.e("333");

                                        }
                                    }
                                    BluetoothLog.e("11111");

                                    final UUID uuid1=UUID.fromString("0000ffb2-0000-1000-8000-00805f9b34fb");
                                    mClient.write(mac, uuid, uuid1 , mBytes, new BleWriteResponse() {
                                        @Override
                                        public void onResponse(int code) {
                                            if (code == 0) {
                                                BluetoothLog.e("22222");
                                            }
                                        }
                                    });

                                    mClient.write(mac, uuid, uuid1 , mBytesTime, new BleWriteResponse() {
                                        @Override
                                        public void onResponse(int code) {
                                            if (code == 0) {
                                                BluetoothLog.e("22222");
                                                mClient.read(mac, uuid, uuid1, new BleReadResponse() {
                                                    @Override
                                                    public void onResponse(int code, byte[] data) {
                                                        BluetoothLog.e("data" + data.toString() + "   " + data[0]);
                                                    }
                                                });
                                            }
                                        }
                                    });


                                }

                            }
                        });
                    }
                }

                @Override
                public void onSearchStopped() {

                }

                @Override
                public void onSearchCanceled() {

                }
            });
        }else {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mClient.openBluetooth();

        }
    }
}
