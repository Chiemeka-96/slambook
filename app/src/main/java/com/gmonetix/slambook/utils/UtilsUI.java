package com.gmonetix.slambook.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.gmonetix.slambook.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Gmonetix
 */

public class UtilsUI {

    public static int darker (int color, double factor) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);

        return Color.argb(a, Math.max((int) (r * factor), 0), Math.max((int) (g * factor), 0), Math.max((int) (b * factor), 0));
    }

    public static void setFont(Context _context, TextView textView) {
        Typeface roboto = Typeface.createFromAsset(_context.getAssets(), "fonts/RobotoLight.ttf");
        textView.setTypeface(roboto);
    }

    public static void applyFontForToolbarTitle(Activity activity){
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                if(tv.getText().equals(toolbar.getTitle())){
                    setFont(activity,tv);
                    break;
                }
            }
        }
    }

    public static void applyFontForCollpasingToolbar(Context activity, CollapsingToolbarLayout toolbarLayout){
        Typeface roboto = Typeface.createFromAsset(activity.getAssets(), "fonts/RobotoLight.ttf");
        toolbarLayout.setExpandedTitleTypeface(roboto);
        toolbarLayout.setCollapsedTitleTypeface(roboto);
    }

    public static void showFileChooser(Activity activty, int REQUEST_CODE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activty.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
    }

    public static void getUilInstance(Context context){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }

}
