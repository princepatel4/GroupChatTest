package com.groupchattest.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.groupchattest.R;
import com.groupchattest.model.UserDetails;
import com.groupchattest.utils.Config;
import com.groupchattest.utils.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    Button buttonSignup;
    EditText editTextUsername, editTextPassword;
    DatabaseReference mFireBaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFireBaseDatabase = FirebaseDatabase.getInstance(this);

        setUI();

    }

    private void setUI(){

        editTextUsername = (EditText) findViewById(R.id.edittext_username);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);
        buttonSignup = (Button) findViewById(R.id.button_signup);
        buttonSignup.setOnClickListener(clickListener);

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.button_signup:

                    sendUserDetails();

                    break;
            }
        }
    };

    private void sendUserDetails(){

        mFireBaseDatabase.child(Config.userNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(editTextUsername.getText().toString())) {
                    Toast.makeText(SignUpActivity.this, "User already registered", Toast.LENGTH_SHORT).show();
                }else{
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();

                    UserDetails userDetails = new UserDetails();
                    userDetails.setUsername(editTextUsername.getText().toString());
                    userDetails.setPassword(editTextPassword.getText().toString());
                    userDetails.setToken(refreshedToken);
                    mFireBaseDatabase.child(Config.userNode + "/" + editTextUsername.getText().toString()).setValue(userDetails);

                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

        /*mFireBaseDatabase.child(Config.userNode).child(editTextUsername.getText().toString()).addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("tag","checking");

                if(dataSnapshot.exists()){


                    Toast.makeText(SignUpActivity.this, "Username already registered", Toast.LENGTH_SHORT).show();

                }else{

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/







/*
        if(FirebaseDatabase.checkFirebaseForUsername(SignUpActivity.this, editTextUsername.getText().toString()) == 0 ) {

            String refreshedToken = FirebaseInstanceId.getInstance().getToken();

            UserDetails userDetails = new UserDetails();
            userDetails.setUsername(editTextUsername.getText().toString());
            userDetails.setPassword(editTextPassword.getText().toString());
            userDetails.setToken(refreshedToken);
            mFireBaseDatabase.child(Config.userNode + "/" + editTextUsername.getText().toString()).setValue(userDetails);

        }else{Toast.makeText(SignUpActivity.this, "Username already exist.", Toast.LENGTH_SHORT).show();}*/
    }





}
