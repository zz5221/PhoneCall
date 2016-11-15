package com.pbg.activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.pbg.bean.MessageBean;
import com.pbg.bluetooth.BluetoothInstance;
import com.pbg.bluetooth.BluetoothUtils;
import com.pbg.uitl.BaseUtil;
import com.pbg.uitl.OperatingMessage;
import com.pbg.uitl.OperatingString;
import com.pbg.view.adapter.MessageBoxListAdapter;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import xu.ye.R;

/**
 * @author 33257
 *
 */
public class MessageBoxList extends Activity {

	private ListView talkView;
	private List<MessageBean> list = null;
	private Button fasong;
	private Button btn_return;
	private EditText neirong;
	private SimpleDateFormat sdf;
	private AsyncQueryHandler asyncQuery;
	private String address;
	private String thread;
	private final String ReceiveSMSAction = "com.pbg.activity.receive.message";
	private final IntentFilter ReceiveSMS = new IntentFilter(ReceiveSMSAction);
	private BroadcastReceiver SMSReceiver = new BroadcastReceiver(){
	    
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            init(thread);
        }
	};
	public void onCreate(Bundle savedInstanceState){

		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_messageboxlist);
		btn_return=(Button) findViewById(R.id.btn_return);
		fasong=(Button) findViewById(R.id.fasong);
		neirong=(EditText) findViewById(R.id.neirong);
		thread=getIntent().getStringExtra("threadId");
		address=getIntent().getStringExtra("phoneNumber");
		TextView tv=(TextView) findViewById(R.id.topbar_title);
		tv.setText(getPersonName(address));
		sdf= new SimpleDateFormat("MM-dd HH:mm");
		init(thread);
		btn_return.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MessageBoxList.this.setResult(RESULT_OK);
				MessageBoxList.this.finish();
			}
		});
		fasong.setOnClickListener(new OnClickListener() {
		    
			public void onClick(View v) {
	             String nei=neirong.getText().toString();
	            //测验发送广播
	             sendBroadcastReceiver();
	            //向收件箱保存短信
	             OperatingMessage operatingMessage = new OperatingMessage(MessageBoxList.this, getContentResolver());
	             operatingMessage.insertMessageToInbox(address, nei);
			    //调用蓝牙发送短信
			    if (BluetoothInstance.getInfo() != null && nei!=null && !nei.equals("")) {
			        BluetoothInstance.getInstance().onSendBtnClicked(nei);
			        BluetoothInstance.getInstance().toString();
		        }
			    //保存短信
				Cursor cursor=getContentResolver().query(insertMessageTOSend(address,nei), new String []{"_id"}, null, null, null);
				String date = sdf.format(new java.util.Date());
				if(cursor.moveToNext()){
				MessageBean d = new MessageBean(cursor.getInt(cursor.getColumnIndex("_id")),address,
						date,
						nei,
						R.layout.list_say_me_item);
    				list.add(d);
				}
				if(null ==talkView.getAdapter()){
				    talkView.setAdapter(new MessageBoxListAdapter(MessageBoxList.this, list));
                    talkView.setDivider(null);
                    talkView.setSelection(list.size());
				}
                ((MessageBoxListAdapter)talkView.getAdapter()).notifyDataSetChanged();
                neirong.setText("");
                BaseUtil.HideKeyboard(neirong);
                Toast.makeText(MessageBoxList.this, nei, Toast.LENGTH_SHORT).show();
			}
		});
	}
	@Override
	protected void onResume() {
	    // TODO Auto-generated method stub
	    super.onResume();
	    registerReceiver(SMSReceiver,ReceiveSMS);
	}
	@Override
	protected void onDestroy() {
	    // TODO Auto-generated method stub
	    super.onDestroy();
	    unregisterReceiver(SMSReceiver);
	}

	/**
	 * test for BroadCastReceiver
	 */
	private void sendBroadcastReceiver(){
        Intent intent = new Intent();
        intent.setAction(ReceiveSMSAction);
        sendBroadcast(intent);
    }
	private void init(String thread){
		asyncQuery = new MyAsyncQueryHandler(getContentResolver());
		talkView = (ListView) findViewById(R.id.list);
		list = new ArrayList<MessageBean>();
		Uri uri = Uri.parse("content://sms"); 
		String[] projection = new String[] {
				"_id",
				"date",
				"address",
				"person",
				"body",
				"type"
		};// 查询的列
		asyncQuery.startQuery(0, null, uri, projection, "thread_id = " + thread, null,
				"date asc");
	}
	
	/**
	 * 数据库异步查询类AsyncQueryHandler
	 * 
	 * @author administrator
	 * 
	 */
    private class MyAsyncQueryHandler extends AsyncQueryHandler{

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }
        protected void onQueryComplete(int token, Object cookie, Cursor cursor){
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String date = sdf.format(new Date(cursor.getLong(cursor.getColumnIndex("date"))));
                    if (cursor.getInt(cursor.getColumnIndex("type")) == 1) {
                        MessageBean d = new MessageBean(cursor.getInt(cursor.getColumnIndex("_id")),
                                cursor.getString(cursor.getColumnIndex("address")), date,
                                cursor.getString(cursor.getColumnIndex("body")), R.layout.list_say_he_item);
                        list.add(d);
                    } else {
                        MessageBean d = new MessageBean(cursor.getInt(cursor.getColumnIndex("_id")),
                                cursor.getString(cursor.getColumnIndex("address")), date,
                                cursor.getString(cursor.getColumnIndex("body")), R.layout.list_say_me_item);
                        list.add(d);
                    }
                }
                if (list.size() > 0) {
                    List<MessageBean> beans = new ArrayList<MessageBean>();
                    for (int i = 0; i < list.size(); i++) {
                        beans.add(list.get(i));
                    }
                    list = beans;
                }
                talkView.setAdapter(new MessageBoxListAdapter(MessageBoxList.this, list));
                talkView.setDivider(null);
                talkView.setSelection(list.size());
            } else {
                Toast.makeText(MessageBoxList.this,getString(R.string.no_message_operation), Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	/**
	 * get person number by phone number 
	 * @param number
	 * @return
	 */
	public String getPersonName(String number){   
		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, };   
		Cursor cursor = this.getContentResolver().query(   
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
				projection,    
				ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",  
				null,        
				null);  
		if( cursor == null ) {   
			return number;   
		}   
		String name = number;
		for( int i = 0; i < cursor.getCount(); i++ ){   
			cursor.moveToPosition(i);   
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));   
		}
		cursor.close();
		return name;   
	} 
	
	/**
	 * insert message to Inbox
	 * @param address
	 * @param message
	 * @return
	 */
	public Uri insertMessageToInbox(String address,String message){
	    if( address == null || address.equals("")|| message == null ||message.equals("")){
	        Toast.makeText(MessageBoxList.this,getString(R.string.no_message_operation), Toast.LENGTH_SHORT).show();
	        return null;
	    }
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("type", "1");
        values.put("read", "0");
        values.put("body", message);
        values.put("person", "test");
        Uri uri = this.getApplicationContext().getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
        return uri;
	}

    /**
     * insert message to send
     * @param address
     * @param message
     * @return
     */
    public Uri insertMessageTOSend(String address, String message){
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("body", message);
        Uri uri = getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        return uri;
    }
}
