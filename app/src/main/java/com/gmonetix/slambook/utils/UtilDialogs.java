package com.gmonetix.slambook.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.content.res.AppCompatResources;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmonetix.slambook.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Gmonetix
 */

public class UtilDialogs {

    public static MaterialDialog editTextDialog(Context context, String title,String positiveText, int drawableId) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .icon(context.getResources().getDrawable(drawableId))
                .customView(R.layout.edit_text_dialog,false)
                .typeface("RobotoLight.ttf","RobotoLight.ttf")
                .positiveText(positiveText)
                .negativeText("CANCEL")
                .autoDismiss(false)
                .cancelable(false)
                .build();
    }

    public static MaterialDialog imageViewDialog(Context context, String title, int drawableId) {
        return   new MaterialDialog.Builder(context)
                .title(title)
                .typeface("RobotoLight.ttf","RobotoLight.ttf")
                .icon(context.getResources().getDrawable(drawableId))
                .customView(R.layout.image_view_dialog,false)
                .cancelable(true)
                .build();
    }

    public static MaterialDialog showString(Context context, String title,String content, int drawableId) {
        return   new MaterialDialog.Builder(context)
                .title(title)
                .typeface("RobotoLight.ttf","RobotoLight.ttf")
                .icon(context.getResources().getDrawable(drawableId))
                .content(content)
                .positiveText("OK")
                .cancelable(true)
                .build();
    }

}
