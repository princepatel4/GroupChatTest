package com.groupchattest.view;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.groupchattest.R;
import com.groupchattest.utils.AppSharedPrefs;

public class MainActivity extends AppCompatActivity {

    AppSharedPrefs appSharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appSharedPrefs = AppSharedPrefs.getInstance(MainActivity.this);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent;
                if(appSharedPrefs.getBoolean(AppSharedPrefs.PREFS_LOGIN_STATUS)) {
                    intent = new Intent(MainActivity.this, GroupListActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 100);

    }
}
