package com.groupchattest.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.groupchattest.R;
import com.groupchattest.model.UserDetails;
import com.groupchattest.utils.AppSharedPrefs;
import com.groupchattest.utils.Config;
import com.groupchattest.utils.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    EditText editTextUsername, editTextPassword;
    TextView textViewSignupNow;
    Button buttonLogin;

    AppSharedPrefs appSharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        appSharedPrefs = AppSharedPrefs.getInstance(LoginActivity.this);

        setUI();
    }

    private void setUI(){

        editTextUsername = (EditText) findViewById(R.id.edittext_username);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);
        buttonLogin = ( Button) findViewById(R.id.button_login);
        textViewSignupNow = (TextView) findViewById(R.id.text_signup_navigate);

        textViewSignupNow.setOnClickListener(clickListener);
        buttonLogin.setOnClickListener(clickListener);
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()){

                case R.id.text_signup_navigate:

                    intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);

                    break;

                case R.id.button_login:

                    login(editTextUsername.getText().toString());
                    /*if(FirebaseDatabase.checkLoginCredential(LoginActivity.this,editTextUsername.getText().toString(), editTextPassword.getText().toString())){
                        Toast.makeText(LoginActivity.this, "True", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Username and Password not correct", Toast.LENGTH_SHORT).show();
                    }*/

                    break;
            }
        }
    };

    private void login(String username){
        DatabaseReference mFireBaseDatabase = FirebaseDatabase.getInstance(LoginActivity.this);

        mFireBaseDatabase.child(Config.userNode).child(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    String password = dataSnapshot.child("password").getValue().toString();

                    if (username.equals(editTextUsername.getText().toString()) && password.equals(editTextPassword.getText().toString())) {

                        appSharedPrefs.setString(AppSharedPrefs.PREFS_USERNAME, editTextUsername.getText().toString());
                        appSharedPrefs.setBoolean(AppSharedPrefs.PREFS_LOGIN_STATUS, true);
                        Intent intent = new Intent(LoginActivity.this, GroupListActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Username or Password dont't match", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "User not exist", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
