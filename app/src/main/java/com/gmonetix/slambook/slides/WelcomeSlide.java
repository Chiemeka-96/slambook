package com.gmonetix.slambook.slides;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro2;
import com.gmonetix.slambook.App;
import com.gmonetix.slambook.activity.LoginActivity;
import com.gmonetix.slambook.utils.AppPref;

/**
 * @author Gmonetix
 */

public class WelcomeSlide extends AppIntro2 {

    private AppPref appPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appPref = App.getAppPref();

        if (!appPref.isFirstTimeLaunch()) {
            launchHomeScreen();
            WelcomeSlide.this.finish();
        }

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        addSlide(new FirstSlide());
        addSlide(new SecondSlide());
        addSlide(new ThirdSlide());
        addSlide(new FourthSlide());

        showSkipButton(true);
        setProgressButtonEnabled(true);

        setCustomTransformer(new ZoomOutPageTransformer());

    }

    private void launchHomeScreen() {
        startActivity(new Intent(WelcomeSlide.this, LoginActivity.class));
        finish();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        appPref.setFirstTimeLaunch(false);
        launchHomeScreen();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        appPref.setFirstTimeLaunch(false);
        launchHomeScreen();

    }

    public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

}
