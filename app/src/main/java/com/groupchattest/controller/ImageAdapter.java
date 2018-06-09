package com.groupchattest.controller;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.groupchattest.R;
import com.groupchattest.model.ImageList;
import com.groupchattest.utils.AppSharedPrefs;
import com.groupchattest.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder> {

    List<ImageList> arrayListImageList = new ArrayList<>();
    Activity activity;
    View view;
    AppSharedPrefs appSharedPrefs;
    public ImageAdapter(Activity activity, List<ImageList> arrayListImageList) {
        this.arrayListImageList = arrayListImageList;
        this.activity = activity;
        appSharedPrefs = AppSharedPrefs.getInstance(activity);
    }

    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_group_item, parent, false);

        return new ImageAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageAdapter.MyViewHolder holder, final int position) {
        //holder.textViewStoreName.setText(arrayListSurvey.get(position).getName());



        holder.textViewName.setText(arrayListImageList.get(position).getUsername());
        holder.textViewTime.setText(Utils.getDate(Long.parseLong(arrayListImageList.get(position).getTime())));
        if(arrayListImageList.get(position).getImageURL() != null){
            Picasso.with(activity)
                    .load(arrayListImageList.get(position).getImageURL())
                    .into(holder.imageView);
        }
        if(arrayListImageList.get(position).getUsername().equalsIgnoreCase(appSharedPrefs.getString(AppSharedPrefs.PREFS_USERNAME))){
            holder.linearLayoutMainImage.setGravity(Gravity.RIGHT);
            //holder.linearLayoutBubbleLayout.setBackgroundResource(R.drawable.bubble2);
        }else{
            holder.linearLayoutMainImage.setGravity(Gravity.LEFT);
            //holder.linearLayoutBubbleLayout.setBackgroundResource(R.drawable.bubble1);
        }


/*
        holder.imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MovieDetailsActivity.class);
                intent.putExtra("movieID", arrayListImageList.get(position).getId());
                activity.startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return arrayListImageList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTime, textViewName;
        LinearLayout linearLayoutMainImage;
        ImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image_server);
            textViewTime = (TextView) itemView.findViewById(R.id.text_user_name);
            textViewName = (TextView) itemView.findViewById(R.id.text_time);
            linearLayoutMainImage = (LinearLayout) itemView.findViewById(R.id.layout_image);
        }
    }

    public void updateList(List<ImageList> movieList) {
        arrayListImageList = movieList;
        notifyDataSetChanged();
    }
}
