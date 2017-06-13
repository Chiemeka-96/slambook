package com.gmonetix.slambook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.activity.ReadSlamActivity;
import com.gmonetix.slambook.model.ReadSlam;
import com.gmonetix.slambook.utils.AppPref;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * @author Gmonetix
 */

public class ReadSlamAdapter extends RecyclerView.Adapter<ReadSlamAdapter.ViewHolder>{

    private AppPref appPref;
    private List<ReadSlam> readSlamList;
    public Context context;

    private final static String INTENT_USERNAME = "username";
    private final static String INTENT_NAME = "name";
    private final static String INTENT_IMAGE = "image";
    private final static String INTENT_SENT_ON = "sent_on";
    private final static String INTENT_UPDATED_ON = "updated_on";

    public ReadSlamAdapter(List<ReadSlam> readSlamList, Context context) {
        this.appPref = App.getAppPref();
        this.readSlamList = readSlamList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View AdapterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_slam_cardview, parent, false);
        return new ReadSlamAdapter.ViewHolder(AdapterView, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ReadSlam readSlam = readSlamList.get(position);

        holder.name.setText(readSlam.getName());
        holder.username.setText(readSlam.getFromUsername());
        holder.updatedOn.setText(readSlam.getUpdatedOn());
        ImageLoader.getInstance().displayImage(readSlam.getImage(), holder.imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_outline));
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_outline));
                holder.progressBar.setVisibility(View.GONE);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReadSlamActivity.class);
                intent.putExtra(INTENT_USERNAME,readSlamList.get(position).getFromUsername());
                intent.putExtra(INTENT_NAME,readSlamList.get(position).getName());
                intent.putExtra(INTENT_IMAGE,readSlamList.get(position).getImage());
                intent.putExtra(INTENT_SENT_ON,readSlamList.get(position).getSentOn());
                intent.putExtra(INTENT_UPDATED_ON,readSlamList.get(position).getUpdatedOn());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return readSlamList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView username;
        private TextView updatedOn;
        protected ImageView imageView;
        protected CardView cardView;
        protected ProgressBar progressBar;

        public ViewHolder(View v, Context context) {
            super(v);
            name = (TextView) v.findViewById(R.id.sent_slam_name_tv);
            username = (TextView) v.findViewById(R.id.sent_slam_username_tv);
            imageView = (ImageView) v.findViewById(R.id.sent_slam_profile_image);
            cardView = (CardView) v.findViewById(R.id.sent_slam_cardview);
            updatedOn = (TextView) v.findViewById(R.id.sent_slam_updated_on_tv);
            progressBar = (ProgressBar) v.findViewById(R.id.sent_slam_profile_image_progrss_bar);
        }
    }

}
