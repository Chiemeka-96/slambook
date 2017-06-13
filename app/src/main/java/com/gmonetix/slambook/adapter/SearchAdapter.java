package com.gmonetix.slambook.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.model.Friend;
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

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{

    private AppPref appPref;
    private List<Friend> friendList;
    public Context context;

    private static final String NAME_INTENT = "name";
    private static final String EMAIL_INTENT = "email";
    private static final String IMAGE_INTENT = "image";
    private static final String USERNAME_INTENT = "username";
    private static final String DESCRIPTION_INTENT = "description";
    private static final String DOB_INTENT = "dob";
    private static final String PHONE_INTENT = "phone";
    private static final String VISIBILITY_INTENT = "phone_visibility";
    private static final String REGISTERED_AT_INTENT = "registered_at";

    private static final String FRIEND_SEARCH = "friend_search";

    public SearchAdapter(List<Friend> friendList, Context context) {
        this.appPref = App.getAppPref();
        this.friendList = friendList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View AdapterView = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_cardview, parent, false);
        return new ViewHolder(AdapterView, context);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Friend friend = friendList.get(position);
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
        holder.sendRqst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.url_send_friend_request,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                try {
                                    JSONArray jsonArray = new JSONArray(s);
                                    JSONObject object = jsonArray.getJSONObject(0);
                                    if (object.getString("code").equals("failed")) {
                                        Toast.makeText(context,object.getString("message"),Toast.LENGTH_SHORT).show();
                                    } else if (object.getString("code").equals("success")) {
                                        holder.sendRqst.setBackgroundResource(R.drawable.ic_friend_accept_request_black);
                                        Toast.makeText(context,object.getString("message"),Toast.LENGTH_SHORT).show();
                                    } else if (object.getString("code").equals("error")) {
                                        Toast.makeText(context,object.getString("message"),Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context,"Error occurred",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context,volleyError.toString(),Toast.LENGTH_SHORT).show();
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String, String>();
                        params.put("user_name",appPref.getUsername());
                        params.put("friend_user_name",friend.getUsername());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(context);
                requestQueue.add(stringRequest);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FriendProfileActivity.class);
                intent.putExtra(NAME_INTENT,friend.getName());
                intent.putExtra(EMAIL_INTENT,friend.getEmail());
                intent.putExtra(IMAGE_INTENT,friend.getImage());
                intent.putExtra(USERNAME_INTENT,friend.getUsername());
                intent.putExtra(DESCRIPTION_INTENT,friend.getDescription());
                intent.putExtra(DOB_INTENT,friend.getDob());
                intent.putExtra(PHONE_INTENT,friend.getNumber());
                intent.putExtra(VISIBILITY_INTENT,friend.getPhoneVisibility());
                intent.putExtra(REGISTERED_AT_INTENT,friend.getRegisteredAt());
                intent.putExtra(FRIEND_SEARCH,true);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
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
            sendRqst.setBackgroundResource(R.drawable.ic_friend_request_black);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar_friend_cardview);
            cardView = (CardView) v.findViewById(R.id.friend_cardview);
        }
    }

}
