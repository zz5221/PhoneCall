package com.pbg.uitl;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import xu.ye.R;

public class OperatingMessage {
    private Context mContext;
    private ContentResolver mContentResolver;
    public OperatingMessage(Context mContext,ContentResolver mContentResolver) {
        super();
        this.mContext = mContext;
        this.mContentResolver = mContentResolver;
    }
    
    /**
     * insert message to send
     * @param address
     * @param message
     * @return
     */
    public  Uri insertMessageTOSend(String address, String message){
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("body", message);
        Uri uri = mContentResolver.insert(Uri.parse("content://sms/sent"), values);
        return uri;
    }
    /**
     * insert message to Inbox
     * @param address
     * @param message
     * @return
     */
    public  Uri insertMessageToInbox(String address,String message){
        if( address == null || address.equals("")|| message == null ||message.equals("")){
            Toast.makeText(mContext,mContext.getString(R.string.no_message_operation), Toast.LENGTH_SHORT).show();
            return null;
        }
        ContentValues values = new ContentValues();
        values.put("address", address);
        values.put("type", "1");
        values.put("read", "0");
        values.put("body", message);
        values.put("person", "test");
        Uri uri = mContentResolver.insert(Uri.parse("content://sms/inbox"), values);
        return uri;
    }
    
    /**Delete all SMS one by phone num
     * @param phoneNum
     */
    public  void deleteSMS(String phoneNum) {
        try {
            mContentResolver.delete(Uri.parse("content://sms/"), "address in (?, ?)", new String[] { phoneNum, "+86" + phoneNum});
        } catch (Exception e) {
            
        }
    }
    
    /**Delete all SMS one by message id
     * @param phoneNum
     */
    public  void deleteSMSByMessageId(int id) {
        try {
            mContentResolver.delete(Uri.parse("content://sms"), "_id=?", new String []{id+""});
        } catch (Exception e) {
            
        }
    }
}
