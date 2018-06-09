package com.groupchattest.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSharedPrefs {

    private static final String PREFS_NAME = "groupchat";

    static SharedPreferences sp;
    static SharedPreferences.Editor prefEditor = null;

    private static Context mContext = null;
    public static AppSharedPrefs instance = null;

    public final static String PREFS_USERNAME = "username";
    public final static String PREFS_LOGIN_STATUS = "isLogin";


    public static AppSharedPrefs getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new AppSharedPrefs();
        }
        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefEditor = sp.edit();
        return instance;
    }

    public void setString(String key, String name) {
        prefEditor.putString(key, name);
        prefEditor.commit();
    }


    public String getString(String key)
    {
        // if name key available then it will returned value of name otherwise returned empty string.
        return sp.getString(key, "");
    }


    public void setBoolean(String key, boolean name) {
        prefEditor.putBoolean(key, name);
        prefEditor.commit();
    }


    public boolean getBoolean(String key)
    {
        // if name key available then it will returned value of name otherwise returned empty string.
        return sp.getBoolean(key, false);
    }

    public void clearData() {
        prefEditor.clear();
        prefEditor.commit();
    }
}
