package com.rbu.bluetoothtest;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.List;
import java.util.Set;

/**
 * @创建者 liuyang
 * @创建时间 2018/6/7 11:23
 * @描述 ${TODO}
 * @更新者 $Author$
 * @更新时间 $Date$
 * @更新描述 ${TODO}
 */

public class BluetoothService extends Service {

    private BleBinder mBleBinder;

    private Handler mBleHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBleBinder;
    }

    public class BleBinder extends Binder {



    }

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mTargetDevice;

    private List<BluetoothDevice> mDevices;



    private static int REQUEST_ENABLE_BT = 1;

    /**
     * 检查是否支持蓝牙
     * @return
     */
    private boolean checkIfSupportBle() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * 获取BluetoothAdapter
     * @return
     */
    private BluetoothAdapter getAdapter() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = mBluetoothManager.getAdapter();
        }else {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    private void enableBluetooth() {
        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            ((Activity)getApplicationContext()).startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private void getBoundDevice() {
        Set<BluetoothDevice> boundDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : boundDevices) {

        }
    }

    /**
     * 开始扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void startScan() {
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    /**
     * 停止扫描
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void stopScan() {
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    /**
     * 蓝牙扫描返回结果的回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            mDevices.add(device);
        }
    };


}
