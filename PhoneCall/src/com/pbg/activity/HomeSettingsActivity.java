package com.pbg.activity;

import com.pbg.uitl.SharedPreferencesTool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import xu.ye.R;

public class HomeSettingsActivity extends Activity {
    private Button btSave;
    private EditText etPhoneNume;
    private EditText etsecurity;
    boolean isEdit = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        intView();
    }
    private void intView() {
        etPhoneNume = (EditText)findViewById(R.id.etPhoneNume);
        etsecurity = (EditText)findViewById(R.id.etsecurity);
        String phoneNum = SharedPreferencesTool.getString(HomeSettingsActivity.this,SharedPreferencesTool.KEY_PHONE_NUM);
        String securityCode = SharedPreferencesTool.getString(HomeSettingsActivity.this,SharedPreferencesTool.KEY_SECURITY_CODE);
        etPhoneNume.setText(phoneNum);
        etsecurity.setText(securityCode);
        btSave = (Button)findViewById(R.id.save);
        btSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etPhoneNume == null||etPhoneNume.equals("") ||etsecurity == null||etsecurity.equals("")){
                    Toast.makeText(HomeSettingsActivity.this,getString(R.string.please_check_phoneNum_security_code),Toast.LENGTH_SHORT).show();;
                    return;
                }
                if(isEdit){
                    btSave.setText(getString(R.string.save));
                    etPhoneNume.setEnabled(true);
                    etsecurity.setEnabled(true);
                    isEdit = false;
                }else{
                    SharedPreferencesTool.setString(HomeSettingsActivity.this,SharedPreferencesTool.KEY_PHONE_NUM,etPhoneNume.getText().toString());
                    SharedPreferencesTool.setString(HomeSettingsActivity.this,SharedPreferencesTool.KEY_SECURITY_CODE,etsecurity.getText().toString());
                    Toast.makeText(HomeSettingsActivity.this,getString(R.string.save_succeed),Toast.LENGTH_SHORT).show();
                    btSave.setText(getString(R.string.edit));
                    etPhoneNume.setEnabled(false);
                    etsecurity.setEnabled(false);
                    isEdit = true;
                }
                
            }
        });
    }
    private void refreshView() {
       // etPhoneNume.setText(shar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
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
}
