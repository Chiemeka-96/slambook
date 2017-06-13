package com.gmonetix.slambook.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.helper.Device;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WriteSlamActivity extends AppCompatActivity implements View.OnClickListener {

    private AppPref appPref;

    private AdView adView;
    private Toolbar toolbar;
    private EditText etNickName,etHobbies,etOnFamousNameChange,etAim,etLoveWearing,etZodiacSign,
            etHangoutPlace,etTreatForBirthday,etWeekendActivity,etMemorableMoment,etEmbarassingMoment,etThingsToDoBeforeDie,etWhatBoresMe,
            etMcrazyAbout,etMyBiggestStrength,etThingsIHate,etWhenMHappy,etWhenMSad,etWhenMMad,etWorstHabit,etBestThingAbtMe,etFeelPowerfullWhen,
            etBiggestAchievement,etMyTeddyKnows,etFb,etAddress,etPhoneNumber,etWebsite,etTwitter,etInstagram,etHpyMomentWidU,etSadMomentWidU,
            etGoodThingsAbtU,etBadThingsAbtU,etFriendshipToMe,etFavColor,etFavCelebrities,etFavRoleModel,etFavTvShow,etFavMusicBand,etFavFood,etFavSport;
    private TextView tvDob;

    //views for hide & show
    private LinearLayout llAbtU, llContact, llMoments, llFavs, llMoreAbtU, llAbtUrFrnd;
    private Button btnAbtU, btnContact, btnMoments, btnFavs, btnMoreAbtU, btnAbtUrFrnd;

    private String NickName = "",Hobbies = "",OnFamousNameChange = "",Aim = "",LoveWearing = "",ZodiacSign = "",
            HangoutPlace = "",TreatForBirthday = "",WeekendActivity = "",MemorableMoment = "",EmbarassingMoment = "",ThingsToDoBeforeDie = "",WhatBoresMe = "",
            McrazyAbout = "",MyBiggestStrength = "",ThingsIHate = "",WhenMHappy = "",WhenMSad = "",WhenMMad = "",WorstHabit = "",BestThingAbtMe = "",FeelPowerfullWhen = "",
            BiggestAchievement = "",MyTeddyKnows = "",Fb = "",Address = "",PhoneNumber = "",Website = "",Twitter = "",Instagram = "",HpyMomentWidU = "",SadMomentWidU = "",
            GoodThingsAbtU = "",BadThingsAbtU = "",FriendshipToMe = "",FavColor = "",FavCelebrities = "",FavRoleModel = "",FavTvShow = "",FavMusicBand = "",FavFood = "",FavSport = "";
    private String toUserName = "",toName="",Dob="";
    private static final int DATE_PICKER_DIALOG_ID = 0;
    private static final String USERNAME_INTENT = "username";
    private static final String NAME_INTENT = "name";
    private int year_x,month_x,day_x;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_write_slam);
        appPref = App.getAppPref();
        setHeader();

        MobileAds.initialize(getApplicationContext(),getResources().getString(R.string.ad_write_slam_activity));
        adView = (AdView) findViewById(R.id.adViewWriteSlam);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Device.getId(WriteSlamActivity.this)).build();
        adView.loadAd(adRequest);

        init();

        if (getIntent().hasExtra(USERNAME_INTENT)) {
            toUserName = getIntent().getExtras().getString(USERNAME_INTENT);
            toName = getIntent().getExtras().getString(NAME_INTENT);
        } else {
            Toast.makeText(WriteSlamActivity.this,"Some error occurred ! Try again",Toast.LENGTH_SHORT).show();
            finish();
        }
        WriteSlamActivity.this.setTitle(toName);

        Dob = appPref.getDOB();
        tvDob.setText(Dob);
        String[] date = Dob.split("/");
        day_x = Integer.parseInt(date[0]);
        month_x = Integer.parseInt(date[1])-1;
        year_x = Integer.parseInt(date[2]);

    }

    private void init() {

        etNickName = (EditText) findViewById(R.id.etNickName);
        etHobbies = (EditText) findViewById(R.id.etHobbies);
        etOnFamousNameChange = (EditText) findViewById(R.id.etOnFamousNameChange);
        etAim = (EditText) findViewById(R.id.etAim);
        etLoveWearing = (EditText) findViewById(R.id.etLoveWearing);
        etZodiacSign = (EditText) findViewById(R.id.etZodiacSign);
        etHangoutPlace = (EditText) findViewById(R.id.etHangoutPlace);
        etTreatForBirthday = (EditText) findViewById(R.id.etTreatForBirthday);
        etWeekendActivity = (EditText) findViewById(R.id.etWeekendActivity);
        etMemorableMoment = (EditText) findViewById(R.id.etMemorableMoment);
        etEmbarassingMoment = (EditText) findViewById(R.id.etEmbarassingMoment);
        etThingsToDoBeforeDie = (EditText) findViewById(R.id.etThingsToDoBeforeDie);
        etWhatBoresMe = (EditText) findViewById(R.id.etWhatBoresMe);
        etMcrazyAbout = (EditText) findViewById(R.id.etMcrazyAbout);
        etMyBiggestStrength = (EditText) findViewById(R.id.etMyBiggestStrength);
        etThingsIHate = (EditText) findViewById(R.id.etThingsIHate);
        etWhenMHappy = (EditText) findViewById(R.id.etWhenMHappy);
        etWhenMSad = (EditText) findViewById(R.id.etWhenMSad);
        etWhenMMad = (EditText) findViewById(R.id.etWhenMMad);
        etWorstHabit = (EditText) findViewById(R.id.etWorstHabit);
        etBestThingAbtMe = (EditText) findViewById(R.id.etBestThingAbtMe);
        etFeelPowerfullWhen = (EditText) findViewById(R.id.etFeelPowerfullWhen);
        etBiggestAchievement = (EditText) findViewById(R.id.etBiggestAchievement);
        etMyTeddyKnows = (EditText) findViewById(R.id.etMyTeddyKnows);
        etFb = (EditText) findViewById(R.id.etFb);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etWebsite = (EditText) findViewById(R.id.etWebsite);
        etTwitter = (EditText) findViewById(R.id.etTwitter);
        etInstagram = (EditText) findViewById(R.id.etInstagram);
        etHpyMomentWidU = (EditText) findViewById(R.id.etHpyMomentWidU);
        etSadMomentWidU = (EditText) findViewById(R.id.etSadMomentWidU);
        etGoodThingsAbtU = (EditText) findViewById(R.id.etGoodThingsAbtU);
        etBadThingsAbtU = (EditText) findViewById(R.id.etBadThingsAbtU);
        etFriendshipToMe = (EditText) findViewById(R.id.etFriendshipToMe);
        etFavColor = (EditText) findViewById(R.id.etFavColor);
        etFavCelebrities = (EditText) findViewById(R.id.etFavCelebrities);
        etFavRoleModel = (EditText) findViewById(R.id.etFavRoleModel);
        etFavTvShow = (EditText) findViewById(R.id.etFavTvShow);
        etFavMusicBand = (EditText) findViewById(R.id.etFavMusicBand);
        etFavFood = (EditText) findViewById(R.id.etFavFood);
        etFavSport = (EditText) findViewById(R.id.etFavSport);

        tvDob = (TextView) findViewById(R.id.tvDob);
        tvDob.setOnClickListener(this);

        //views for hide & show
        llAbtU = (LinearLayout) findViewById(R.id.ll_views_abt_u);
        llContact = (LinearLayout) findViewById(R.id.ll_views_contacts);
        llMoments = (LinearLayout) findViewById(R.id.ll_views_your_moments);
        llFavs = (LinearLayout) findViewById(R.id.ll_views_favourites);
        llMoreAbtU = (LinearLayout) findViewById(R.id.ll_views_more_abt_u);
        llAbtUrFrnd = (LinearLayout) findViewById(R.id.ll_views_abt_your_friend);
        btnAbtU = (Button) findViewById(R.id.btn_abt_u);
        btnContact = (Button) findViewById(R.id.btn_contacts);
        btnMoments = (Button) findViewById(R.id.btn_your_moments);
        btnFavs = (Button) findViewById(R.id.btn_favourites);
        btnMoreAbtU = (Button) findViewById(R.id.btn_more_abt_u);
        btnAbtUrFrnd = (Button) findViewById(R.id.btn_abt_your_friend);
        btnAbtU.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnMoments.setOnClickListener(this);
        btnFavs.setOnClickListener(this);
        btnMoreAbtU.setOnClickListener(this);
        btnAbtUrFrnd.setOnClickListener(this);

        Drawable dDown = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_down_white);
        Drawable dRight = AppCompatResources.getDrawable(this, R.drawable.ic_arrow_right_white);
        btnAbtU.setCompoundDrawablesWithIntrinsicBounds(null,null,dDown,null);
        btnContact.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnMoments.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnFavs.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnMoreAbtU.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);
        btnAbtUrFrnd.setCompoundDrawablesWithIntrinsicBounds(null,null,dRight,null);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_PICKER_DIALOG_ID) {
            return new DatePickerDialog(this, dPickerListener, year_x, month_x, day_x);
        }
        return  null;
    }

    private DatePickerDialog.OnDateSetListener dPickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            year_x = year;
            month_x = month + 1;
            day_x = dayOfMonth;
            Dob = String.valueOf(day_x) + "/" + String.valueOf(month_x) + "/" + String.valueOf(year_x);
            tvDob.setText(Dob);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tvDob:
                showDialog(DATE_PICKER_DIALOG_ID);
                break;

            case R.id.btn_abt_u:
                if (llAbtU.isShown()) {
                    llAbtU.setVisibility(View.GONE);
                    btnAbtU.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llAbtU.setVisibility(View.VISIBLE);
                    btnAbtU.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
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
            case R.id.btn_more_abt_u:
                if (llMoreAbtU.isShown()) {
                    llMoreAbtU.setVisibility(View.GONE);
                    btnMoreAbtU.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_right_white),null);
                } else {
                    llMoreAbtU.setVisibility(View.VISIBLE);
                    btnMoreAbtU.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(R.drawable.ic_arrow_down_white),null);
                }
                break;
            case R.id.btn_abt_your_friend:
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

    private void setHeader() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPref.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPref.getPrimaryColorPref());
        }
        UtilsUI.applyFontForToolbarTitle(WriteSlamActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.write_slam_activity_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_send_slam) {
            if (validate()) {
                sendSlam();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendSlam() {

        NickName = etNickName.getText().toString().trim();
        Hobbies = etHobbies.getText().toString().trim();
        OnFamousNameChange = etOnFamousNameChange.getText().toString().trim();
        Aim = etAim.getText().toString().trim();
        LoveWearing = etLoveWearing.getText().toString().trim();
        ZodiacSign = etZodiacSign.getText().toString().trim();
        HangoutPlace = etHangoutPlace.getText().toString().trim();
        TreatForBirthday = etTreatForBirthday.getText().toString().trim();
        WeekendActivity = etWeekendActivity.getText().toString().trim();
        MemorableMoment = etMemorableMoment.getText().toString().trim();
        EmbarassingMoment = etEmbarassingMoment.getText().toString().trim();
        ThingsToDoBeforeDie = etThingsToDoBeforeDie.getText().toString().trim();
        WhatBoresMe = etWhatBoresMe.getText().toString().trim();
        McrazyAbout = etMcrazyAbout.getText().toString().trim();
        MyBiggestStrength = etMyBiggestStrength.getText().toString().trim();
        ThingsIHate = etThingsIHate.getText().toString().trim();
        WhenMHappy = etWhenMHappy.getText().toString().trim();
        WhenMSad = etWhenMSad.getText().toString().trim();
        WhenMMad = etWhenMMad.getText().toString().trim();
        WorstHabit = etWorstHabit.getText().toString().trim();
        BestThingAbtMe = etBestThingAbtMe.getText().toString().trim();
        FeelPowerfullWhen = etFeelPowerfullWhen.getText().toString().trim();
        BiggestAchievement = etBiggestAchievement.getText().toString().trim();
        MyTeddyKnows = etMyTeddyKnows.getText().toString().trim();
        Fb = etFb.getText().toString().trim();
        Address = etAddress.getText().toString().trim();
  //      PhoneNumber = etPhoneNumber.getText().toString().trim();
        Website = etWebsite.getText().toString().trim();
        Twitter = etTwitter.getText().toString().trim();
        Instagram = etInstagram.getText().toString().trim();
        HpyMomentWidU = etHpyMomentWidU.getText().toString().trim();
        SadMomentWidU = etSadMomentWidU.getText().toString().trim();
        GoodThingsAbtU = etGoodThingsAbtU.getText().toString().trim();
        BadThingsAbtU = etBadThingsAbtU.getText().toString().trim();
        FriendshipToMe = etFriendshipToMe.getText().toString().trim();
        FavColor = etFavColor.getText().toString().trim();
        FavCelebrities = etFavCelebrities.getText().toString().trim();
        FavRoleModel = etFavRoleModel.getText().toString().trim();
        FavTvShow = etFavTvShow.getText().toString().trim();
        FavMusicBand = etFavMusicBand.getText().toString().trim();
        FavFood = etFavFood.getText().toString().trim();
        FavSport = etFavSport.getText().toString().trim();

        RequestQueue queue = Volley.newRequestQueue(WriteSlamActivity.this);
        StringRequest stringrqst = new StringRequest(Request.Method.POST, Const.write_slam,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject object = jsonArray.getJSONObject(0);
                            String code = object.getString("code");
                            if (code.equals("success")) {
                                Toast.makeText(WriteSlamActivity.this,object.getString("message"),Toast.LENGTH_LONG).show();
                                finish();
                            } else if (code.equals("unsuccess_already_present")){
                                Toast.makeText(WriteSlamActivity.this,object.getString("message"),Toast.LENGTH_LONG).show();
                            } else if (code.equals("unsuccess_not_sent")){
                                Toast.makeText(WriteSlamActivity.this,object.getString("message"),Toast.LENGTH_LONG).show();
                            } else Toast.makeText(WriteSlamActivity.this,"Unknown Error !",Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(WriteSlamActivity.this,"Unknown Error ! Try again later !",Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
                Toast.makeText(getApplicationContext(),volleyError + "Error",Toast.LENGTH_LONG).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();

                params.put("to_user_name",toUserName);
                params.put("from_user_name",appPref.getUsername());
                params.put("sent_on", DateFormat.getDateTimeInstance().format(new Date()));
                params.put("name",appPref.getName());
                params.put("nick_name",NickName);
                params.put("dob",Dob);
                params.put("hobbies",Hobbies);
                params.put("on_famous_name_change_to",OnFamousNameChange);
                params.put("aim",Aim);
                params.put("love_wearing",LoveWearing);
                params.put("zodiac_sign",ZodiacSign);
                params.put("hangout_place",HangoutPlace);
                params.put("treat_for_birthday",TreatForBirthday);
                params.put("weekend_activity",WeekendActivity);
                params.put("memorable_moment",MemorableMoment);
                params.put("embarrassing_moment",EmbarassingMoment);
                params.put("things_want_to_do_before_die",ThingsToDoBeforeDie);
                params.put("what_bores_me_most",WhatBoresMe);
                params.put("m_crazy_about",McrazyAbout);
                params.put("my_biggest_strength",MyBiggestStrength);
                params.put("things_i_hate",ThingsIHate);
                params.put("when_m_happy",WhenMHappy);
                params.put("when_m_sad",WhenMMad);
                params.put("when_m_mad",WhenMMad);
                params.put("my_worst_habit",WorstHabit);
                params.put("best_thing_abt_me",BestThingAbtMe);
                params.put("feel_powerful_when",FeelPowerfullWhen);
                params.put("biggest_achievement",BiggestAchievement);
                params.put("my_teddy_knows",MyTeddyKnows);
                params.put("fb",Fb);
                params.put("address",Address);
                params.put("phone_number",PhoneNumber);
                params.put("website",Website);
                params.put("twitter",Twitter);
                params.put("instagram",Instagram);
                params.put("hpy_moment_wid_u",HpyMomentWidU);
                params.put("sad_moment_wid_u",SadMomentWidU);
                params.put("good_things_about_u",GoodThingsAbtU);
                params.put("bad_things_about_u",BadThingsAbtU);
                params.put("friendship_to_me_is",FriendshipToMe);
                params.put("fav_color",FavColor);
                params.put("fav_celebrities",FavCelebrities);
                params.put("fav_role_model",FavRoleModel);
                params.put("fav_tv_show",FavTvShow);
                params.put("fav_music_band",FavMusicBand);
                params.put("fav_food",FavFood);
                params.put("fav_sport",FavSport);

                return checkParams(params);
            }

            private Map<String, String> checkParams(Map<String, String> map){
                Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
                    if(pairs.getValue()==null){
                        map.put(pairs.getKey(), "");
                    }
                }
                return map;
            }
        };
        stringrqst.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        queue.add(stringrqst);
    }

    public boolean validate() {
        boolean valid = true;

        PhoneNumber = etPhoneNumber.getText().toString().trim();

        if (PhoneNumber.isEmpty() || PhoneNumber.length() < 8 || PhoneNumber.length() > 15) {
            etPhoneNumber.setError("enter valid phone number");
            valid = false;
        } else {
            etPhoneNumber.setError(null);
        }

        return valid;
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
