package com.gmonetix.slambook.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.gmonetix.slambook.model.User;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.Funcs;
import com.gmonetix.slambook.utils.SharedPrefUtil;
import com.gmonetix.slambook.utils.UtilDialogs;
import com.gmonetix.slambook.utils.UtilsUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private AppPref appPref;

    private EditText etName, etEmail, etUsername, etPassword, etPhone, etDescription;
    private RadioGroup radioGroup;
    private AppCompatRadioButton radioBtnMale, radioBtnFemale;
    private Button register;
    private ImageButton dobIB;
    private TextView dobTV, login, privacyPolicy;
    private ImageView imageView;

    private Bitmap bitmap;
    private String name, email, username, password, phone, gender, description,dob,registered_at;
    private int year_x,month_x,day_x;
    private static final int DATE_PICKER_DIALOG_ID = 0;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String INTENT_USERNAME = "intent_username";
    private static final String INTENT_PASSWORD = "intent_password";

    ProgressDialog loading;
    private String firebaseUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_registration);
        this.appPref = App.getAppPref();
        init();

        Calendar calendar = Calendar.getInstance();
        year_x = calendar.get(Calendar.YEAR)-10;
        month_x = calendar.get(Calendar.MONTH);
        day_x = calendar.get(Calendar.DAY_OF_MONTH);
        radioBtnMale.setChecked(true);
        gender = "MALE";
        registered_at = DateFormat.getDateInstance().format(new Date());

        register.setOnClickListener(this);
        login.setOnClickListener(this);
        dobTV.setOnClickListener(this);
        dobIB.setOnClickListener(this);
        privacyPolicy.setOnClickListener(this);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.maleRadioBtn:
                        gender = "MALE";
                        return;
                    case R.id.femaleRadioBtn:
                        gender = "FEMALE";
                        break;
                    default:
                        gender = "MALE";
                        break;
                }
            }
        });


    }

    private void init() {
        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPhone = (EditText) findViewById(R.id.et_phone_number);
        etDescription = (EditText) findViewById(R.id.et_description);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group_container_dob);
        radioBtnFemale = (AppCompatRadioButton) findViewById(R.id.femaleRadioBtn);
        radioBtnMale = (AppCompatRadioButton) findViewById(R.id.maleRadioBtn);
        register = (Button) findViewById(R.id.btn_register);
        dobIB = (ImageButton) findViewById(R.id.ib_dob);
        dobTV = (TextView) findViewById(R.id.tv_dob);
        login = (TextView)  findViewById(R.id.login_here_tv);
        privacyPolicy = (TextView) findViewById(R.id.privacy_policy_register_screen);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (validate())
                    showDialog();
                break;

            case R.id.login_here_tv:
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
                break;

            case R.id.ib_dob:
                showDialog(DATE_PICKER_DIALOG_ID);
                break;

            case R.id.tv_dob:
                showDialog(DATE_PICKER_DIALOG_ID);
                break;

            case R.id.privacy_policy_register_screen:
                MaterialDialog dialog = UtilDialogs.showString(RegistrationActivity.this,"Privacy Policy","You consent to the use of your personal information by Gmonetix.com and/or its third party providers and distributors in accordance with the terms of and for the purposes set forth in our Privacy Policy. Gmonetix does not rent, sell, or share personal information about you with other people or non affiliated companies except to provide products or services you've requested, when we have your permission, or under the following circumstances:\n" +
                        "\n" +
                        "Gmonetix.com provides the information to trusted partners who work on behalf of or with Gmonetix under extremely strict confidentiality agreements. These companies may use your personal information to help Gmonetix communicate with you about offers from Gmonetix and our marketing partners. However, these companies do not have any independent right to share this information.\n" +
                        "\n" +
                        "You may choose to give us personal information, such as your name, contact number, address or e-mail id that may be needed, for example, to correspond with you, to participate on the site, forum, contests, use of softwares, articles, white papers or to provide you with a subscription. If you tell us that you do not want us to use this information as a basis for further contact with you, we will respect your wishes. We intend to protect the quality and integrity of your personally identifiable information.\n" +
                        "\n" +
                        "Gmonetix may share your information with its business partners who sponsor an event, white paper downloads, contest, or promotion; or who jointly offer a service or feature with Gmonetix.com.\n" +
                        "\n" +
                        "We will make a sincere effort to respond in a timely manner to your requests to correct inaccuracies in your personal information. To correct inaccuracies in your personal information please return the message containing the inaccuracies to the sender email id with details of the correction requested.\n" +
                        "\n" +
                        "We sometimes collect anonymous information from visits to our sites to help us provide better customer service. For example, we keep track of the domains from which people visit and we also measure visitor activity on Gmonetix web site, but we do so in ways that keep the information anonymous. Gmonetix or its affiliates or vendors may use this data to analyze trends and statistics and to help us provide better customer service. We maintain the highest levels of confidentiality for this information; our affiliates and vendors follow the same high levels of confidentiality. This anonymous information is used and analyzed only at an aggregate level to help us understand trends and patterns. None of this information is reviewed at an individual level. If you do not want your transaction details used in this manner, you can either disable your cookies or opt-out by sending an email to editor at Gmonetix.com.\n" +
                        "\n" +
                        "Gmonetix may, if you so choose, send direct mailers to you at the address given by you. You have the option to 'opt-out' of this direct mailer by way of links provided at the bottom of each mailer. We respect your privacy and in the event that you choose to not receive such mailers, we will take all steps to remove you from the list.\n" +
                        "\n" +
                        "We transfer information about you if Gmonetix is acquired by or merged with another company.\n" +
                        "\n" +
                        "We believe it is ",R.drawable.ic_about_info_black);
                dialog.show();
                break;
        }
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
            dobTV.setText("Date of birth : " + String.valueOf(day_x) + "/" + String.valueOf(month_x) + "/" + String.valueOf(year_x));
        }
    };

    private void showDialog() {
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Upload Profile Image")
                .icon(getResources().getDrawable(R.drawable.ic_face_profile))
                .typeface("RobotoLight.ttf","RobotoLight.ttf")
                .customView(R.layout.image_upload_dialog,false)
                .positiveText("PROCEED")
                .canceledOnTouchOutside(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        loading = ProgressDialog.show(RegistrationActivity.this,"Registering...","Please wait...",false,false);
                        registerFirebase();
                    }
                })
                .build();
        imageView = (ImageView) dialog.findViewById(R.id.iv_profile_image);
        imageView.setBackgroundResource(R.drawable.user_default_image_black);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UtilsUI.showFileChooser(RegistrationActivity.this,PICK_IMAGE_REQUEST);
            }
        });
        dialog.show();
    }

    private void registerFirebase() {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.e("TAG", "performFirebaseRegistration:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this,"registration failed",Toast.LENGTH_LONG).show();
                        } else {
                            addUserToDatabase(RegistrationActivity.this,task.getResult().getUser());
                            Toast.makeText(RegistrationActivity.this,"chat success registraution",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void registerWithImage() {

        String image = "null";
        if (bitmap != null)
        {
            image = Funcs.getStringImage(bitmap);
        }

        final String finalImage = image;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.register_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.getString("code").equals("success")) {
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                intent.putExtra(INTENT_USERNAME,username);
                                intent.putExtra(INTENT_PASSWORD,password);
                                Funcs.saveProfileImage(RegistrationActivity.this,bitmap);
                                startActivity(intent);
                                finish();
                            } else if(jsonObject.getString("code").equals("registraion_unsuccessfull")) {
                                Toast.makeText(RegistrationActivity.this,jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else Toast.makeText(RegistrationActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RegistrationActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        loading.dismiss();
                        volleyError.printStackTrace();
                        Toast.makeText(RegistrationActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new Hashtable<String, String>();

                params.put(Const.USER_ACCOUNT_DATA_IMAGE, finalImage);
                params.put(Const.USER_ACCOUNT_DATA_NAME, name);
                params.put(Const.USER_ACCOUNT_DATA_USER_NAME,username);
                params.put(Const.USER_ACCOUNT_DATA_EMAIL,email);
                params.put(Const.USER_ACCOUNT_DATA_PASSWORD,password);
                params.put(Const.USER_ACCOUNT_DATA_DOB,dob);
                params.put(Const.USER_ACCOUNT_DATA_DESCRIPTION,description);
                params.put(Const.USER_ACCOUNT_DATA_PHONE_NUMBER,phone);
                params.put(Const.USER_ACCOUNT_DATA_REGISTERED_AT,registered_at);
                params.put(Const.USER_ACCOUNT_DATA_GENDER,gender);
                params.put("firebaseUid",firebaseUid);
                Log.e("TAG",params.toString());
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void addUserToDatabase(final Context context, final FirebaseUser firebaseUser) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        User user = new User(firebaseUser.getUid(),
                firebaseUser.getEmail(),
                new SharedPrefUtil(context).getString(Const.ARG_FIREBASE_TOKEN));
        database.child(Const.ARG_USERS)
                .child(firebaseUser.getUid())
                .setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this,"adding to database Success",Toast.LENGTH_LONG).show();
                            firebaseUid = firebaseUser.getUid();
                            registerWithImage();
                        } else {
                            Toast.makeText(RegistrationActivity.this,"adding to database failed",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image_black));
            }
        }
    }

    public boolean validate() {
        boolean valid = true;

        name = etName.getText().toString().trim();
        email = etEmail.getText().toString().trim();
        username = etUsername.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        dob = String.valueOf(day_x) + "/" + String.valueOf(month_x) + "/" + String.valueOf(year_x);
        description = etDescription.getText().toString().trim();
        phone = etPhone.getText().toString().trim();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("enter valid email");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (name.isEmpty() || name.length() < 4 || name.length()>50) {
            etName.setError("enter valid name");
            valid = false;
        } else {
            etName.setError(null);
        }

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

        if (phone.isEmpty() || phone.length() < 8 || phone.length() > 15) {
            etPhone.setError("enter valid phone number");
            valid = false;
        } else {
            etPhone.setError(null);
        }

        return valid;
    }

}
