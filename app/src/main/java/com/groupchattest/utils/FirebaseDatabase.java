package com.groupchattest.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class FirebaseDatabase {

    private static  DatabaseReference mFireBaseDatabase;

    public static DatabaseReference getInstance(Context activity){
        if (mFireBaseDatabase==null){
            mFireBaseDatabase = initiateFireBase(activity, Config.firebaseURL);
        }
        return mFireBaseDatabase;
    }

    public static DatabaseReference initiateFireBase(Context mActivity, String url) {
        try {
            DatabaseReference mFirebaseDatabase;
            com.google.firebase.database.FirebaseDatabase mFirebaseInstance;

            mFirebaseInstance = com.google.firebase.database.FirebaseDatabase.getInstance();
            mFirebaseDatabase = mFirebaseInstance.getReference(url);

            FirebaseApp firebaseApp = FirebaseApp.initializeApp(mActivity);
            System.out.println("AppId - " + firebaseApp.getOptions().getApplicationId());
            return mFirebaseDatabase;
        }catch (Exception e){
            return null;
        }
    }



}
