package com.gmonetix.slambook.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.fragments.ChatFragment;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.UtilsUI;

public class ChatActivity extends AppCompatActivity {

    private AppPref appPref;
    private Toolbar toolbar;

    public static void startActivity(Context context,String name, String receiver, String receiverUid, String firebaseToken) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Const.ARG_RECEIVER, receiver);
        intent.putExtra(Const.ARG_RECEIVER_UID, receiverUid);
        intent.putExtra(Const.ARG_FIREBASE_TOKEN, firebaseToken);
        intent.putExtra("name",name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_chat);
        appPref = App.getAppPref();
        setHeader();
        init();
    }

    private void setHeader() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ChatActivity.this.setTitle(getIntent().getExtras().getString("name"));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(UtilsUI.darker(appPref.getPrimaryColorPref(), 0.8));
            toolbar.setBackgroundColor(appPref.getPrimaryColorPref());
        }
        UtilsUI.applyFontForToolbarTitle(ChatActivity.this);
    }

    private void init() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_content_chat,
                ChatFragment.newInstance(getIntent().getExtras().getString(Const.ARG_RECEIVER),
                        getIntent().getExtras().getString(Const.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(Const.ARG_FIREBASE_TOKEN)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.setChatActivityOpen(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.setChatActivityOpen(false);
    }
}
