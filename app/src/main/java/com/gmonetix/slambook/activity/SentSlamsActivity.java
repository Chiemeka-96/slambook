package com.gmonetix.slambook.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.UtilDialogs;
import com.gmonetix.slambook.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SentSlamsActivity extends AppCompatActivity implements View.OnClickListener {

    private AppPref appPref;

    private AdView adView;
    private Toolbar toolbar;
    private ImageView imageView;
    private ProgressBar progressBarImage;
    private FloatingActionButton editSlam;
    private TextView tvNickName,tvDob,tvHobbies,tvOnFamousNameChange,tvAim,tvLoveWearing,tvZodiacSign,
            tvHangoutPlace,tvTreatForBirthday,tvWeekendActivity,tvMemorableMoment,tvEmbarassingMoment,tvThingsToDoBeforeDie,tvWhatBoresMe,
            tvMcrazyAbout,tvMyBiggestStrength,tvThingsIHate,tvWhenMHappy,tvWhenMSad,tvWhenMMad,tvWorstHabit,tvBestThingAbtMe,tvFeelPowerfullWhen,
            tvBiggestAchievement,tvMyTeddyKnows,tvFb,tvAddress,tvPhoneNumber,tvWebsite,tvTwitter,tvInstagram,tvHpyMomentWidU,tvSadMomentWidU,
            tvGoodThingsAbtU,tvBadThingsAbtU,tvFriendshipToMe,tvFavColor,tvFavCelebrities,tvFavRoleModel,tvFavTvShow,tvFavMusicBand,tvFavFood,tvFavSport;

    //views for hide & show
    private LinearLayout llAbtMe, llContact, llMoments, llFavs, llMoreAbtMe, llAbtUrFrnd;
    private Button btnAbtMe, btnContact, btnMoments, btnFavs, btnMoreAbtMe, btnAbtUrFrnd;

    private String nick_name, dob, hobbies, on_famous_name_change_to, aim,love_wearing, zodiac_sign, hangout_place,
            treat_for_birthday, weekend_activity, memorable_moment, embarrassing_moment, things_want_to_do_before_die, what_bores_me_most,
            m_crazy_about, my_biggest_strength, things_i_hate, when_m_happy, when_m_sad, when_m_mad, my_worst_habit, best_thing_abt_me,
            feel_powerful_when, biggest_achievement, my_teddy_knows, fb, address, phone_number, website, twitter, instagram, hpy_moment_wid_u,
            sad_moment_wid_u, good_things_about_u, bad_things_about_u, friendship_to_me_is, fav_color, fav_celebrities, fav_role_model, fav_tv_show,
            fav_music_band, fav_food, fav_sport;
    private String fromUserName = "",name="",image="",sentOn="",updateON="";

    private final static String INTENT_USERNAME = "username";
    private final static String INTENT_NAME = "name";
    private final static String INTENT_IMAGE = "image";
    private final static String INTENT_SENT_ON = "sent_on";
    private final static String INTENT_UPDATED_ON = "updated_on";

    private String jsonData = "";
    private static final String JSON_DATA = "json_data";
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_sent_slams);
        appPref = App.getAppPref();
        setHeader();

        MobileAds.initialize(getApplicationContext(),getResources().getString(R.string.ad_sent_slam_activity));
        adView = (AdView) findViewById(R.id.adViewSentSlams);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Device.getId(SentSlamsActivity.this)).build();
        adView.loadAd(adRequest);

        init();

        if (getIntent().hasExtra(INTENT_USERNAME)) {
            fromUserName = getIntent().getExtras().getString(INTENT_USERNAME);
            name = getIntent().getExtras().getString(INTENT_NAME);
            image = getIntent().getExtras().getString(INTENT_IMAGE);
            sentOn = getIntent().getExtras().getString(INTENT_SENT_ON);
            updateON = getIntent().getExtras().getString(INTENT_UPDATED_ON);
        }
        SentSlamsActivity.this.setTitle(name);

        ImageLoader.getInstance().displayImage(image, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                progressBarImage.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
                progressBarImage.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                progressBarImage.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
                progressBarImage.setVisibility(View.GONE);
            }
        });

        StringRequest rqst = new StringRequest(Request.Method.POST, Const.slams_sent_detials,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject object = jsonArray.getJSONObject(0);
                            if (object.getString("code").equals("success_present")) {
                                success = true;
                                jsonData = s;
                                nick_name = object.getString("nick_name");
                                dob = object.getString("dob");
                                hobbies = object.getString("hobbies");
                                on_famous_name_change_to = object.getString("on_famous_name_change_to");
                                aim = object.getString("aim");
                                love_wearing = object.getString("love_wearing");
                                zodiac_sign = object.getString("zodiac_sign");
                                hangout_place = object.getString("hangout_place");
                                treat_for_birthday = object.getString("treat_for_birthday");
                                weekend_activity = object.getString("weekend_activity");
                                memorable_moment = object.getString("memorable_moment");
                                embarrassing_moment = object.getString("embarrassing_moment");
                                things_want_to_do_before_die = object.getString("things_want_to_do_before_die");
                                what_bores_me_most = object.getString("what_bores_me_most");
                                m_crazy_about = object.getString("m_crazy_about");
                                my_biggest_strength = object.getString("my_biggest_strength");
                                things_i_hate = object.getString("things_i_hate");
                                when_m_happy = object.getString("when_m_happy");
                                when_m_sad = object.getString("when_m_sad");
                                when_m_mad = object.getString("when_m_mad");
                                my_worst_habit = object.getString("my_worst_habit");
                                best_thing_abt_me = object.getString("best_thing_abt_me");
                                feel_powerful_when = object.getString("feel_powerful_when");
                                biggest_achievement = object.getString("biggest_achievement");
                                my_teddy_knows = object.getString("my_teddy_knows");
                                fb = object.getString("fb");
                                address = object.getString("address");
                                phone_number = object.getString("phone_number");
                                website = object.getString("website");
                                twitter = object.getString("twitter");
                                instagram = object.getString("instagram");
                                hpy_moment_wid_u = object.getString("hpy_moment_wid_u");
                                sad_moment_wid_u = object.getString("sad_moment_wid_u");
                                good_things_about_u = object.getString("good_things_about_u");
                                bad_things_about_u = object.getString("bad_things_about_u");
                                friendship_to_me_is = object.getString("friendship_to_me_is");
                                fav_color = object.getString("fav_color");
                                fav_celebrities = object.getString("fav_celebrities");
                                fav_role_model = object.getString("fav_role_model");
                                fav_tv_show = object.getString("fav_tv_show");
                                fav_music_band = object.getString("fav_music_band");
                                fav_food = object.getString("fav_food");
                                fav_sport = object.getString("fav_sport");
                            } else {
                                Toast.makeText(SentSlamsActivity.this,"Error Occurred ! Try again later !",Toast.LENGTH_LONG).show();
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            tvNickName.setText(nick_name);
                            tvDob.setText(dob);
                            tvHobbies.setText(hobbies);
                            tvOnFamousNameChange.setText(on_famous_name_change_to);
                            tvAim.setText(aim);
                            tvLoveWearing.setText(love_wearing);
                            tvZodiacSign.setText(zodiac_sign);
                            tvHangoutPlace.setText(hangout_place);
                            tvTreatForBirthday.setText(treat_for_birthday);
                            tvWeekendActivity.setText(weekend_activity);
                            tvMemorableMoment.setText(memorable_moment);
                            tvEmbarassingMoment.setText(embarrassing_moment);
                            tvThingsToDoBeforeDie.setText(things_want_to_do_before_die);
                            tvWhatBoresMe.setText(what_bores_me_most);
                            tvMcrazyAbout.setText(m_crazy_about);
                            tvMyBiggestStrength.setText(my_biggest_strength);
                            tvThingsIHate.setText(things_i_hate);
                            tvWhenMHappy.setText(when_m_happy);
                            tvWhenMSad.setText(when_m_sad);
                            tvWhenMMad.setText(when_m_mad);
                            tvWorstHabit.setText(my_worst_habit);
                            tvBestThingAbtMe.setText(best_thing_abt_me);
                            tvFeelPowerfullWhen.setText(feel_powerful_when);
                            tvBiggestAchievement.setText(biggest_achievement);
                            tvMyTeddyKnows.setText(my_teddy_knows);
                            tvFb.setText(fb);
                            tvAddress.setText(address);
                            tvPhoneNumber.setText(phone_number);
                            tvWebsite.setText(website);
                            tvTwitter.setText(twitter);
                            tvInstagram.setText(instagram);
                            tvHpyMomentWidU.setText(hpy_moment_wid_u);
                            tvSadMomentWidU.setText(sad_moment_wid_u);
                            tvGoodThingsAbtU.setText(good_things_about_u);
                            tvBadThingsAbtU.setText(bad_things_about_u);
                            tvFriendshipToMe.setText(friendship_to_me_is);
                            tvFavColor.setText(fav_color);
                            tvFavCelebrities.setText(fav_celebrities);
                            tvFavRoleModel.setText(fav_role_model);
                            tvFavTvShow.setText(fav_tv_show);
                            tvFavMusicBand.setText(fav_music_band);
                            tvFavFood.setText(fav_food);
                            tvFavSport.setText(fav_sport);
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

                Map<String,String> params = new HashMap<String, String>();

                params.put("to_user_name",fromUserName);
                params.put("from_user_name",appPref.getUsername());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(SentSlamsActivity.this);
        queue.add(rqst);

    }

    private void init() {
        UtilsUI.getUilInstance(SentSlamsActivity.this);

        editSlam = (FloatingActionButton) findViewById(R.id.friend_profile_edit_slam_btn);
        tvNickName = (TextView) findViewById(R.id.tvNickName);
        tvDob = (TextView) findViewById(R.id.tvDob);
        tvHobbies = (TextView) findViewById(R.id.tvHobbies);
        tvOnFamousNameChange = (TextView) findViewById(R.id.tvOnFamousNameChange);
        tvAim = (TextView) findViewById(R.id.tvAim);
        tvLoveWearing = (TextView) findViewById(R.id.tvLoveWearing);
        tvZodiacSign = (TextView) findViewById(R.id.tvZodiacSign);
        tvHangoutPlace = (TextView) findViewById(R.id.tvHangoutPlace);
        tvTreatForBirthday = (TextView) findViewById(R.id.tvTreatForBirthday);
        tvWeekendActivity = (TextView) findViewById(R.id.tvWeekendActivity);
        tvMemorableMoment = (TextView) findViewById(R.id.tvMemorableMoment);
        tvEmbarassingMoment = (TextView) findViewById(R.id.tvEmbarassingMoment);
        tvThingsToDoBeforeDie = (TextView) findViewById(R.id.tvThingsToDoBeforeDie);
        tvWhatBoresMe = (TextView) findViewById(R.id.tvWhatBoresMe);
        tvMcrazyAbout = (TextView) findViewById(R.id.tvMcrazyAbout);
        tvMyBiggestStrength = (TextView) findViewById(R.id.tvMyBiggestStrength);
        tvThingsIHate = (TextView) findViewById(R.id.tvThingsIHate);
        tvWhenMHappy = (TextView) findViewById(R.id.tvWhenMHappy);
        tvWhenMSad = (TextView) findViewById(R.id.tvWhenMSad);
        tvWhenMMad = (TextView) findViewById(R.id.tvWhenMMad);
        tvWorstHabit = (TextView) findViewById(R.id.tvWorstHabit);
        tvBestThingAbtMe = (TextView) findViewById(R.id.tvBestThingAbtMe);
        tvFeelPowerfullWhen = (TextView) findViewById(R.id.tvFeelPowerfullWhen);
        tvBiggestAchievement = (TextView) findViewById(R.id.tvBiggestAchievement);
        tvMyTeddyKnows = (TextView) findViewById(R.id.tvMyTeddyKnows);
        tvFb = (TextView) findViewById(R.id.tvFb);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        tvWebsite = (TextView) findViewById(R.id.tvWebsite);
        tvTwitter = (TextView) findViewById(R.id.tvTwitter);
        tvInstagram = (TextView) findViewById(R.id.tvInstagram);
        tvHpyMomentWidU = (TextView) findViewById(R.id.tvHpyMomentWidU);
        tvSadMomentWidU = (TextView) findViewById(R.id.tvSadMomentWidU);
        tvGoodThingsAbtU = (TextView) findViewById(R.id.tvGoodThingsAbtU);
        tvBadThingsAbtU = (TextView) findViewById(R.id.tvBadThingsAbtU);
        tvFriendshipToMe = (TextView) findViewById(R.id.tvFriendshipToMe);
        tvFavColor = (TextView) findViewById(R.id.tvFavColor);
        tvFavCelebrities = (TextView) findViewById(R.id.tvFavCelebrities);
        tvFavRoleModel = (TextView) findViewById(R.id.tvFavRoleModel);
        tvFavTvShow = (TextView) findViewById(R.id.tvFavTvShow);
        tvFavMusicBand = (TextView) findViewById(R.id.tvFavMusicBand);
        tvFavFood = (TextView) findViewById(R.id.tvFavFood);
        tvFavSport = (TextView) findViewById(R.id.tvFavSport);

        imageView = (ImageView) findViewById(R.id.sent_slam_activity_profile_image);
        progressBarImage = (ProgressBar) findViewById(R.id.sent_slam_activity_profile_image_progrss_bar);

        //views for hide & show
        llAbtMe = (LinearLayout) findViewById(R.id.ll_views_abt_frnd);
        llContact = (LinearLayout) findViewById(R.id.ll_views_contacts);
        llMoments = (LinearLayout) findViewById(R.id.ll_views_your_moments);
        llFavs = (LinearLayout) findViewById(R.id.ll_views_favourites);
        llMoreAbtMe = (LinearLayout) findViewById(R.id.ll_views_more_abt_frnd);
        llAbtUrFrnd = (LinearLayout) findViewById(R.id.ll_views_abt_you);
        btnAbtMe = (Button) findViewById(R.id.btn_abt_frnd);
        btnContact = (Button) findViewById(R.id.btn_contacts);
        btnMoments = (Button) findViewById(R.id.btn_your_moments);
        btnFavs = (Button) findViewById(R.id.btn_favourites);
        btnMoreAbtMe = (Button) findViewById(R.id.btn_more_abt_frnd);
        btnAbtUrFrnd = (Button) findViewById(R.id.btn_abt_you);
        btnAbtMe.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnMoments.setOnClickListener(this);
        btnFavs.setOnClickListener(this);
        btnMoreAbtMe.setOnClickListener(this);
        btnAbtUrFrnd.setOnClickListener(this);

        Drawable dDown = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_down_white);
        Drawable dRight = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_right_white);
        btnAbtMe.setCompoundDrawablesWithIntrinsicBounds(null,null,dDown,null);
        btnContact.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnMoments.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnFavs.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnMoreAbtMe.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnAbtUrFrnd.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);

        editSlam.setOnClickListener(this);

    }

    private void setHeader() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SentSlamsActivity.this.setTitle("Friend");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPref.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPref.getPrimaryColorPref());
            AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            appBarLayout.setBackgroundColor(appPref.getPrimaryColorPref());
        }
        UtilsUI.applyFontForToolbarTitle(SentSlamsActivity.this);
        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        UtilsUI.applyFontForCollpasingToolbar(this,toolbarLayout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.friend_profile_edit_slam_btn:
                Intent intent = new Intent(SentSlamsActivity.this,UpdateSlamActivity.class);
                intent.putExtra(JSON_DATA,jsonData);
                intent.putExtra(INTENT_USERNAME,fromUserName);
                startActivity(intent);
                break;

            case R.id.btn_abt_frnd:
                if (llAbtMe.isShown()) {
                    llAbtMe.setVisibility(View.GONE);
                    btnAbtMe.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llAbtMe.setVisibility(View.VISIBLE);
                    btnAbtMe.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
                }
                break;
            case R.id.btn_contacts:
                if (llContact.isShown()) {
                    llContact.setVisibility(View.GONE);
                    btnContact.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llContact.setVisibility(View.VISIBLE);
                    btnContact.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
                }
                break;
            case R.id.btn_your_moments:
                if (llMoments.isShown()) {
                    llMoments.setVisibility(View.GONE);
                    btnMoments.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llMoments.setVisibility(View.VISIBLE);
                    btnMoments.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
                }
                break;
            case R.id.btn_favourites:
                if (llFavs.isShown()) {
                    llFavs.setVisibility(View.GONE);
                    btnFavs.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llFavs.setVisibility(View.VISIBLE);
                    btnFavs.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
                }
                break;
            case R.id.btn_more_abt_frnd:
                if (llMoreAbtMe.isShown()) {
                    llMoreAbtMe.setVisibility(View.GONE);
                    btnMoreAbtMe.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llMoreAbtMe.setVisibility(View.VISIBLE);
                    btnMoreAbtMe.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
                }
                break;
            case R.id.btn_abt_you:
                if (llAbtUrFrnd.isShown()) {
                    llAbtUrFrnd.setVisibility(View.GONE);
                    btnAbtUrFrnd.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llAbtUrFrnd.setVisibility(View.VISIBLE);
                    btnAbtUrFrnd.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sent_slams_activity_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sent_slam_info) {
            MaterialDialog dialog = UtilDialogs.showString(SentSlamsActivity.this,"About the slam",
                    "Sent to - " + name + "\nUsername - "+ fromUserName + "\nSent On - "+ sentOn + "\nUpdated On - " + updateON,R.drawable.ic_about_info_black);
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
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
