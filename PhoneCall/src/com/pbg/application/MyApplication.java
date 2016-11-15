package com.pbg.application;

import java.util.List;

import com.pbg.bean.ContactBean;
import com.pbg.bluetooth.BluetoothInstance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
//import android.pim.vcard.AppeSession;
import android.view.LayoutInflater;
import android.view.View;
import xu.ye.R;

public class MyApplication extends android.app.Application {

    private List<ContactBean> contactBeanList;

    public List<ContactBean> getContactBeanList() {
        return contactBeanList;
    }

    public void setContactBeanList(List<ContactBean> contactBeanList) {
        this.contactBeanList = contactBeanList;
    }

    public void onCreate() {
        
    }

    public void promptExit(final Context context) {
        LayoutInflater li = LayoutInflater.from(context);
        View exitV = li.inflate(R.layout.dialog_exit, null);
        AlertDialog.Builder ab = new AlertDialog.Builder(context);
        ab.setView(exitV);
        ab.setPositiveButton(R.string.exit, new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                exitApp(context);
            }
        });
        ab.setNegativeButton(R.string.cancle, null);
        ab.show();
    }

    public void exitApp(Context context) {
        ((Activity) context).finish();
    }
}
