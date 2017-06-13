package com.gmonetix.slambook;

import android.app.Application;

import com.gmonetix.slambook.utils.AppPref;

/**
 * @author Gmonetix
 */

public class App extends Application {

    private static AppPref appPref;
    private static boolean sIsChatActivityOpen = false;

    @Override
    public void onCreate() {
        super.onCreate();
        appPref = new AppPref(this);
    }

    public static AppPref getAppPref() {
        return appPref;
    }

    public static boolean isChatActivityOpen() {
        return sIsChatActivityOpen;
    }

    public static void setChatActivityOpen(boolean isChatActivityOpen) {
        App.sIsChatActivityOpen = isChatActivityOpen;
    }

}
