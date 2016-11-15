package com.pbg.bluetooth;

import java.nio.ByteBuffer;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import xu.ye.R;


	public class MainActivity extends Activity implements View.OnClickListener {

//	    private final int DATA_REFRESH = 8;

	    protected static final int PERMISSION_REQUEST_COARSE_LOCATION = 0;
		private TextView mDeviceName;
	    private TextView mDataRecvText;
	    private TextView mRecvBytes;
	    private TextView mDataRecvFormat;
	    private EditText mEditBox;
	    private TextView mSendBytes;
	    private TextView mDataSendFormat;
	    private Button mScanDeviceBtn;

	    private long recvBytes;        // 当前接收的字节数
	    private long sendBytes;        // 当前发送的字节数
//	    private boolean isTimerEnable; // 定时器的使能标志
	    private StringBuilder mData;   // 要显示的数据
//	    private Timer timer;
	    private BluetoothUtils mBluetoothUtils;
	    private boolean NoBlueDebug = false;
	    private boolean sendSMSReady = false; //ÊÇ·ñ¿ÉÒÔ·¢ËÍ¶ÌÏûÏ¢
	    
	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_main);

	        initViews(); // ³õÊ¼»¯views
	        
	        if(!NoBlueDebug)
	        {
	        	mBluetoothUtils = new BluetoothUtils(this, mHandler);
	        	mBluetoothUtils.initialize();
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
	        
//	        timer = new Timer();
	        mData = new StringBuilder();

	        recvBytes = 0;
	        sendBytes = 0;

	    }
	    @Override  
	    public void onRequestPermissionsResult(int requestCode,  
	                                           String permissions[], int[] grantResults) {  
	      
	        switch (requestCode) {  
	            case PERMISSION_REQUEST_COARSE_LOCATION: {  
	                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {  
	                   // Log.d(TAG, "coarse location permission granted");  
	                    finish();  
	                } else {  
	                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);  
	                    builder.setTitle("Functionality limited");  
	                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");  
	                    builder.setPositiveButton(android.R.string.ok, null);  
	                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {  
	                        @Override  
	                        public void onDismiss(DialogInterface dialog) {  
	                        }  
	      
	                    });  
	                    builder.show();  
	                }  
	                return;  
	            }  
	        }  
	    }  
	    @Override
	    protected void onResume() {
	        super.onResume();
	        if(!NoBlueDebug)
	        {
	        	mBluetoothUtils.checkBluetoothEnabled();
	        }
	    }

	    @Override
	    protected void onStop() {
	        super.onStop();
	        if(!NoBlueDebug)
	        {
	        	mBluetoothUtils.checkDeviceScanning();
	        	mBluetoothUtils.checkGattConnected();
	        }
	       /* if (timer != null) {
	            isTimerEnable = false;
	        }*/
	    }

	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        if (requestCode == 1 && resultCode == Activity.RESULT_CANCELED) {
	            finish();
	        }
	    }

	    @Override
	    public void onClick(View v) {
	        switch (v.getId()) {
	            case R.id.data_received_format:
	                if (mDataRecvFormat.getText().equals("Ascii")) {
	                	 if(!NoBlueDebug)
	         	        {
	                		 mBluetoothUtils.convertText(mDataRecvFormat,
	                            R.string.data_format_hex);
	         	        }
	                } else {
	                	if(!NoBlueDebug)
	         	        {
	                		mBluetoothUtils.convertText(mDataRecvFormat,
	                            R.string.data_format_default);
	         	        }
	                }
	                break;

	            case R.id.data_sended_format:
	                if (mDataSendFormat.getText().equals("Ascii")) {
	                	if(!NoBlueDebug)
	         	        { 
	                		mBluetoothUtils.convertText(mDataSendFormat,
	                            R.string.data_format_hex);
	         	        }
	                } else {
	                	if(!NoBlueDebug)
	         	        {
	                		mBluetoothUtils.convertText(mDataSendFormat,
	                            R.string.data_format_default);
	         	        }
	                }
	                break;

	            case R.id.byte_received_text:
	                recvBytes = 0;
	                if(!NoBlueDebug)
	    	        {
	                	mBluetoothUtils.convertText(mRecvBytes, R.string.zero);
	    	        }
	                break;

	            case R.id.byte_send_text:
	                sendBytes = 0;
	                if(!NoBlueDebug)
	    	        {
	                	mBluetoothUtils.convertText(mSendBytes, R.string.zero);
	    	        }
	                break;

	            case R.id.scan_device_btn:
	                if (mScanDeviceBtn.getText().equals("Scan BLE Device")) {
	                	if(!NoBlueDebug)
	         	        {
	                		mBluetoothUtils.scanBleDevice(true);
	         	        }
	                } else if (mScanDeviceBtn.getText().equals("Disconnect")) {
	                	if(!NoBlueDebug)
	         	        {
	                		mBluetoothUtils.checkGattConnected();
	                		mScanDeviceBtn.setText(R.string.scan_ble_device);
	         	        }
	                }
	                break;

	            case R.id.send_data_btn:
	                onSendBtnClicked();
	                break;

	            case R.id.clean_data_btn:
	                mData.delete(0, mData.length());
	                mDataRecvText.setText(mData.toString());
	                break;

	            case R.id.clean_text_btn:
	                mEditBox.setText("");
	                break;

	            default:
	                break;
	        }
	    }

	    /**
         * 对各个view的实例化，设置点击事件监听器
         */
	    private void initViews() {
	        mDeviceName = (TextView) findViewById(R.id.device_name_text);
	        mDataRecvText = (TextView) findViewById(R.id.data_read_text);
	        mRecvBytes = (TextView) findViewById(R.id.byte_received_text);
	        mDataRecvFormat = (TextView) findViewById(R.id.data_received_format);
	        mEditBox = (EditText) findViewById(R.id.data_edit_box);
	        mSendBytes = (TextView) findViewById(R.id.byte_send_text);
	        mDataSendFormat = (TextView) findViewById(R.id.data_sended_format);
	        mScanDeviceBtn = (Button) findViewById(R.id.scan_device_btn);
	        Button mSendBtn = (Button) findViewById(R.id.send_data_btn);
	        Button mCleanBtn = (Button) findViewById(R.id.clean_data_btn);
	        Button mCleanTextBtn = (Button) findViewById(R.id.clean_text_btn);

	        mDataRecvFormat.setOnClickListener(this);
	        mDataSendFormat.setOnClickListener(this);
	        mRecvBytes.setOnClickListener(this);
	        mSendBytes.setOnClickListener(this);
	        mScanDeviceBtn.setOnClickListener(this);
	        mCleanBtn.setOnClickListener(this);
	        mSendBtn.setOnClickListener(this);
	        mCleanTextBtn.setOnClickListener(this);
	        mDataRecvText.setMovementMethod(ScrollingMovementMethod.getInstance());
	    }

	    /**
         * 发送按钮点击后调用，写入数据
         */
	    private void onSendBtnClicked() {
	    	
	     if(sendSMSReady)
	     {
	        if (mDataSendFormat.getText().equals("Ascii")) {
	            byte[] buf = mEditBox.getText().toString().trim().getBytes();
	            sendBytes += buf.length;
	            mBluetoothUtils.sendToBuf(buf, buf.length);
	           // mBluetoothUtils.writeData(buf);
	        } else {
	            byte[] buf = mBluetoothUtils.stringToBytes(getHexString());
	            sendBytes += buf.length;
	            mBluetoothUtils.sendToBuf(buf, buf.length);
	            //mBluetoothUtils.writeData(buf);
	        }
	     }
	     else
	     {
	    	 
	     }
	    }

	    /**
         * 得到16进制字符串，过滤掉不正确的字符
         *
         * @return 字符串
         */
	    private String getHexString() {
	        String s = mEditBox.getText().toString();
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < s.length(); i++) {
	            char c = s.charAt(i);
	            if (('0' <= c && c <= '9') || ('a' <= c && c <= 'f') ||
	                    ('A' <= c && c <= 'F')) {
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
         * @return 格式化后的字符串
         */
	    private String getFormattedString() {
	        String s = getHexString();
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < s.length() - 1; i += 2) {
	            sb.append(s.substring(i, i + 2));
	            sb.append(' ');
	        }
	        return sb.toString();
	    }

	    /**
         * 展示数据
         */
	    private void displayData() {
	    	int len = mBluetoothUtils.getDataLen();
	    	recvBytes += len;
	    	byte[] buf = new byte[len];
	    	
	    	mBluetoothUtils.getData(buf, len);
	    	
	    	if(buf[0] == 0x01)
	    	{//·¢ËÍ×¢²áÏûÏ¢ºó£¬ÊÕµ½PBGµÄ»ØÓ¦
	    		if(buf[1] == 0x0)
	    		{//·¢ËÍ³É¹¦
	    			sendSMSReady = true;
	    		}
	    		else
	    		{//´ËÊÖ»ú¶ÌÐÅ²»ºÏ·¨£¬
	    			sendSMSReady = false;
	    		}
	    	}
	    	else if(buf[0] == 0x03)
	    	{//·¢ËÍ¶ÌÐÅµÄ·µ»Ø½á¹û
	    		if(buf[1] == 0x0)
	    		{
	    			//·¢ËÍ³É¹¦
	    		}
	    		else
	    		{
	    			//ÒÑ·¢³öµÄÏûÏ¢£¬Ç°Ãæ¼Ó¸ö¡°X¡±ºÅ£¬ÀàËÆÓÚÊÖ»ú¶ÌÐÅ·¢²»³öÈ¥µÄÐ§¹û
	    		}
	    	}
	    	else if(buf[0] == 0x04)
	    	{//ÊÕµ½¶ÌÐÅ
	    		byte[] srcNum = new byte[4]; //¶Ô·½µÄµç»°ºÅÂë
	    		srcNum[0] = buf[2];
	    		srcNum[1] = buf[3];
	    		srcNum[2] = buf[4];
	    		srcNum[3] = buf[5];
	    	 //Ê£ÏÂÄÚÈÝÎª¶ÌÐÅÄÚÈÝ£¬ÐèÒª½«ËüÏÔÊ¾³öÀ´¡£	
	    		
	    	}
	    	
	        if (mDataRecvFormat.getText().equals("Ascii")) {
	            String s = mBluetoothUtils.asciiToString(buf);
	            mData.append(s);
	        } else {
	            String s = mBluetoothUtils.bytesToString(buf);
	            mData.append(s);
	        }
	        mDataRecvText.setText(mData.toString());
	        mRecvBytes.setText(recvBytes + " ");
	    }

	    /**
         * 设置定时器，递归调用不断定时来实时更新接受和发送的字节数
         *
         * @param enable 使能标志
         */
	    /*private void setTimer(boolean enable) {
	        if (enable) {
	            timer.schedule(new TimerTask() {
	                @Override
	                public void run() {
	                    Message message = new Message();
	                    message.what = DATA_REFRESH;
	                    mHandler.sendMessage(message);

	                    setTimer(isTimerEnable);
	                }
	            }, 2000);
	        }
	    }*/
	    private void sendRegMsg(String srcNum)
	    {
            byte[] reg = new byte[5];
            int tel = Integer.parseInt(srcNum);
            reg[0] = 0x01;
            reg[4] = (byte) (tel & 0xff);// ×îµÍÎ»   
            reg[3] = (byte) ((tel >> 8) & 0xff);// ´ÎµÍÎ»   
            reg[2] = (byte) ((tel >> 16) & 0xff);// ´Î¸ßÎ»   
            reg[1] = (byte) (tel >>> 24);// ×î¸ßÎ»,ÎÞ·ûºÅÓÒÒÆ¡£  
            mBluetoothUtils.sendToBuf(reg, 5);
	    }
	    
	    public void sendSMSMsg(String sms, String dstNum)
	    {
	    	int msgLen = dstNum.length();
	    	byte[] SMS = new byte[6 + msgLen];
	    	int tel = Integer.parseInt(dstNum);
	    	SMS[0] = 0x03;
	    	SMS[1] = 0x0;
	    	SMS[5] = (byte) (tel & 0xff);// ×îµÍÎ»   
	    	SMS[4] = (byte) ((tel >> 8) & 0xff);// ´ÎµÍÎ»   
	    	SMS[3] = (byte) ((tel >> 16) & 0xff);// ´Î¸ßÎ»   
	    	SMS[2] = (byte) (tel >>> 24);// ×î¸ßÎ»,ÎÞ·ûºÅÓÒÒÆ¡£
	    	System.arraycopy(dstNum.getBytes(),0, SMS, 6, msgLen);
	    	mBluetoothUtils.sendToBuf(SMS, 6+msgLen);
	    }
	    private Handler mHandler = new Handler() {
	        @Override
	        public void handleMessage(Message msg) {
	            switch (msg.what) {
	                case BluetoothUtils.ENABLE_BLUETOOTH:
	                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	                    startActivityForResult(intent, 1);
	                    break;

	                case BluetoothUtils.DEVICE_SCAN_STARTED:
	                    mScanDeviceBtn.setText(R.string.scanning);
	                    break;

	                case BluetoothUtils.DEVICE_SCAN_STOPPED:
	                    mScanDeviceBtn.setText(R.string.scan_ble_device);
	                    break;

	                case BluetoothUtils.DEVICE_SCAN_COMPLETED:
	                    mBluetoothUtils.creatDeviceListDialog();
	                    mScanDeviceBtn.setText(R.string.scan_ble_device);
	                    break;

	                case BluetoothUtils.DEVICE_CONNECTED:
	                    mDeviceName.setText(mBluetoothUtils.getDeviceName());

	                    //´Ë´¦µ÷ÓÃ»ñÈ¡µç»°ºÅÂë½Ó¿Ú
	                    String srcNumber = "1381111";
	                    sendRegMsg(srcNumber);
	                    break;

	                case BluetoothUtils.DATA_SENDED:
	                    if (mDataSendFormat.getText().equals("Hex")) {
	                        mEditBox.setText(getFormattedString());
	                    }
	                    mSendBytes.setText(sendBytes + " ");
	                    break;

	                case BluetoothUtils.DATA_READED:
	                    displayData();
	                    break;

	                case BluetoothUtils.CHARACTERISTIC_ACCESSIBLE:
	                    mScanDeviceBtn.setText("Disconnect");
//	                    isTimerEnable = true;
//	                    setTimer(isTimerEnable);
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

	}
