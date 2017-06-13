package com.gmonetix.slambook.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gmonetix.slambook.R;

/**
 * @author Gmonetix
 */

public class AppPref {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String KEY_PRIMARY_COLOR = "colorPrefPrimary";

    private static final String PREF_LOGIN_STATUS = "login_status";
    private static final String PREF_PHONE_VISIBILITY = "phone_visibility";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_NAME = "name";
    private static final String PREF_PASSWORD = "password";
    private static final String PREF_NUMBER = "number";
    private static final String PREF_DOB = "dob";
    private static final String PREF_DESCRIPTION = "description";
    private static final String PREF_EMAIL = "email";
    private static final String PREF_IMAGE_URL = "profile_image_url";
    private static final String PREF_REGISTERED_AT = "registered_at";
    private static final String PREF_GENDER = "gender";
    private static final String PREF_FIREBASE_UID = "firebase_uid";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";

    private static final String PROFILE_IMAGE = "profile_image";


    public AppPref(Context context) {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sharedPreferences.edit();
        this.context = context;
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return sharedPreferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void setLoggedIn(boolean value) {
        editor.putBoolean(PREF_LOGIN_STATUS,value);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(PREF_LOGIN_STATUS,false);
    }

    public void setPhoneVisibility(String value) {
        editor.putString(PREF_PHONE_VISIBILITY,value);
        editor.commit();
    }

    public String getPhoneVisibility() {
        return sharedPreferences.getString(PREF_PHONE_VISIBILITY,"1");
    }

    public void setName(String name) {
        editor.putString(PREF_NAME,name);
        editor.commit();
    }

    public String getName() {
        return sharedPreferences.getString(PREF_NAME, "johnson");
    }

    public void setUsername(String username) {
        editor.putString(PREF_USERNAME,username);
        editor.commit();
    }

    public String getUsername() {
        return sharedPreferences.getString(PREF_USERNAME, "johnson");
    }

    public void setPassword(String password) {
        editor.putString(PREF_PASSWORD,password);
        editor.commit();
    }

    public String getPassword() {
        return sharedPreferences.getString(PREF_PASSWORD, "");
    }

    public void setEmail(String email) {
        editor.putString(PREF_EMAIL,email);
        editor.commit();
    }

    public String getEmail() {
        return sharedPreferences.getString(PREF_EMAIL, "johnson@example.com");
    }

    public void setGender(String gender) {
        editor.putString(PREF_GENDER,gender);
        editor.commit();
    }

    public String getGender() {
        return sharedPreferences.getString(PREF_GENDER, "MALE");
    }

    public void setFirebaseUid(String id) {
        editor.putString(PREF_FIREBASE_UID,id);
        editor.commit();
    }

    public String getFirebaseUid() {
        return sharedPreferences.getString(PREF_FIREBASE_UID, "0");
    }

    public void setRegisteredAt(String date) {
        editor.putString(PREF_REGISTERED_AT,date);
        editor.commit();
    }

    public String getRegisteredAt() {
        return sharedPreferences.getString(PREF_REGISTERED_AT, "01 JANUARY 1999");
    }

    public void setNumber(String number) {
        editor.putString(PREF_NUMBER,number);
        editor.commit();
    }

    public String getNumber() {
        return sharedPreferences.getString(PREF_NUMBER, "1001010010");
    }

    public void setDOB(String dob) {
        editor.putString(PREF_DOB,dob);
        editor.commit();
    }

    public String getDOB() {
        return sharedPreferences.getString(PREF_DOB, "01/01/1999");
    }

    public void setDescription(String desc) {
        editor.putString(PREF_DESCRIPTION,desc);
        editor.commit();
    }

    public String getDescription() {
        return sharedPreferences.getString(PREF_DESCRIPTION, "this is a dummy content");
    }

    public void setProfileImageUrl(String url) {
        editor.putString(PREF_IMAGE_URL,url);
        editor.commit();
    }

    public String getProfileImageUrl() {
        return sharedPreferences.getString(PREF_IMAGE_URL, "");
    }

    public int getPrimaryColorPref() {
        return sharedPreferences.getInt(KEY_PRIMARY_COLOR, context.getResources().getColor(R.color.colorPrimary));
    }

    public boolean getNotification(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_title_notif), true);
    }

    public String getRingtone(){
        return sharedPreferences.getString(context.getString(R.string.pref_title_ringtone), "content://settings/system/notification_sound");
    }

    public boolean getVibration(){
        return sharedPreferences.getBoolean(context.getString(R.string.pref_title_vibrate), true);
    }

    public String getProfileImagePath() {
        return sharedPreferences.getString(PROFILE_IMAGE, "");
    }

    public void setProfileImagePath(String path) {
        editor.putString(PROFILE_IMAGE,path);
        editor.commit();
    }

}
