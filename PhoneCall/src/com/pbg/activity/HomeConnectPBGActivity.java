package com.pbg.activity;

import com.pbg.bluetooth.BluetoothInstance;
import com.pbg.bluetooth.BluetoothUtils;
import com.pbg.bluetooth.DeviceInfo;

import android.Manifest;
import android.R.string;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import xu.ye.R;

public class HomeConnectPBGActivity extends Activity {
    private ToggleButton bleSwitch;
    private BluetoothAdapter blueadapter;
    private Button btScan;
    private ListView lvDevices;
    private TextView mDeviceName;
    protected static final int PERMISSION_REQUEST_COARSE_LOCATION = 0;
    private final String ReceiveSMS = "com.pbg.activity.receive.message";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pbg);
        intView();
        getBaseContext().registerReceiver(mReceiver, makeFilter());
        blueadapter=BluetoothAdapter.getDefaultAdapter(); 
        //蓝牙初始化
        if (BluetoothInstance.getInfo() == null) {
            BluetoothInstance.setInfo(new BluetoothUtils(this, mHandler));
            BluetoothInstance.getInstance().initialize();
            if(BluetoothUtils.mBleAdapter.isEnabled()){  
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  
                    if (ContextCompat.checkSelfPermission(BluetoothUtils.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {  
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);  
                            builder.setTitle("This app needs location access");  
                            builder.setMessage("Please grant location access so this app can detect Bluetooth.");  
                            builder.setPositiveButton(android.R.string.ok, null);  
                            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {  
                                @SuppressLint("NewApi")
                                @Override  
                                public void onDismiss(DialogInterface dialog) {  
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);  
                                    }  
                                }  
                            });  
                            builder.show();  
                        }  
                    }  
                }
        }
    }
    private void intView(){
        mDeviceName = (TextView)findViewById(R.id.devices_name);
        bleSwitch = (ToggleButton)findViewById(R.id.mTogBtn);
        OnCheckedChangeListener listener = new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    blueadapter.enable();
                    btScan.setEnabled(true);
                }else{
                    blueadapter.disable();
                    btScan.setEnabled(false);
                }
            }
        };
        bleSwitch.setOnCheckedChangeListener(listener);
        btScan = (Button)findViewById(R.id.scan);
        btScan.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (btScan.getText().equals("Scan BLE Device")) {
                    if(!BluetoothInstance.getInstance().NoBlueDebug)
                    {
                        BluetoothInstance.getInstance().scanBleDevice(true);
                    }
                } else if (btScan.getText().equals("Disconnect")) {
                    if(!BluetoothInstance.getInstance().NoBlueDebug)
                    {
                        BluetoothInstance.getInstance().checkGattConnected();
                        btScan.setText(R.string.scan_ble_device);
                    }
                }
            }
        });
        lvDevices = (ListView)findViewById(R.id.deviceslist);
        lvDevices.setOnItemClickListener(new OnItemClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                BluetoothDevice device = BluetoothInstance.getInstance().mDeviceListAdapter.getDevice(position).getDevice();
                BluetoothInstance.getInstance().mBluetoothGatt = device.connectGatt(getBaseContext(), false,BluetoothInstance.getInstance().mGattCallback);
                BluetoothInstance.getInstance().mBluetoothGatt.requestMtu(100);
            }
        });
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        isBlueToothEnabled();
    }
    private void isBlueToothEnabled (){
        BluetoothAdapter blueadapter=BluetoothAdapter.getDefaultAdapter(); 
        if(null != blueadapter){
            if(blueadapter.isEnabled()){
                bleSwitch.setChecked(true);
            }else{
                bleSwitch.setChecked(false);
            }
        }
    }
    
    private IntentFilter makeFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        return filter;
    }
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch(action){
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch(blueState){
                case BluetoothAdapter.STATE_TURNING_ON:
                    break;
                case BluetoothAdapter.STATE_ON:
                    bleSwitch.setChecked(true);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    break;
                case BluetoothAdapter.STATE_OFF:
                    bleSwitch.setChecked(false);
                    break;
                }
                break;
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connect_pbg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    //蓝牙
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothUtils.ENABLE_BLUETOOTH:
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(intent, 1);
                    break;

                case BluetoothUtils.DEVICE_SCAN_STARTED:
                    btScan.setText(R.string.scanning);
                    break;

                case BluetoothUtils.DEVICE_SCAN_STOPPED:
                    btScan.setText(R.string.scan_ble_device);
                    break;

                case BluetoothUtils.DEVICE_SCAN_COMPLETED:
                    lvDevices.setAdapter(BluetoothInstance.getInstance().mDeviceListAdapter);
                    btScan.setText(R.string.scan_ble_device);
                    break;

                case BluetoothUtils.DEVICE_CONNECTED:
                    String connected = getString(R.string.connected);
                    mDeviceName.setText(connected+BluetoothInstance.getInstance().getDeviceName());
                    break;

//                case BluetoothUtils.DATA_SENDED:
//                    if (mDataSendFormat.getText().equals("Hex")) {
//                        mEditBox.setText(getFormattedString());
//                    }
//                    mSendBytes.setText(sendBytes + " ");
//                    break;
//
                case BluetoothUtils.DATA_READED:
                    //发送广播收到短信
                    sendBroadcastReceiver();
                    displayData();
                    break;

                case BluetoothUtils.CHARACTERISTIC_ACCESSIBLE:
                    btScan.setText("Disconnect");
//                  isTimerEnable = true;
//                  setTimer(isTimerEnable);
                    break;

                /*case DATA_REFRESH:
                    mRecvBytes.setText(recvBytes + " ");
                    mSendBytes.setText(sendBytes + " ");
                    break;*/

                default:
                    break;
            }
        }
    };
    /**
     * 展示数据
     */
    private void displayData() {
        StringBuilder mData = new StringBuilder();
        int len = BluetoothInstance.getInstance().getDataLen();
        BluetoothInstance.getInstance().recvBytes += len;
        byte[] buf = new byte[len];
        BluetoothInstance.getInstance().getData(buf, len);
        if (BluetoothInstance.getInstance().isASCII) {
            String s = BluetoothInstance.getInstance().asciiToString(buf);
            mData.append(s);
        } else {
            String s = BluetoothInstance.getInstance().bytesToString(buf);
            mData.append(s);
        }
        Toast.makeText(HomeConnectPBGActivity.this, mData.toString(),Toast.LENGTH_LONG).show();;
    }
    private void sendBroadcastReceiver(){
        Intent intent = new Intent();
        intent.setAction(ReceiveSMS);
        sendBroadcast(intent);
    }
}
