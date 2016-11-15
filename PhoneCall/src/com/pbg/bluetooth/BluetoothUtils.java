package com.pbg.bluetooth;


import java.util.Timer;
import java.util.UUID;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import xu.ye.R;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;


/**
 * 简化蓝牙操作的工具类
 */
@SuppressLint("NewApi")
public class BluetoothUtils {

    public static final int ENABLE_BLUETOOTH = 0;          // 发现蓝牙未开启发送的开启蓝牙消息
    public static final int DEVICE_SCAN_STARTED = 1;       // 扫描设备开始时发送的消息
    public static final int DEVICE_SCAN_STOPPED = 2;       // 扫描终止时发送的消息
    public static final int DEVICE_SCAN_COMPLETED = 3;     // 扫描设备完成时发送的消息
    public static final int DEVICE_CONNECTED = 4;          // 连接上设备时发送的消息
    public static final int DATA_SENDED = 5;               // 发送数据后发送清除edittext内容的消息
    public static final int DATA_READED = 6;               // 读取到数据后发送使适配器更新的消息
    public static final int CHARACTERISTIC_ACCESSIBLE = 7; // 可操作特征值时发送的消息

    private boolean mScanning;                             // 设备扫描状态的标志־
    private byte[] readedData;                             // 读取到的字节数组数据
    public static Context context;
    private Handler handler;
    public static BluetoothAdapter mBleAdapter;
    public BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic mCharacteristic;
    public DeviceListAdapter mDeviceListAdapter;

    private DataBuffer dataBuffer;
    private DataBuffer sendDataBuffer;
    private Timer sendTimer;
	private boolean needConnect = true;
    public boolean isASCII = false;
    public long recvBytes;        // 当前接收的字节数
    public long sendBytes;        // 当前发送的字节数
    public boolean NoBlueDebug = false;
    private boolean sendSMSReady = false; //是否可以发送短消息
    
    public BluetoothUtils(Context context, Handler handler) {
        BluetoothUtils.context = context;
        this.handler = handler;
        
        dataBuffer = new DataBuffer(4096);
        sendDataBuffer = new DataBuffer(4096);
     
    }

    public void initialize() {
        BluetoothManager manager = (BluetoothManager)
                context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleAdapter = manager.getAdapter();
        mDeviceListAdapter = new DeviceListAdapter(context);
    }

    /**
     * 检测蓝牙开启状态，若未开启则发送开启蓝牙消息
     */
    public void checkBluetoothEnabled() {
        if (mBleAdapter == null || !mBleAdapter.isEnabled()) {
            Message message = new Message();
            message.what = ENABLE_BLUETOOTH;
            handler.sendMessage(message);
        }
    }

    /**
     * 检测当前设备扫描的状态，若在扫描中则停止扫描
     */
    public void checkDeviceScanning() {
        if (mScanning) {
            scanBleDevice(false);
        }
    }

    /**
     * 检测蓝牙连接状态，若已连接则断开并关闭连接
     */
    public void checkGattConnected() {
        if (mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mBluetoothGatt.disconnect();
                mBluetoothGatt.close();
            }
        }
        needConnect = false;
    }

    /**
     * 扫描设备的方法，扫描按钮点击后调用，扫描持续3秒
     *
     * @param enable 扫描方法的使能标志
     */
    @SuppressWarnings("deprecation")
	public void scanBleDevice(boolean enable) {
        if (enable) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBleAdapter.stopLeScan(mLeScanCallback);

                    Message message = new Message();
                    message.what = DEVICE_SCAN_COMPLETED;
                    handler.sendMessage(message);
                }
            }, 5000);
            mScanning = true;     
            //mBleAdapter.startLeScan(new UUID[] {UUID.fromString("0000F445-0000-1000-8000-00805F9B34FB"),UUID.fromString("0000FEE0-0000-1000-8000-00805F9B34FB")},mLeScanCallback);
            mBleAdapter.startLeScan(mLeScanCallback);
            Message message = new Message();
            message.what = DEVICE_SCAN_STARTED;
            handler.sendMessage(message);
        } else {
            mScanning = false;
            mBleAdapter.stopLeScan(mLeScanCallback);

            Message message = new Message();
            message.what = DEVICE_SCAN_STOPPED;
            handler.sendMessage(message);
        }
    }

    /**
     * 往特征值里写入数据的方法
     *
     * @param data 字节数组类型的数据
     */
    public void writeData() {
        if (mBluetoothGatt != null) {
            if (mBluetoothGatt.connect() && mCharacteristic != null) {
  	    	  int len = sendDataBuffer.getSize();
	    	  if (len==0)
	    	  {
	    		  return;
	    	  }
	    	  
  	    	  if (len>20)
  	    	  {
  	    		  len = 20;
  	    	  }
  	    	/*   
  	    	  int sendNum;
  	    	  
  	    	  if(len % 14 == 0)
  	    		  sendNum = len / 14;
  	    	  else
  	    		  sendNum = len / 14 + 1;
  	    	  
  	    	  for(int i = 0; i < sendNum; i++)
  	    	  {
  	    		  int sendLen = 14;
  	    		  
  	    		  if(i == sendNum - 1)
  	    			  sendLen = len - 14 * (sendNum - 1);

  	    		  byte[] buf = new byte[20];
  	    	      buf[0] = 0x03;
  	    	      if(i == 0)
  	    	      {
  	    	    	  if( sendNum > 1)
  	    	    	     buf[1] = 0x01;
  	    	    	  else
  	    	    		 buf[1] = 0x03;
  	    	      }
  	    	      else
  	    	      {
  	    	    	  if(i == sendNum - 1)
  	    	    		  buf[1] = 0x03;
  	    	    	  else
  	    	    		  buf[1] = 0x02;
  	    	      }
  	    	      
  	    	      buf[2] = '1';
  	    	      buf[3] = '2';
  	    	      buf[4] = '3';
  	    	      buf[5] = '4';
  	    	      
  	    	      byte[] bufData = new byte[14];
  	    	     
  	    		  sendDataBuffer.dequeue(bufData, sendLen);
  	    		  System.arraycopy(bufData, 0, buf, 6, sendLen);
  	    		 */ 
  	    	  		byte[] buf = new byte[20];
  	    	  		sendDataBuffer.dequeue(buf, len);
  	    		  mCharacteristic.setValue(buf);
  	    		  mBluetoothGatt.writeCharacteristic(mCharacteristic);
  	    	  //}
            }
        }
    }
    
    public void sendToBuf(byte[] data,int len){
    	sendDataBuffer.enqueue(data, len);
    	writeData();
    }
      

    /**
     * 创建一个新的设备列表对话框
     */
    public void creatDeviceListDialog() {
        if (mDeviceListAdapter.getCount() > 0) {
            new AlertDialog.Builder(context).setCancelable(true)
                .setAdapter(mDeviceListAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothDevice device = mDeviceListAdapter.getDevice(which).getDevice();
                        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
                        mBluetoothGatt.requestMtu(100);
                    }
                }).show();
        }
    }

    /**
     * 开启特征值的notification，然后才能读取数据
     */
    public void setCharacteristicNotification() {
        String clientUuid = "00002902-0000-1000-8000-00805f9b34fb";
        mBluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
        BluetoothGattDescriptor descriptor = mCharacteristic.
                getDescriptor(UUID.fromString(clientUuid));
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * 字节数组转化为标准的16进制字符串
     *
     * @param bytes 字节数组数据
     * @return 字符串
     */
    public String bytesToString(byte[] bytes) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[v >>> 4];
            hexChars[i * 2 + 1] = hexArray[v & 0x0F];

            sb.append(hexChars[i * 2]);
            sb.append(hexChars[i * 2 + 1]);
            sb.append(' ');
        }
        return sb.toString();
    }

    /**
     * 将字符串转为16进制值的字节数组
     *
     * @param s 字符串数据
     * @return buf 字节数组
     */
    public byte[] stringToBytes(String s) {
        byte[] buf = new byte[s.length() / 2];
        for (int i = 0; i < buf.length; i++) {
            try {
                buf[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return buf;
    }

    /**
     * Ascii编码的字节数组转化为对应的字符串
     *
     * @param bytes 字节数组
     * @return 字符串
     */
    public String asciiToString(byte[] bytes) {
        char[] buf = new char[bytes.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++) {
            buf[i] = (char) bytes[i];
            sb.append(buf[i]);
        }
        return sb.toString();
    }

    /**
     * 变换文本的方法，有动画效果
     *
     * @param textView      目标文本view对象
     * @param convertTextId 变换后的文本resId
     */
    public void convertText(final TextView textView, final int convertTextId) {
        final Animation scaleIn = AnimationUtils.loadAnimation(context,
                R.anim.text_scale_in);
        Animation scaleOut = AnimationUtils.loadAnimation(context,
                R.anim.text_scale_out);
        scaleOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setText(convertTextId);
                textView.startAnimation(scaleIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        textView.startAnimation(scaleOut);
    }

    /**
     * 获取已连接设备的设备名
     *
     * @return 字符串形式的设备名
     */
    public String getDeviceName() {
        return mBluetoothGatt.getDevice().getName();
    }

    public String getDeviceAdd() {
    	return mBluetoothGatt.getDevice().getAddress();
    }
    /**
     * 获取已读取的数据
     *
     * @return 字节数组数据
     */
    public byte[] getReadedData() {
        return readedData;
    }
    
    /**
     * 获取已读取的数据长度
     *
     * @return 
     */
    public int getDataLen() {
        return dataBuffer.getSize();
    }
    
    /**
     * 获取已读取的数据
     *
     * @return 
     */
    public int getData(byte[] data_out,int len) {
        return dataBuffer.dequeue(data_out, len);
    }
    
    private void connectAgain() {
        if (needConnect) { 
        	String bleAddress = getDeviceAdd();
            BluetoothDevice device = mBleAdapter.getRemoteDevice(bleAddress);
            mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
            mBluetoothGatt.requestMtu(100);
        }
    }
    
 
    /**
     * 连接Gatt之后的回调
     */
    public BluetoothGattCallback mGattCallback =
            new BluetoothGattCallback() {
        		private int supportedMTU;

				@Override
        		public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {  
        			super.onMtuChanged(gatt, mtu, status);  
          
        			if (status == BluetoothGatt.GATT_SUCCESS) {  
        				this.supportedMTU = mtu;//local var to record MTU size  
        			}  
        		} 
        		
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        Message message = new Message();
                        message.what = DEVICE_CONNECTED;
                        handler.sendMessage(message);

                   //     mDeviceListAdapter.clear();
                        gatt.discoverServices();
                    }
                    else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                    	
                    	connectAgain();
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt,
                        BluetoothGattCharacteristic characteristic) {
                    if (characteristic != null) {                      
                        dataBuffer.enqueue(characteristic.getValue(), characteristic.getValue().length);
                        Message message = new Message();
                        message.what = DATA_READED;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt,
                        BluetoothGattCharacteristic characteristic, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        Message message = new Message();
                        message.what = DATA_SENDED;
                        handler.sendMessage(message);
                    }
                    writeData();
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        // 得到目标特征值ֵ
                        String serviceUuid = "0000fee0-0000-1000-8000-00805f9b34fb";
                        String characterUuid = "0000fee1-0000-1000-8000-00805f9b34fb";
                        BluetoothGattService service = gatt.getService(UUID
                                .fromString(serviceUuid));
                        mCharacteristic = service.getCharacteristic(UUID
                                .fromString(characterUuid));

                      //开启通知
                        setCharacteristicNotification();

                        Message message = new Message();
                        message.what = CHARACTERISTIC_ACCESSIBLE;
                        handler.sendMessage(message);
                    }
                }
                
            };
            

    /**
     * 蓝牙扫描时的回调
     */
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                    String buf = bytesToString(scanRecord);
                    System.out.println("BluetoothUtils.enclosing_method()£º"+device.getName()+"\nscanRecord"+buf+"rssi:"+rssi);
                    
                    //if ("E0 FE".equals(buf.substring(0, buf.length()))) 
                    {
                        mDeviceListAdapter.addDevice(new DeviceInfo(device, rssi));
                         
                        mDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            };

    /**
     * 发送按钮点击后调用，写入数据
     */
    public void onSendBtnClicked(String message) {

        if (sendSMSReady) {
            if (BluetoothInstance.getInstance().isASCII) {
             //   byte[] buf = mEditBox.getText().toString().trim().getBytes();
                byte[]buf = message.trim().getBytes();
                sendBytes += buf.length;
                BluetoothInstance.getInstance().sendToBuf(buf, buf.length);
                // mBluetoothUtils.writeData(buf);
            } else {
                byte[] buf = BluetoothInstance.getInstance().stringToBytes(BluetoothUtils.getHexString(message));
                sendBytes += buf.length;
                BluetoothInstance.getInstance().sendToBuf(buf, buf.length);
                // mBluetoothUtils.writeData(buf);
            }
        } else {

        }
    }
     
//    /**
//     * 发送按钮点击后调用，写入数据
//     */
//    public void onSendBtnClicked(String text) {
//        if (isASCII) {
//            byte[] buf = text.trim().getBytes();
//            BluetoothInstance.getInstance().sendToBuf(buf, buf.length);
//            sendBytes += buf.length;
//        } else {
//            byte[] buf = BluetoothInstance.getInstance().stringToBytes(getHexString(text));
//            sendBytes += buf.length;
//            BluetoothInstance.getInstance().sendToBuf(buf, buf.length);
//        }
//    }

    /**
     * 得到16进制字符串，过滤掉不正确的字符
     *
     * @return 字符串
     */
    public static String getHexString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (('0' <= c && c <= '9') || ('a' <= c && c <= 'f') || ('A' <= c && c <= 'F')) {
                sb.append(c);
            }
        }
        if ((sb.length() % 2) != 0) {
            sb.deleteCharAt(sb.length());
        }
        return sb.toString();
    }

    /**
     * 在getHexString基础上每两个字符后加空格
     * 
     * @return 格式化后的字符串
     */
    private String getFormattedString(String s) {
        // String s = getHexString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length() - 1; i += 2) {
            sb.append(s.substring(i, i + 2));
            sb.append(' ');
        }
        return sb.toString();
    }

}
