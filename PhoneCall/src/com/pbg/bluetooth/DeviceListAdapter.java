package com.pbg.bluetooth;


import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import xu.ye.R;

import java.util.ArrayList;


/**
 * 设备列表listview的适配器
 *
 * @author Jason
 * @see android.widget.BaseAdapter
 */
public class DeviceListAdapter extends BaseAdapter {
    private ArrayList<DeviceInfo> mLeDevices;
    private LayoutInflater mInflator;

    public DeviceListAdapter(Context context) {
        mLeDevices = new ArrayList<DeviceInfo>();
        mInflator = LayoutInflater.from(context);
    }

    public void addDevice(DeviceInfo device) {
        for(DeviceInfo dev :mLeDevices)
        {
        	if(dev.getDevice().getAddress().equals(device.getDevice().getAddress()))
        		return;
        }
        mLeDevices.add(device);
    }

    public DeviceInfo getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflator.inflate(R.layout.device_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
            viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        BluetoothDevice device = mLeDevices.get(i).getDevice();
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHolder.deviceName.setText(deviceName);
        viewHolder.deviceAddress.setText(device.getAddress() + " RSSI:" + mLeDevices.get(i).getRssi());

        return view;
    }

    private class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}

