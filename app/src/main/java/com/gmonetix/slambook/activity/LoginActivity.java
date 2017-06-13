package com.gmonetix.slambook.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
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
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.Funcs;
import com.gmonetix.slambook.utils.SharedPrefUtil;
import com.gmonetix.slambook.utils.UtilDialogs;
import com.gmonetix.slambook.utils.UtilsUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * @author Gmonetix
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private Button login;
    private TextView forgotpassword, register;

    private String username, password;

    private AppPref appPref;
    private static final String INTENT_USERNAME = "intent_username";
    private static final String INTENT_PASSWORD = "intent_password";

    private ProgressDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_login);
        this.appPref = App.getAppPref();
        checkStatus();
        init();

        login.setOnClickListener(this);
        forgotpassword.setOnClickListener(this);
        register.setOnClickListener(this);

    }

    private void checkStatus() {
        if (appPref.isLoggedIn()) {
            startActivity(new Intent(LoginActivity.this,Home.class));
            finish();
        }

        if (getIntent().hasExtra(INTENT_USERNAME) && getIntent().hasExtra(INTENT_PASSWORD)) {
            etUsername.setText(getIntent().getExtras().getString(INTENT_USERNAME));
            etPassword.setText(getIntent().getExtras().getString(INTENT_PASSWORD));
        }

    }

    private void init() {
        UtilsUI.getUilInstance(LoginActivity.this);
        etUsername = (EditText) findViewById(R.id.username_login);
        etPassword = (EditText) findViewById(R.id.password_login);
        login = (Button) findViewById(R.id.btn_login);
        forgotpassword = (TextView) findViewById(R.id.forgot_password_login_screen);
        register = (TextView) findViewById(R.id.register_from_login_screen);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (validate()) {
                    dialog = new ProgressDialog(LoginActivity.this);
                    dialog.setTitle("Logging in");
                    dialog.setMessage("Please Wait...");
                    dialog.show();
                    login();
                }
                break;

            case R.id.register_from_login_screen:
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
                finish();
                break;

            case R.id.forgot_password_login_screen:
                final MaterialDialog dialog = UtilDialogs.editTextDialog(LoginActivity.this,"Forgot Password", "SEND", R.drawable.ic_forgot_password_black);
                final EditText editText = (EditText) dialog.findViewById(R.id.et_dialog);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final String email = editText.getText().toString().trim();
                        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                        StringRequest rqst = new StringRequest(Request.Method.POST,Const.forgot_password,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        try {
                                            JSONArray array = new JSONArray(s);
                                            JSONObject object = array.getJSONObject(0);
                                            if (object.getString("code").equals("success")) {
                                                Toast.makeText(LoginActivity.this,object.getString("message"),Toast.LENGTH_LONG).show();
                                            } else if (object.getString("code").equals("failed_unkown")) {
                                                Toast.makeText(LoginActivity.this,object.getString("message"),Toast.LENGTH_LONG).show();
                                            } else if (object.getString("code").equals("failed")) {
                                                Toast.makeText(LoginActivity.this,object.getString("message"),Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(LoginActivity.this,"Unkown error occured ! Try again later !",Toast.LENGTH_LONG).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                                        } finally {
                                            dialog.dismiss();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,volleyError.toString(),Toast.LENGTH_LONG).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put(Const.USER_ACCOUNT_DATA_EMAIL,email);
                                return  params;
                            }
                        };
                        requestQueue.add(rqst);
                    }
                });
                dialog.show();
                break;
        }
    }

    public boolean validate() {
        boolean valid = true;
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();

        if (username.isEmpty() || username.length() < 4 || username.length() > 50) {
            etUsername.setError("enter valid username");
            valid = false;
        } else {
            etUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 50) {
            etPassword.setError("enter valid password");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        return valid;
    }

    private void login() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String data) {
                        try {
                            JSONArray jsonArray = new JSONArray(data);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("login_success")) {
                                appPref.setUsername(username);
                                appPref.setPassword(password);
                                appPref.setLoggedIn(true);
                                appPref.setName(jsonObject.getString("name"));
                                appPref.setEmail(jsonObject.getString("email"));
                                appPref.setProfileImageUrl(jsonObject.getString("image"));
                                appPref.setDescription(jsonObject.getString("description"));
                                appPref.setNumber(jsonObject.getString("phone_number"));
                                appPref.setDOB(jsonObject.getString("dob"));
                                appPref.setGender(jsonObject.getString("gender"));
                                appPref.setRegisteredAt(jsonObject.getString("registered_at"));
                                appPref.setPhoneVisibility(jsonObject.getString("phone_visibility"));
                                appPref.setFirebaseUid(jsonObject.getString("firebaseUid"));
                                ImageLoader.getInstance().loadImage(jsonObject.getString("image"), new SimpleImageLoadingListener() {
                                    @Override
                                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                        Funcs.saveProfileImage(LoginActivity.this,loadedImage);
                                    }
                                });
                                performFirebaseLogin(LoginActivity.this,jsonObject.getString("email"),password);
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Successfully logged in !",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, Home.class));
                                finish();
                            } else if (code.equals("login_failed")){
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,jsonObject.getString("message"),Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(LoginActivity.this,"Something went wrong !",Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        dialog.dismiss();
                        Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<String, String>();
                params.put(Const.USER_ACCOUNT_DATA_USER_NAME,username);
                params.put(Const.USER_ACCOUNT_DATA_PASSWORD,password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void performFirebaseLogin(final Activity activity, final String email, String password) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateFirebaseToken(task.getResult().getUser().getUid(),
                                    new SharedPrefUtil(activity.getApplicationContext()).getString(Const.ARG_FIREBASE_TOKEN, null));
                        }
                    }
                });
    }

    private void updateFirebaseToken(String uid, String token) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Const.ARG_USERS)
                .child(uid)
                .child(Const.ARG_FIREBASE_TOKEN)
                .setValue(token);
    }

}
