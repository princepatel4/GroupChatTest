package com.groupchattest.view;

import android.app.Application;

import com.groupchattest.utils.FirebaseDatabase;

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance(MyApplication.this);
    }
}
