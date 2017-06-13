package com.gmonetix.slambook.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.gmonetix.slambook.helper.Validator;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.Funcs;
import com.gmonetix.slambook.utils.UtilDialogs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View view;
    private AppPref appPref;
    private Drawable drawable;

    private TextView tvName, tvEMail, tvPhone, tvDob, tvDesc, tvRegisteredAt;
    private Switch numberVisibilityToggleBtn;
    private ImageView profileImage, editName, editEmail, editPhone, editDob, editDesc;
    private FloatingActionButton fabEditPic;

    private int numberVisibilityState = 0;
    private String imageString;
    private final static int IMAGE_CHOOSER_CODE = 100;
    private Uri filePath = null;

    public ProfileFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_profile, container, false);
            appPref = App.getAppPref();
            init();

            numberVisibilityToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateNumberVisibility(isChecked);
                }
            });

            if (Funcs.getImageDrawable(getActivity()) != null) {
                drawable = Funcs.getImageDrawable(getActivity());
                profileImage.setImageDrawable(drawable);
            } else {
                profileImage.setImageDrawable(getResources().getDrawable(R.drawable.user_default_image));
            }

        }
        return view;
    }

    private void init() {
        tvName = (TextView) view.findViewById(R.id.profile_activity_name);
        tvEMail = (TextView) view.findViewById(R.id.profile_activity_email);
        tvPhone = (TextView) view.findViewById(R.id.profile_activity_phone);
        tvDob = (TextView) view.findViewById(R.id.profile_activity_dob);
        tvDesc = (TextView) view.findViewById(R.id.profile_activity_desc);
        tvRegisteredAt = (TextView) view.findViewById(R.id.profile_activity_registered_at);

        tvName.setText(appPref.getName());
        tvEMail.setText(appPref.getEmail());
        tvPhone.setText(appPref.getNumber());
        tvDob.setText(appPref.getDOB());
        tvDesc.setText(appPref.getDescription());
        tvRegisteredAt.setText(appPref.getRegisteredAt());

        numberVisibilityToggleBtn = (Switch) view.findViewById(R.id.profile_activity_number_visibility_toggle);
        if (appPref.getPhoneVisibility().equals("1")) {
            numberVisibilityToggleBtn.setChecked(true);
        } else if (appPref.getPhoneVisibility().equals("0")){
            numberVisibilityToggleBtn.setChecked(false);
        }

        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        editName = (ImageView) view.findViewById(R.id.profile_activity_edit_name);
        editEmail = (ImageView) view.findViewById(R.id.profile_activity_edit_email);
        editPhone = (ImageView) view.findViewById(R.id.profile_activity_edit_phone);
        editDob = (ImageView) view.findViewById(R.id.profile_activity_edit_dob);
        editDesc = (ImageView) view.findViewById(R.id.profile_activity_edit_desc);
        fabEditPic = (FloatingActionButton) view.findViewById(R.id.profile_activity_change_pic);

        profileImage.setOnClickListener(this);
        editName.setOnClickListener(this);
        editEmail.setOnClickListener(this);
        editPhone.setOnClickListener(this);
        editDob.setOnClickListener(this);
        editDesc.setOnClickListener(this);
        fabEditPic.setOnClickListener(this);

    }

    private void updateNumberVisibility(final boolean value) {
        if (value) {
            numberVisibilityState = 1;
        } else {
            numberVisibilityState = 0;
        }

        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Updating...","Please wait...",false,false);

        StringRequest rqst = new StringRequest(Request.Method.POST, Const.number_visibility,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject object = array.getJSONObject(0);
                            if (object.getString("code").equals("success")) {
                                Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                if (value) {
                                    numberVisibilityToggleBtn.setChecked(true);
                                    appPref.setPhoneVisibility("1");
                                } else {
                                    numberVisibilityToggleBtn.setChecked(false);
                                    appPref.setPhoneVisibility("0");
                                }
                            } else {
                                Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loading.dismiss();
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                params.put("phone_visibility",String.valueOf(numberVisibilityState));
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(rqst);

    }

    private void uploadProfilePicInServer(Bitmap bitmap) {

        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Updating...","Please wait...",false,false);

        imageString = "";
        if (bitmap != null)
        {
            imageString = Funcs.getStringImage(bitmap);
        } else {
//            if (gender.equals("MALE")) {
//                bitmap  = BitmapFactory.decodeResource(getResources(),R.drawable.male);
//                image = getStringImage(bitmap);
//            } else {
//                bitmap  = BitmapFactory.decodeResource(getResources(),R.drawable.female);
//                image = getStringImage(bitmap);
//            }
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.update_image,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        try {
                            JSONArray array = new JSONArray(response);
                            JSONObject object = array.getJSONObject(0);
                            if (object.getString("code").equals("success")) {
                                Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                error.printStackTrace();
                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("image",imageString);
                params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.profileImage:
                MaterialDialog dialog = UtilDialogs.imageViewDialog(getActivity(),"SELF",R.drawable.user_outline);
                ImageView imageView = (ImageView) dialog.findViewById(R.id.imageViewDialog);
                imageView.setImageDrawable(Funcs.getImageDrawable(getActivity()));
                dialog.show();
                break;

            case R.id.profile_activity_edit_name:
                MaterialDialog dialog1 = UtilDialogs.editTextDialog(getActivity(),"Update Name", "UPDATE", R.drawable.user_outline);
                final EditText editText = (EditText) dialog1.findViewById(R.id.et_dialog);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                dialog1.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final String newName = editText.getText().toString().trim();
                        if (Validator.validateName(editText)) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.update_name,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            dialog.dismiss();
                                            try {
                                                JSONArray array = new JSONArray(response);
                                                JSONObject object = array.getJSONObject(0);
                                                if (object.getString("code").equals("success")) {
                                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                                    tvName.setText(newName);
                                                    appPref.setName(newName);
                                                } else {
                                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dialog.dismiss();
                                    error.printStackTrace();
                                    Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("name",newName);
                                    params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                                    return  params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                            requestQueue.add(stringRequest);
                        }
                    }
                });
                dialog1.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
                dialog1.show();
                break;

            case R.id.profile_activity_edit_email:
                final MaterialDialog dialog2 = UtilDialogs.editTextDialog(getActivity(),"Update Email", "UPDATE", R.drawable.email_outline);
                final EditText editText1 = (EditText) dialog2.findViewById(R.id.et_dialog);
                editText1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                dialog2.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final String newEmail = editText1.getText().toString().trim();
                        if (Validator.validateEmail(editText1)) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.update_email,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            dialog.dismiss();
                                            try {
                                                JSONArray array = new JSONArray(response);
                                                JSONObject object = array.getJSONObject(0);
                                                if (object.getString("code").equals("success")) {
                                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                                    updateFirebaseEmail(newEmail);
                                                } else {
                                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dialog.dismiss();
                                    error.printStackTrace();
                                    Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put("email",newEmail);
                                    params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                                    return  params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                            requestQueue.add(stringRequest);
                        }
                    }
                });
                dialog2.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
                dialog2.show();
                break;

            case R.id.profile_activity_edit_phone:
                final MaterialDialog dialog3 = UtilDialogs.editTextDialog(getActivity(),"Update Phone", "UPDATE", R.drawable.phone_outline);
                final EditText editText2 = (EditText) dialog3.findViewById(R.id.et_dialog);
                editText2.setInputType(InputType.TYPE_CLASS_PHONE);
                dialog3.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final String newPhone = editText2.getText().toString().trim();
                        if (Validator.validatePhone(editText2)) {
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.update_phone,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            dialog.dismiss();
                                            try {
                                                JSONArray array = new JSONArray(response);
                                                JSONObject object = array.getJSONObject(0);
                                                if (object.getString("code").equals("success")) {
                                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                                    tvPhone.setText(newPhone);
                                                    appPref.setNumber(newPhone);
                                                } else {
                                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    dialog.dismiss();
                                    error.printStackTrace();
                                    Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                                }
                            }){
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String,String> params = new HashMap<String, String>();
                                    params.put(Const.USER_ACCOUNT_DATA_PHONE_NUMBER,newPhone);
                                    params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                                    return  params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                            requestQueue.add(stringRequest);
                        }
                    }
                });
                dialog3.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
                dialog3.show();
                break;

            case R.id.profile_activity_edit_dob:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getFragmentManager(),"datePicker");
                break;

            case R.id.profile_activity_edit_desc:
                final MaterialDialog dialog4 = UtilDialogs.editTextDialog(getActivity(),"Update Bio", "UPDATE", R.drawable.ic_book_open_black);
                final EditText editText3 = (EditText) dialog4.findViewById(R.id.et_dialog);
                editText3.setInputType(InputType.TYPE_CLASS_TEXT);
                dialog4.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final String newDesc = editText3.getText().toString().trim();
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.update_description,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        dialog.dismiss();
                                        try {
                                            JSONArray array = new JSONArray(response);
                                            JSONObject object = array.getJSONObject(0);
                                            if (object.getString("code").equals("success")) {
                                                Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                                tvDesc.setText(newDesc);
                                                appPref.setDescription(newDesc);
                                            } else {
                                                Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                dialog.dismiss();
                                error.printStackTrace();
                                Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String,String> params = new HashMap<String, String>();
                                params.put(Const.USER_ACCOUNT_DATA_DESCRIPTION,newDesc);
                                params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                                return  params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                        requestQueue.add(stringRequest);
                    }
                });
                dialog4.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
                dialog4.show();
                break;

            case R.id.profile_activity_change_pic:
                showImageChooser(IMAGE_CHOOSER_CODE);
                break;

        }

    }

    public void updateDate(final String date) {
        if (Validator.validateDOB(date)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.update_dob,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONArray array = new JSONArray(response);
                                JSONObject object = array.getJSONObject(0);
                                if (object.getString("code").equals("success")) {
                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                    tvDob.setText(date);
                                    appPref.setDOB(date);
                                } else {
                                    Toast.makeText(getActivity(),object.getString("message"),Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(),e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put(Const.USER_ACCOUNT_DATA_DOB,date);
                    params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                    return  params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(getActivity(),"Enter correct date of birth",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CHOOSER_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),filePath);
                uploadProfilePicInServer(bitmap);
                Funcs.saveProfileImage(getActivity(),bitmap);
                profileImage.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showImageChooser(int REQUEST_CODE) {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
    }

    public void updateFirebaseEmail(final String newEmail) {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(appPref.getEmail(), appPref.getPassword());

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    tvEMail.setText(newEmail);
                                    appPref.setEmail(newEmail);
                                    Toast.makeText(getActivity(),"Success change of email",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

}
