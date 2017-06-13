package com.gmonetix.slambook.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.helper.Device;
import com.gmonetix.slambook.model.Friend;
import com.gmonetix.slambook.model.User;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.Funcs;
import com.gmonetix.slambook.utils.UtilDialogs;
import com.gmonetix.slambook.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FriendProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private AppPref appPref;

    private AdView adView;
    private Toolbar toolbar;
    private TextView tvName, tvEmail, tvUsername, tvPhone, tvDob, tvDesc, tvRegisteredAt;
    private ImageView imageView;
    private FloatingActionButton fabSendSlam, fabSendMessage;

    //phone views
    RelativeLayout rl;
    View v;

    private String name = "", email = "", image = "", username = "", description = "", dob = "", phone = "", visibility = "", registeredAt = "";
    private Bitmap bitmap;
    private String firebaseUid = "";

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
    private static final String FRIEND_REQUEST = "friend_request";
    private static final String FIREBASE_UID_INTENT = "firebase_uid";

    private boolean isItaRequest = false;
    private boolean isItaSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_friend_profile);
        this.appPref = App.getAppPref();
        setHeader();

        MobileAds.initialize(getApplicationContext(),getResources().getString(R.string.ad_friend_profile_activity));
        adView = (AdView) findViewById(R.id.adViewFriendProfile);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Device.getId(FriendProfileActivity.this)).build();
        adView.loadAd(adRequest);

        init();

        Intent intent = getIntent();
        if (intent.hasExtra(NAME_INTENT)) {
            name = intent.getExtras().getString(NAME_INTENT);
            email = intent.getExtras().getString(EMAIL_INTENT);
            image = intent.getExtras().getString(IMAGE_INTENT);
            username = intent.getExtras().getString(USERNAME_INTENT);
            description = intent.getExtras().getString(DESCRIPTION_INTENT);
            dob = intent.getExtras().getString(DOB_INTENT);
            phone = intent.getExtras().getString(PHONE_INTENT);
            visibility = intent.getExtras().getString(VISIBILITY_INTENT);
            registeredAt = intent.getExtras().getString(REGISTERED_AT_INTENT);
            firebaseUid = intent.getExtras().getString(FIREBASE_UID_INTENT);
        }
        if (intent.hasExtra(FRIEND_REQUEST)) {
            fabSendMessage.setVisibility(View.GONE);
            isItaRequest = intent.getExtras().getBoolean(FRIEND_REQUEST);
            fabSendSlam.setBackgroundResource(R.drawable.ic_friend_request_accept_white);
        }
        if (intent.hasExtra(FRIEND_SEARCH)) {
            fabSendMessage.setVisibility(View.GONE);
        }

        tvName.setText(name);
        tvEmail.setText(email);
        tvUsername.setText(username);
        if (visibility.equals("1")) {
            tvPhone.setText(phone);
        } else {
            findViewById(R.id.rlViewFriendProfile).setVisibility(View.GONE);
            findViewById(R.id.viewFriendProfile).setVisibility(View.GONE);
        }
        tvPhone.setText(phone);
        tvDob.setText(dob);
        tvDesc.setText(description);
        tvRegisteredAt.setText(registeredAt);

        ImageLoader.getInstance().displayImage(image, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                bitmap = loadedImage;
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
            }
        });

    }

    private void init() {
        UtilsUI.getUilInstance(FriendProfileActivity.this);

        tvName = (TextView) findViewById(R.id.friend_profile_activity_name);
        tvEmail = (TextView) findViewById(R.id.friend_profile_activity_email);
        tvUsername = (TextView) findViewById(R.id.friend_profile_username);
        tvPhone = (TextView) findViewById(R.id.friend_profile_activity_phone);
        tvDob = (TextView) findViewById(R.id.friend_profile_activity_dob);
        tvDesc = (TextView) findViewById(R.id.friend_profile_activity_desc);
        tvRegisteredAt = (TextView) findViewById(R.id.friend_profile_activity_registered_at);
        imageView = (ImageView) findViewById(R.id.friend_profileImage);
        fabSendSlam = (FloatingActionButton) findViewById(R.id.friend_profile_send_slam_btn);
        fabSendMessage = (FloatingActionButton) findViewById(R.id.friend_profile_send_message);

        fabSendSlam.setOnClickListener(this);
        fabSendMessage.setOnClickListener(this);
        imageView.setOnClickListener(this);
    }

    private void setHeader() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FriendProfileActivity.this.setTitle("Friend");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPref.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPref.getPrimaryColorPref());
        }
        UtilsUI.applyFontForToolbarTitle(FriendProfileActivity.this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.friend_profile_send_slam_btn:
                if (isItaRequest) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.url_accept_friend_request,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String s) {
                                    try {
                                        JSONArray jsonArray = new JSONArray(s);
                                        JSONObject object = jsonArray.getJSONObject(0);
                                        if (object.getString("code").equals("success")) {
                                            isItaRequest = false;
                                            fabSendSlam.setBackgroundResource(R.drawable.ic_send_white);
                                            Toast.makeText(FriendProfileActivity.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                                        } else if (object.getString("code").equals("error")) {
                                            Toast.makeText(FriendProfileActivity.this,object.getString("message"),Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(FriendProfileActivity.this,"unknown error",Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(FriendProfileActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(FriendProfileActivity.this,volleyError.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> params = new HashMap<String, String>();
                            params.put("user_name",appPref.getUsername());
                            params.put("friend_user_name",username);
                            params.put("friend_accepted_on", DateFormat.getDateInstance().format(new Date()));
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(FriendProfileActivity.this);
                    requestQueue.add(stringRequest);
                } else {
                    Intent intent = new Intent(FriendProfileActivity.this,WriteSlamActivity.class);
                    intent.putExtra(USERNAME_INTENT,username);
                    intent.putExtra(NAME_INTENT,name);
                    startActivity(intent);
                }
                break;

            case R.id.friend_profileImage:
                MaterialDialog dialog = UtilDialogs.imageViewDialog(FriendProfileActivity.this, name, R.drawable.user_outline);
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imageViewDialog);
                if (bitmap != null)
                    imageView.setImageBitmap(bitmap);
                dialog.show();
                break;

            case R.id.friend_profile_send_message:
                FirebaseDatabase.getInstance().getReference().child(Const.ARG_USERS).child(appPref.getFirebaseUid()).
                        addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                String token = user.firebaseToken;
                                ChatActivity.startActivity(FriendProfileActivity.this,name,
                                        email, firebaseUid, token);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                break;

        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

}
