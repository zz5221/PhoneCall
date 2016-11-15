package com.pbg.bluetooth;

import android.bluetooth.BluetoothDevice;

public class DeviceInfo {
	private BluetoothDevice device;
	private int	rssi;
	
	DeviceInfo(BluetoothDevice device, int rssi)
	{
		this.device = device;
		this.rssi = rssi;
	}
	public BluetoothDevice getDevice()
	{
		return device;
	}
	
	public int getRssi()
	{
		return rssi;
	}
}

