package com.gmonetix.slambook.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.gmonetix.slambook.adapter.FriendAdapter;
import com.gmonetix.slambook.adapter.FriendRequestAdapter;
import com.gmonetix.slambook.fragments.ChatsFragment;
import com.gmonetix.slambook.fragments.DatePickerFragment;
import com.gmonetix.slambook.fragments.FriendsFragment;
import com.gmonetix.slambook.fragments.ProfileFragment;
import com.gmonetix.slambook.fragments.ReadSlamsFragment;
import com.gmonetix.slambook.fragments.SearchFragment;
import com.gmonetix.slambook.fragments.SentSlamsFragment;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.helper.CustomTypefaceSpan;
import com.gmonetix.slambook.helper.Device;
import com.gmonetix.slambook.model.Friend;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.Funcs;
import com.gmonetix.slambook.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DatePickerFragment.OnDateChosenListener {

    public static List<Friend> friendList;
    public static List<Friend> requestList;

    private AppPref appPref;

    private AdView adView;
    private Toolbar toolbar;
    private ImageView headerImage;
    private TextView headerName;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_home);
        appPref = App.getAppPref();
        setHeader();
        UtilsUI.getUilInstance(Home.this);
        progressDialog = new ProgressDialog(Home.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Getting friend list...Please Wait...");
        progressDialog.show();

        MobileAds.initialize(getApplicationContext(),getResources().getString(R.string.ad_home_activity));
        adView = (AdView) findViewById(R.id.adViewHomeActivity);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Device.getId(Home.this)).build();
        adView.loadAd(adRequest);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView =  navigationView.getHeaderView(0);
        headerImage = (ImageView) hView.findViewById(R.id.header_profile_image);
        headerName = (TextView) hView.findViewById(R.id.header_name);
        headerName.setText(appPref.getName());
        ImageLoader.getInstance().displayImage(appPref.getProfileImageUrl(), headerImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                headerImage.setImageResource(R.drawable.user_default_image_black);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                headerImage.setImageResource(R.drawable.user_default_image_black);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                headerImage.setImageResource(R.drawable.user_default_image_black);
            }
        });

        Menu m = navigationView.getMenu();
        for (int i=0;i<m.size();i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu!=null && subMenu.size() >0 ) {
                for (int j=0; j <subMenu.size();j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }

        friendList = new ArrayList<>();
        requestList = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.url_get_friend_list,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                if (object.getString("code").equals("success")) {

                                    Friend friend = new Friend();
                                    friend.setName(object.getString("name"));
                                    friend.setEmail(object.getString("email"));
                                    friend.setImage(object.getString("image"));
                                    friend.setDescription(object.getString("description"));
                                    friend.setNumber(object.getString("phone_number"));
                                    friend.setDob(object.getString("dob"));
                                    friend.setRegisteredAt(object.getString("registered_at"));
                                    friend.setPhoneVisibility(object.getString("phone_visibility"));
                                    friend.setFriendUsername(object.getString("friend_username"));
                                    friend.setFriendAccepted(object.getString("friend_accepted"));
                                    friend.setFriendAcceptedOn(object.getString("friend_accepted_on"));
                                    friend.setFriendBlocked(object.getString("message_blocked"));
                                    friend.setFirebaseUid(object.getString("firebaseUid"));
                                    if (object.getString("friend_accepted").equals("0")) {
                                        requestList.add(friend);
                                    } else friendList.add(friend);
                                    progressDialog.dismiss();
                                    Toast.makeText(Home.this, "List updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Home.this, "Error occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(Home.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressDialog.dismiss();
                Toast.makeText(Home.this, volleyError.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_name", appPref.getUsername());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(Home.this);
        requestQueue.add(stringRequest);

    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/RobotoLight.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("" , font), 0 , mNewTitle.length(),  Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void setHeader() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPref.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPref.getPrimaryColorPref());
        }
        UtilsUI.applyFontForToolbarTitle(Home.this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(Home.this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_home,new ProfileFragment()).commit();
                Home.this.setTitle("My Profile");
                break;
            case R.id.nav_friends:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_home,new FriendsFragment()).commit();
                Home.this.setTitle("Friends");
                break;
            case R.id.nav_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_home,new ChatsFragment()).commit();
                Home.this.setTitle("Chats");
                break;
            case R.id.nav_search:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_home,new SearchFragment()).commit();
                Home.this.setTitle("Search");
                break;
            case R.id.nav_read:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_home,new ReadSlamsFragment()).commit();
                Home.this.setTitle("Received Slams");
                break;
            case R.id.nav_sent:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_container_home,new SentSlamsFragment()).commit();
                Home.this.setTitle("Sent Slams");
                break;
            case R.id.nav_logout:
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().clear().apply();
                performFirebaseLogout();
                Intent i = new Intent(Home.this,LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            case R.id.nav_rate:
                Funcs.goToGooglePlay(Home.this,Home.this.getPackageName());
                break;
            case R.id.nav_help:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void performFirebaseLogout() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(Home.this, "successfully logged out from chat", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Home.this, "failed logging out from chat", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDateChose(String date) {
        ProfileFragment frag = (ProfileFragment)
                getSupportFragmentManager().findFragmentById(R.id.frame_container_home);
        frag.updateDate(date);
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
