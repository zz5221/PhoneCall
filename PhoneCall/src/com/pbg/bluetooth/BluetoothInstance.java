package com.pbg.bluetooth;

public class BluetoothInstance {
    private static BluetoothUtils mBluetoothUtils = null;
    public static String mConnectedDeviceName = "";
    public static BluetoothUtils getInfo() {
        return mBluetoothUtils;
    }

    public static void setInfo(BluetoothUtils mBluetoothUtils) {
        BluetoothInstance.mBluetoothUtils = mBluetoothUtils;
    }

    public static BluetoothUtils getInstance() {
        if(null != mBluetoothUtils){
            return mBluetoothUtils;
        }
        return null;
    }
}
