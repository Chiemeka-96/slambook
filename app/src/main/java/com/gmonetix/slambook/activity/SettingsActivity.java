package com.gmonetix.slambook.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.fragments.SettingsFragment;
import com.gmonetix.slambook.helper.Device;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.UtilsUI;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * @author Gmonetix
 */

public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private AdView adView;

    private AppPref appPref;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_settings);
        appPref = App.getAppPref();
        setHeader();

        MobileAds.initialize(getApplicationContext(),getResources().getString(R.string.ad_settings_activity));
        adView = (AdView) findViewById(R.id.adViewSettingsActivity);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(Device.getId(SettingsActivity.this)).build();
        adView.loadAd(adRequest);

        getFragmentManager().beginTransaction().replace(R.id.frame_container, new SettingsFragment()).commit();

    }

    private void setHeader() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SettingsActivity.this.setTitle("Settings");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPref.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPref.getPrimaryColorPref());
        }
        UtilsUI.applyFontForToolbarTitle(SettingsActivity.this);
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
