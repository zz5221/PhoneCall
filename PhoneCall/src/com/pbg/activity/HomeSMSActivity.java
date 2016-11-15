package com.pbg.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pbg.application.MyApplication;
import com.pbg.bean.SMSBean;
import com.pbg.uitl.BaseIntentUtil;
import com.pbg.uitl.RexseeSMS;
import com.pbg.view.adapter.HomeSMSAdapter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import xu.ye.R;

public class HomeSMSActivity extends Activity implements OnCreateContextMenuListener {

    private ListView listView;
    private HomeSMSAdapter adapter;
    private RexseeSMS rsms;
    private final String ReceiveSMSAction = "com.pbg.activity.receive.message";
    private final IntentFilter ReceiveSMS = new IntentFilter(ReceiveSMSAction);
    private BroadcastReceiver SMSReceiver = new BroadcastReceiver(){
        
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            refreshView();
        }
    };
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        registerReceiver(SMSReceiver,ReceiveSMS);
        refreshView();
    }
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(SMSReceiver);
    }
    private void refreshView() {
        List<SMSBean> list_mmt = rsms.getThreadsNum(rsms.getThreads(0));
        List<SMSBean> smsBeans = new ArrayList<SMSBean>();
        smsBeans = list_mmt;
        adapter.assignment(smsBeans);
        listView.setAdapter(adapter);

    }

    public void intView() {
        setContentView(R.layout.home_sms_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String, String> map = new HashMap<String, String>();
                SMSBean sb = adapter.getItem(position);
                map.put("phoneNumber", sb.getAddress());
                map.put("threadId", sb.getThread_id());
                BaseIntentUtil.intentSysDefault(HomeSMSActivity.this, MessageBoxList.class, map);
            }
        });
        listView.setOnCreateContextMenuListener(this);
        adapter = new HomeSMSAdapter(HomeSMSActivity.this);
        rsms = new RexseeSMS(HomeSMSActivity.this);
    }

    /**
     * Creat context menu
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(getString(R.string.operating));
        menu.add(0, 0, 0, getString(R.string.delect));
    }

    /**
     * Event handling of the context menu
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 0:
            SMSBean sms = (SMSBean) adapter.getItem(item.getItemId());
            if (sms != null) {
                deleteSMS(sms.getAddress());
            }
            adapter.getList().remove(sms);
            adapter.notifyDataSetChanged();
            break;
        default:
            break;
        }
        return super.onContextItemSelected(item);
    }

    /*
     * Delete all SMS one by phone num
     */
    public void deleteSMS(String nub) {
        try {
            ContentResolver CR = getContentResolver();
            CR.delete(Uri.parse("content://sms/"), "address in (?, ?)", new String[] { nub, "+86" + nub });
            Toast.makeText(HomeSMSActivity.this, getString(R.string.Delete_SMS_successfully), 2000).show();
        } catch (Exception e) {
            Toast.makeText(HomeSMSActivity.this, getString(R.string.Deleting_MS_failed), 2000).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ((MyApplication) getApplication()).promptExit(this);
            return true;
        }
        return false;
    }
}
