package com.groupchattest.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.groupchattest.R;
import com.groupchattest.controller.ImageAdapter;
import com.groupchattest.model.ImageList;
import com.groupchattest.model.UserDetails;
import com.groupchattest.utils.APIHandler;
import com.groupchattest.utils.AppSharedPrefs;
import com.groupchattest.utils.Config;
import com.groupchattest.utils.FirebaseDatabase;
import com.groupchattest.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GroupListActivity extends AppCompatActivity {


    RecyclerView recyclerViewGroupList;

    DatabaseReference databaseReference;

    StorageReference storageReference;
    FirebaseStorage firebaseStorage;

    AppSharedPrefs appSharedPrefs;

    private final int SELECT_PHOTO = 1;

    List<ImageList> arrayListImage = new ArrayList<>();

    String lastImage = "";

    ImageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_list);

        databaseReference = FirebaseDatabase.getInstance(GroupListActivity.this);
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        appSharedPrefs = AppSharedPrefs.getInstance(GroupListActivity.this);

        setUI();
    }

    private void setUI(){

        recyclerViewGroupList = (RecyclerView) findViewById(R.id.recycler_group_list);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(GroupListActivity.this);
        recyclerViewGroupList.setLayoutManager(layoutManager);
        if(adapter == null){
            adapter = new ImageAdapter(GroupListActivity.this, arrayListImage);
            recyclerViewGroupList.setAdapter(adapter);
        }else{
            adapter.updateList(arrayListImage);
        }
        getUpdatedImage();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_list, menu);//Menu Resource, Menu
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                Toast.makeText(getApplicationContext(),"Item 1 Selected",Toast.LENGTH_LONG).show();

                openImageGallery();
                break;

        }
        return true;
    }

    private void openImageGallery(){
        Intent in = new Intent(Intent.ACTION_PICK);
        in.setType("image/*");
        startActivityForResult(in, SELECT_PHOTO);
    }
    protected void onActivityResult(int requestcode, int resultcode,
                                    Intent imagereturnintent) {
        super.onActivityResult(requestcode, resultcode, imagereturnintent);
        switch (requestcode) {
            case SELECT_PHOTO:
                if (resultcode == RESULT_OK) {
                    try {

                        Uri imageuri = imagereturnintent.getData();// Get intent

                        Bitmap bitmap = decodeUri(GroupListActivity.this, imageuri, 300);// call

                        if (bitmap != null)
                            openDialog(bitmap,imageuri);

                        else
                            Toast.makeText(GroupListActivity.this,
                                    "Error while decoding image.",
                                    Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {

                        e.printStackTrace();
                        Toast.makeText(GroupListActivity.this, "File not found.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    // Method that deocde uri into bitmap. This method is necessary to deocde
    // large size images to load over imageview
    public static Bitmap decodeUri(Context context, Uri uri,
                                   final int requiredSize) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o2);
    }

    private void openDialog(Bitmap bitmap, final Uri filePath){
        final Dialog dialog = new Dialog(GroupListActivity.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.custom_dialog);
        // Set dialog title
        dialog.setTitle("Custom Dialog");

        // set values for custom dialog components - text, image and button

        ImageView image = (ImageView) dialog.findViewById(R.id.image_pick);
        image.setImageBitmap(bitmap);

        dialog.show();

        Button sendButton = (Button) dialog.findViewById(R.id.button_send);
        // if decline button is clicked, close the custom dialog
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
                uploadImage(filePath);
            }
        });
    }

    private void uploadImage(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(Config.imageNode+"_"+appSharedPrefs.getString(AppSharedPrefs.PREFS_USERNAME)+"_"+ Utils.getCurrentTimeStamp());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Toast.makeText(GroupListActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            String mGroupId = databaseReference.push().getKey();
                            ImageList imageList = new ImageList();
                            imageList.setImageURL(taskSnapshot.getDownloadUrl().toString());
                            imageList.setUsername(appSharedPrefs.getString(AppSharedPrefs.PREFS_USERNAME));
                            imageList.setTime(String.valueOf(Utils.getCurrentTimeStamp()));


                            databaseReference.child(Config.imageNode).child(mGroupId).setValue(imageList);
                            sendNotificationToAllUsers();
                            arrayListImage.add(imageList);
                            adapter.notifyDataSetChanged();
                            lastImage = mGroupId;
                            recyclerViewGroupList.smoothScrollToPosition(recyclerViewGroupList.getAdapter().getItemCount());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(GroupListActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }


    private void getUpdatedImage()
    {

        databaseReference.child(Config.imageNode).orderByKey().startAt(lastImage).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                System.out.println("snapshot "+dataSnapshot.getChildren());
                System.out.println("snapshot "+s);

                if(dataSnapshot.getKey().equalsIgnoreCase(lastImage))
                {return;}else {
                    //dataSnapshot.child("messageKey").getValue().toString()

                    ImageList imageList = new ImageList();
                    imageList.setUsername(dataSnapshot.child("username").getValue().toString());
                    imageList.setTime(dataSnapshot.child("time").getValue().toString());
                    imageList.setImageURL(dataSnapshot.child("imageURL").getValue().toString());

                    if(arrayListImage.contains(imageList)){

                    }else {
                        arrayListImage.add(imageList);
                    }
                    arrayListImage.size();
                    if(adapter == null){
                        adapter = new ImageAdapter(GroupListActivity.this, arrayListImage);
                        recyclerViewGroupList.setAdapter(adapter);
                    }else{
                        adapter.updateList(arrayListImage);
                    }

                    lastImage = dataSnapshot.getKey();
                    recyclerViewGroupList.smoothScrollToPosition(recyclerViewGroupList.getAdapter().getItemCount());
                }
                /*for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    //ChatConversation chatConversation = new ChatConversation();
                    System.out.println("snapshot "+snapshot.child("chatMessage").getValue());
                    Toast.makeText(ChatConversationActivity.this, ""+snapshot.child("chatMessage").getValue(), Toast.LENGTH_SHORT).show();
                    //arra.add(snapshot.getValue().toString());
                }*/

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                ImageList imageList = new ImageList();
                imageList.setUsername(dataSnapshot.child("username").getValue().toString());
                imageList.setTime(dataSnapshot.child("time").getValue().toString());
                imageList.setImageURL(dataSnapshot.child("imageURL").getValue().toString());

                arrayListImage.remove(imageList);
                adapter.updateList(arrayListImage);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendNotificationToAllUsers(){

        databaseReference.child(Config.userNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, UserDetails> map = (Map<String,UserDetails>) dataSnapshot.getValue();
                for(String key : map.keySet()) {
                    sendMessage(key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String token){

        try {

            JSONObject json=new JSONObject();
            JSONObject dataJson=new JSONObject();

            dataJson.put("body", appSharedPrefs.getString(AppSharedPrefs.PREFS_USERNAME) + "Send new image");
            dataJson.put("title", "New image");
            json.put("notification",dataJson);
            json.put("to",token);
            System.out.println("request Json "+ json);
            APIHandler.getsharedInstance(GroupListActivity.this).execute(Request.Method.POST, APIHandler.restAPI.sendFCMMessage, json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {



                }
            }, "key="+Config.LEGACY_SERVER_KEY);
        }catch (JSONException e){

        }
    }

}
