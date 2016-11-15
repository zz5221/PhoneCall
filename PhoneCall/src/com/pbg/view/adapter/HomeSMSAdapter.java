package com.pbg.view.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.pbg.bean.SMSBean;

import xu.ye.R;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HomeSMSAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<SMSBean> list;
	private Context context;
	private Date d;
	private SimpleDateFormat sdf;
	public List<SMSBean> getList() {
		return list;
	}

	public void setList(List<SMSBean> list) {
		this.list = list;
	}

	public HomeSMSAdapter(Context context) {     
		mInflater = LayoutInflater.from(context); 
		this.list = new ArrayList<SMSBean>();
		this.context=context;
		this.d=new Date();
		this.sdf=new SimpleDateFormat("MM/dd HH:mm");
	}   

	public void assignment(List<SMSBean> list){
		this.list = list;
	}
	public void add(SMSBean bean) {
		list.add(bean);
	}
	public void remove(int position){
		list.remove(position);
	}
	public int getCount() {  
		return list.size();     
	}            
	public SMSBean getItem(int position) {    
		return list.get(position);     
	}          
	public long getItemId(int position) {
		return 0;   
	}           
	public View getView(int position, View convertView, ViewGroup parent) {   

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.home_sms_list_item, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);  
			holder.count = (TextView) convertView.findViewById(R.id.count);  
			holder.date = (TextView) convertView.findViewById(R.id.date);  
			holder.content = (TextView) convertView.findViewById(R.id.content);  
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.name.setText(getPersonName(list.get(position).getAddress()));
		holder.count.setText("(" + list.get(position).getMsg_count() + ")");
		
		this.d.setTime(list.get(position).getDate());
		holder.date.setText(this.sdf.format(d));
		
		holder.content.setText(list.get(position).getMsg_snippet());

		convertView.setTag(holder);
		return convertView;
	}   

	public final class ViewHolder {   
		public TextView name;        
		public TextView count;        
		public TextView date;        
		public TextView content;        
	}
	
	public String getPersonName(String number) {   
        String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME, };   
        Cursor cursor = context.getContentResolver().query(   
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,   
                projection,    
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = '" + number + "'",  
                null,        
                null);  
        if( cursor == null ) {   
            return number;   
        }   
        String name = number;
        for( int i = 0; i < cursor.getCount(); i++ ) {   
            cursor.moveToPosition(i);   
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));   
        }
        cursor.close();
        return name;   
    }
}
