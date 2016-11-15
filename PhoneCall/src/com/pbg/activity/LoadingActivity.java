package com.pbg.activity;

import xu.ye.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class LoadingActivity extends Activity {

    private final String TAG = "LoadingActivity";
    private ImageView iv_adimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        setupView();
    }

    private void setupView() {
        // TODO Auto-generated method stub
        iv_adimg = (ImageView) findViewById(R.id.iv_adimg);
        AlphaAnimation alphaAnimation = new AlphaAnimation((float) 0.1, 1);
        alphaAnimation.setDuration(0);// 设定动画时间
        alphaAnimation.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent();
                intent.setClass(LoadingActivity.this,
                        HomeTabHostAcitivity.class);
                startActivity(intent);
                LoadingActivity.this.finish();
            }
        });

        iv_adimg.setAnimation(alphaAnimation);
        iv_adimg.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return false;
    }

}
