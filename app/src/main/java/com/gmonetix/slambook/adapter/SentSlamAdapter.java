package com.gmonetix.slambook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.activity.FriendProfileActivity;
import com.gmonetix.slambook.activity.Home;
import com.gmonetix.slambook.activity.SentSlamsActivity;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.model.Friend;
import com.gmonetix.slambook.model.SentSlam;
import com.gmonetix.slambook.utils.AppPref;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Gmonetix
 */

public class SentSlamAdapter extends RecyclerView.Adapter<SentSlamAdapter.ViewHolder>{

    private AppPref appPref;
    private List<SentSlam> sentSlamList;
    public Context context;

    private int pos;

    private static final String NAME_INTENT = "name";
    private static final String EMAIL_INTENT = "email";
    private static final String IMAGE_INTENT = "image";
    private static final String USERNAME_INTENT = "username";
    private static final String DESCRIPTION_INTENT = "description";
    private static final String DOB_INTENT = "dob";
    private static final String PHONE_INTENT = "phone";
    private static final String VISIBILITY_INTENT = "phone_visibility";
    private static final String REGISTERED_AT_INTENT = "registered_at";
    private static final String FIREBASE_UID_INTENT = "firebase_uid";

    private final static String INTENT_USERNAME = "username";
    private final static String INTENT_NAME = "name";
    private final static String INTENT_IMAGE = "image";
    private final static String INTENT_SENT_ON = "sent_on";
    private final static String INTENT_UPDATED_ON = "updated_on";

    @NonNull
    private OnSlamDeletedListener onSlamDeletedListener;

    public interface OnSlamDeletedListener {
        void onSlamDeleted(int pos);
    }

    public SentSlamAdapter(List<SentSlam> sentSlamList, Context context, OnSlamDeletedListener onSlamDeletedListener) {
        this.appPref = App.getAppPref();
        this.sentSlamList = sentSlamList;
        this.context = context;
        this.onSlamDeletedListener = onSlamDeletedListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View AdapterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_slam_cardview, parent, false);
        return new ViewHolder(AdapterView, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SentSlam sentSlam = sentSlamList.get(position);

        holder.name.setText(sentSlam.getName());
        holder.username.setText(sentSlam.getUsername());
        holder.updatedOn.setText(sentSlam.getUpdatedOn());
        ImageLoader.getInstance().displayImage(sentSlam.getImage(), holder.imageView, new ImageLoadingListener() {
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
        holder.overflowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.overflowMenu);
                pos = position;
            }
        });
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(holder.overflowMenu);
                pos = position;
                return true;
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SentSlamsActivity.class);
                intent.putExtra(INTENT_USERNAME,sentSlamList.get(position).getUsername());
                intent.putExtra(INTENT_NAME,sentSlamList.get(position).getName());
                intent.putExtra(INTENT_IMAGE,sentSlamList.get(position).getImage());
                intent.putExtra(INTENT_SENT_ON,sentSlamList.get(position).getSentOn());
                intent.putExtra(INTENT_UPDATED_ON,sentSlamList.get(position).getUpdatedOn());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sentSlamList.size();
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.slam_cardview_popup_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.slam_cardview_open_profile:
                    openProfile();
                    return true;
                case R.id.slam_cardview_delete_slam:
                    deleteSlam();
                    return true;
                default:
            }
            return false;
        }
    }

    private void openProfile() {
        List<Friend> friendList  = Home.friendList;
        for (int i = 0; i<friendList.size(); i++) {
            if (sentSlamList.get(pos).getUsername().equals(friendList.get(i).getFriendUsername())) {
                Intent intent = new Intent(context, FriendProfileActivity.class);
                intent.putExtra(NAME_INTENT,friendList.get(i).getName());
                intent.putExtra(EMAIL_INTENT,friendList.get(i).getEmail());
                intent.putExtra(IMAGE_INTENT,friendList.get(i).getImage());
                intent.putExtra(USERNAME_INTENT,friendList.get(i).getFriendUsername());
                intent.putExtra(DESCRIPTION_INTENT,friendList.get(i).getDescription());
                intent.putExtra(DOB_INTENT,friendList.get(i).getDob());
                intent.putExtra(PHONE_INTENT,friendList.get(i).getNumber());
                intent.putExtra(VISIBILITY_INTENT,friendList.get(i).getPhoneVisibility());
                intent.putExtra(REGISTERED_AT_INTENT,friendList.get(i).getRegisteredAt());
                intent.putExtra(FIREBASE_UID_INTENT,friendList.get(i).getFirebaseUid());
                context.startActivity(intent);
                break;
            }
        }
    }

    private void deleteSlam() {
        StringRequest request = new StringRequest(Request.Method.POST, Const.delete_slam_sent,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject object = jsonArray.getJSONObject(0);
                            if (object.getString("code").equals("success")) {
                                onSlamDeletedListener.onSlamDeleted(pos);
                                Toast.makeText(context,object.getString("message"),Toast.LENGTH_SHORT).show();
                            } else if (object.getString("code").equals("failed")) {
                                Toast.makeText(context,object.getString("message"),Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context,"Unexpected error",Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("user_name",appPref.getUsername());
                params.put("friend_user_name",sentSlamList.get(pos).getUsername());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView name;
        protected TextView username;
        private TextView updatedOn;
        protected ImageView imageView;
        protected CardView cardView;
        protected ProgressBar progressBar;
        private ImageView overflowMenu;

        public ViewHolder(View v, Context context) {
            super(v);
            name = (TextView) v.findViewById(R.id.sent_slam_name_tv);
            username = (TextView) v.findViewById(R.id.sent_slam_username_tv);
            imageView = (ImageView) v.findViewById(R.id.sent_slam_profile_image);
            cardView = (CardView) v.findViewById(R.id.sent_slam_cardview);
            updatedOn = (TextView) v.findViewById(R.id.sent_slam_updated_on_tv);
            progressBar = (ProgressBar) v.findViewById(R.id.sent_slam_profile_image_progrss_bar);
            overflowMenu = (ImageView) v.findViewById(R.id.overflow_slam_cardview);
        }
    }

}
