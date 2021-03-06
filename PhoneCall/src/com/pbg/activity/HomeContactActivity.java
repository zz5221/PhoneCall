package com.pbg.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.pbg.application.MyApplication;
import com.pbg.bean.ContactBean;
import com.pbg.bean.GroupBean;
import com.pbg.bluetooth.BluetoothInstance;
import com.pbg.uitl.BaseIntentUtil;
import com.pbg.view.adapter.ContactHomeAdapter;
import com.pbg.view.adapter.MenuListAdapter;
import com.pbg.view.other.SizeCallBackForMenu;
import com.pbg.view.ui.MenuHorizontalScrollView;
import com.pbg.view.ui.QuickAlphabeticBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Groups;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import xu.ye.R;

public class HomeContactActivity extends Activity {

    private MenuHorizontalScrollView scrollView;
    private ListView menuList;
    private View acbuwaPage;
    private Button menuBtn;
    private MenuListAdapter menuListAdapter;
    private View[] children;
    private LayoutInflater inflater;
    private ContactHomeAdapter adapter;
    private ListView personList;
    private List<ContactBean> list;
    private static AsyncQueryHandler asyncQuery;
    private QuickAlphabeticBar alpha;
    private Button addContactBtn;
    private Map<Integer, ContactBean> contactIdMap = null;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        inflater = LayoutInflater.from(this);
        setContentView(inflater.inflate(R.layout.menu_scroll_view, null));
        scrollView = (MenuHorizontalScrollView) findViewById(R.id.mScrollView);
        menuListAdapter = new MenuListAdapter(this, queryGroup());
        menuList = (ListView) findViewById(R.id.menuList);
        menuList.setAdapter(menuListAdapter);
        acbuwaPage = inflater.inflate(R.layout.home_contact_page, null);
        menuBtn = (Button) this.acbuwaPage.findViewById(R.id.menuBtn);
        personList = (ListView) this.acbuwaPage.findViewById(R.id.acbuwa_list);
        alpha = (QuickAlphabeticBar) this.acbuwaPage.findViewById(R.id.fast_scroller);
        asyncQuery = new MyAsyncQueryHandler(getContentResolver());
        init();
        menuBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                
            }
        });
        if (BluetoothInstance.getInfo() != null ) {
          //  BluetoothInstance.getInstance().onSendBtnClicked();
            BluetoothInstance.getInfo().toString();
        }
        View leftView = new View(this);
        leftView.setBackgroundColor(Color.TRANSPARENT);
        children = new View[] { leftView, acbuwaPage };
        scrollView.initViews(children, new SizeCallBackForMenu(this.menuBtn), this.menuList);
        scrollView.setMenuBtn(this.menuBtn);
        addContactBtn = (Button) findViewById(R.id.addContactBtn);
        addContactBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Uri insertUri = android.provider.ContactsContract.Contacts.CONTENT_URI;
                Intent intent = new Intent(Intent.ACTION_INSERT, insertUri);
                startActivityForResult(intent, 1008);
            }
        });
        startReceiver1();
    }

    public static void init() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key", ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY }; // 查询的列
        asyncQuery.startQuery(0, null, uri, projection, null, null, "sort_key COLLATE LOCALIZED asc"); // 按照sort_key升序查询

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

    /**
     * 数据库异步查询类AsyncQueryHandler
     * 
     * @author administrator
     * 
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        /**
         * 查询结束的回调函数
         */
        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                contactIdMap = new HashMap<Integer, ContactBean>();
                list = new ArrayList<ContactBean>();
                cursor.moveToFirst();
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);
                    if (contactIdMap.containsKey(contactId)) {

                    }else{
                        ContactBean cb = new ContactBean();
                        cb.setDisplayName(name);
                        cb.setPhoneNum(number);
                        cb.setSortKey(sortKey);
                        cb.setContactId(contactId);
                        cb.setPhotoId(photoId);
                        cb.setLookUpKey(lookUpKey);
                        list.add(cb);
                        contactIdMap.put(contactId, cb);
                    }
                }
                if (list.size() > 0) {
                    setAdapter(list);
                }
            }
        }

    }

    private void setAdapter(List<ContactBean> list) {
        final String[] lianxiren1 = new String[] {getString(R.string.send_message), getString(R.string.Details),getString(R.string.delete)};
        adapter = new ContactHomeAdapter(this, list, alpha);
        personList.setAdapter(adapter);
        alpha.init(HomeContactActivity.this);
        alpha.setListView(personList);
        alpha.setHight(alpha.getHeight());
        alpha.setVisibility(View.VISIBLE);
        personList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean cb = (ContactBean) adapter.getItem(position);
                showContactDialog(lianxiren1, cb, position);
            }
        });
    }
    // 群组联系人弹出页
    private void showContactDialog(final String[] arg, final ContactBean cb, final int position) {
        new AlertDialog.Builder(this).setTitle(cb.getDisplayName())
                .setItems(arg, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = null;
                        switch (which) {
                        case 0:// 发短息
                            String threadId = getSMSThreadId(cb.getPhoneNum());
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("phoneNumber", cb.getPhoneNum());
                            map.put("threadId", threadId);
                            BaseIntentUtil.intentSysDefault(HomeContactActivity.this, MessageBoxList.class, map);
                            break;
                            
                        case 1:// 查看详细 修改联系人资料
                            uri = ContactsContract.Contacts.CONTENT_URI;
                            Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
                            Intent intent2 = new Intent();
                            intent2.setAction(Intent.ACTION_VIEW);
                            intent2.setData(personUri);
                            startActivity(intent2);
                            break;

                        case 2:
                            showDelete(cb.getContactId(), position);
                            break;
                        }
                    }
                }).show();
    }

    // 删除联系人方法
    private void showDelete(final int contactsID, final int position) {
        new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle(getString(R.string.delect_contact))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 源码删除
                        Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
                        Uri lookupUri = ContactsContract.Contacts
                                .getLookupUri(HomeContactActivity.this.getContentResolver(), deleteUri);
                        if (lookupUri != Uri.EMPTY) {
                            HomeContactActivity.this.getContentResolver().delete(deleteUri, null, null);
                        }
                        adapter.remove(position);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(HomeContactActivity.this,getString(R.string.cancle), Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
    }

    /**
     * 
     * 查询所有群组 返回值List<ContactGroup>
     */
    public List<GroupBean> queryGroup() {
        List<GroupBean> list = new ArrayList<GroupBean>();
        GroupBean cg_all = new GroupBean();
        cg_all.setId(0);
        cg_all.setName(getString(R.string.all));
        list.add(cg_all);
        Cursor cur = getContentResolver().query(Groups.CONTENT_URI, null, null, null, null);
        for (cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()) {
            if (null != cur.getString(cur.getColumnIndex(Groups.TITLE))
                    && (!"".equals(cur.getString(cur.getColumnIndex(Groups.TITLE))))) {
                GroupBean cg = new GroupBean();
                cg.setId(cur.getInt(cur.getColumnIndex(Groups._ID)));
                cg.setName(cur.getString(cur.getColumnIndex(Groups.TITLE)));
                list.add(cg);
            }
        }
        cur.close();
        return list;
    }

    private void queryGroupMember(GroupBean gb) {

        String[] RAW_PROJECTION = new String[] { ContactsContract.Data.RAW_CONTACT_ID };

        Cursor cur = getContentResolver().query(
                ContactsContract.Data.CONTENT_URI, RAW_PROJECTION, ContactsContract.Data.MIMETYPE + " = '"
                        + GroupMembership.CONTENT_ITEM_TYPE + "' AND " + ContactsContract.Data.DATA1 + "=" + gb.getId(),
                null, "data1 asc");

        StringBuilder inSelectionBff = new StringBuilder().append(ContactsContract.RawContacts._ID + " IN ( 0");
        while (cur.moveToNext()) {
            inSelectionBff.append(',').append(cur.getLong(0));
        }
        cur.close();
        inSelectionBff.append(')');
        Cursor contactIdCursor = getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                new String[] { ContactsContract.RawContacts.CONTACT_ID }, inSelectionBff.toString(), null,
                ContactsContract.Contacts.DISPLAY_NAME + "  COLLATE LOCALIZED asc ");
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        while (contactIdCursor.moveToNext()) {
            map.put(contactIdCursor.getInt(0), 1);
        }
        contactIdCursor.close();
        Set<Integer> set = map.keySet();
        Iterator<Integer> iter = set.iterator();
        List<ContactBean> list = new ArrayList<ContactBean>();
        while (iter.hasNext()) {
            Integer key = iter.next();
            list.add(queryMemberOfGroup(key));
        }
        setAdapter(list);
    }

    private ContactBean queryMemberOfGroup(int id) {
        ContactBean cb = null;
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人的Uri
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.DATA1,
                "sort_key", ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY }; // 查询的列
        Cursor cursor = getContentResolver().query(uri, projection,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            list = new ArrayList<ContactBean>();
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                String name = cursor.getString(1);
                String number = cursor.getString(2);
                String sortKey = cursor.getString(3);
                int contactId = cursor.getInt(4);
                Long photoId = cursor.getLong(5);
                String lookUpKey = cursor.getString(6);

                cb = new ContactBean();
                cb.setDisplayName(name);
                cb.setPhoneNum(number);
                cb.setSortKey(sortKey);
                cb.setContactId(contactId);
                cb.setPhotoId(photoId);
                cb.setLookUpKey(lookUpKey);
            }
        }
        cursor.close();
        return cb;
    }

    public static String[] SMS_COLUMNS = new String[] { "thread_id" };
    private String getSMSThreadId(String adddress) {
        Cursor cursor = null;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(Uri.parse("content://sms"), SMS_COLUMNS, " address like '%" + adddress + "%' ",
                null, null);
        String threadId = "";
        if (cursor == null || cursor.getCount() > 0) {
            cursor.moveToFirst();
            threadId = cursor.getString(0);
            cursor.close();
            return threadId;
        } else {
            cursor.close();
            return threadId;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (1008 == requestCode) {
            init();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void onDestroy() {
        super.onDestroy();
        stopReceiver1();
    }

    private String ACTION1 = "SET_DEFAULT_SIG";
    private HomeContactActivity.BaseReceiver1 receiver1 = null;

    /**
     * 打开接收器
     */
    private void startReceiver1() {
        if (null == receiver1) {
            IntentFilter localIntentFilter = new IntentFilter(ACTION1);
            receiver1 = new HomeContactActivity.BaseReceiver1();
            this.registerReceiver(receiver1, localIntentFilter);
        }
    }

    /**
     * 关闭接收器
     */
    private void stopReceiver1() {
        if (null != receiver1)
            unregisterReceiver(receiver1);
    }

    public class BaseReceiver1 extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION1)) {
                String str_bean = intent.getStringExtra("groupbean");
                Gson gson = new Gson();
                GroupBean gb = gson.fromJson(str_bean, GroupBean.class);
                if (gb.getId() == 0) {
                    init();
                }else{
                    queryGroupMember(gb);
                }
            }
        }
    }
}
