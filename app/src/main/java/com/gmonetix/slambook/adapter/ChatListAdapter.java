package com.gmonetix.slambook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.activity.ChatActivity;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.model.Friend;
import com.gmonetix.slambook.model.User;
import com.gmonetix.slambook.utils.AppPref;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * @author Gmonetix
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder>{

    private AppPref appPref;
    private List<Friend> chatList;
    public Context context;

    public ChatListAdapter(List<Friend> chatList, Context context) {
        this.appPref = App.getAppPref();
        this.chatList = chatList;
        this.context = context;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View AdapterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_cardview, parent, false);
        return new ChatListAdapter.ViewHolder(AdapterView, context);
    }

    @Override
    public void onBindViewHolder(final ChatListAdapter.ViewHolder holder, final int position) {
        final Friend friend = chatList.get(position);
        holder.name.setText(friend.getName());
        ImageLoader.getInstance().displayImage(friend.getImage(), holder.imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                holder.progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_default_image_black));
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                holder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.user_default_image_black));
                holder.progressBar.setVisibility(View.GONE);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance().getReference().child(Const.ARG_USERS).child(appPref.getFirebaseUid()).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                String token = user.firebaseToken;
                                ChatActivity.startActivity(context,chatList.get(position).getName(),
                                        chatList.get(position).getEmail(), chatList.get(position).getFirebaseUid(), token);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected ImageView imageView;
        private Button sendRqst;
        private ProgressBar progressBar;
        protected CardView cardView;

        public ViewHolder(View v, Context context) {
            super(v);
            name = (TextView) v.findViewById(R.id.friend_cardview_name);
            imageView = (ImageView) v.findViewById(R.id.friend_cardview_image);
            sendRqst = (Button) v.findViewById(R.id.friend_cardview_send_rqst);
            sendRqst.setVisibility(View.GONE);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar_friend_cardview);
            cardView = (CardView) v.findViewById(R.id.friend_cardview);
        }
    }

}
