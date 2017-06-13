package com.gmonetix.slambook.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Gmonetix
 */

public class Funcs {

    public static String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static void saveProfileImage(Context context,Bitmap bitmap) {
        FileOutputStream out;
        try {
            out = context.openFileOutput("slambook_gmonetix_profile.png", Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Drawable getImageDrawable(Context context){
        try{
            FileInputStream fis = context.openFileInput("slambook_gmonetix_profile.png");
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            if (b != null)
                return new BitmapDrawable(context.getResources(),b);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void goToGooglePlay(Context context, String id) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + id)));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + id)));
        }
    }

}
