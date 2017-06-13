package com.gmonetix.slambook.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.widget.EditText;
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
import com.gmonetix.slambook.activity.Home;
import com.gmonetix.slambook.activity.LoginActivity;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.UtilDialogs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kizitonwose.colorpreference.ColorPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

public  class SettingsFragment extends PreferenceFragment {

    private AppPref appPref;

    private ColorPreference prefPrimaryColor;
    private Preference changePassword, deleteAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.appPref = App.getAppPref();

        prefPrimaryColor = (ColorPreference) findPreference("colorPrefPrimary");
        deleteAccount = (Preference) findPreference("DeleteAccount");
        changePassword = (Preference) findPreference("ChangePassword");

        deleteAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialDialog dialog = UtilDialogs.showString(getActivity(),"WARNING","Are you sure you want to delete your account?" +
                        "\nYour friends won't be able to find you or send any slams ",R.drawable.ic_about_info_black);
                dialog.getBuilder().negativeText("NO");
                dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteAccountFromServer();
                    }
                });
                dialog.show();
                return true;
            }
        });

        changePassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                MaterialDialog dialog = UtilDialogs.editTextDialog(getActivity(),"Change Password","CHANGE",R.drawable.ic_forgot_password_black);
                final EditText editText = (EditText) dialog.findViewById(R.id.et_dialog);
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final String newPass = editText.getText().toString().trim();
                        changeFirebasePassword(newPass);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.change_password,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        JSONArray jsonArray = null;
                                        try {
                                            jsonArray = new JSONArray(s);
                                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                                            String code = jsonObject.getString("code");
                                            if (code.equals("success")) {
                                                appPref.setPassword(newPass);
                                            } else {
                                                Toast.makeText(getActivity(),"Error Occurred! Try again Later !", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                                Toast.makeText(getActivity(),"Error Occurred! Try again Later !", Toast.LENGTH_SHORT).show();
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String,String> params = new Hashtable<String, String>();

                                params.put(Const.USER_ACCOUNT_DATA_USER_NAME,appPref.getUsername());
                                params.put(Const.USER_ACCOUNT_DATA_PASSWORD,appPref.getPassword());
                                params.put("new_password",newPass);
                                return params;
                            };
                        };
                        RequestQueue queue = Volley.newRequestQueue(getActivity());
                        queue.add(stringRequest);
                    }
                });
                dialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                return true;
            }
        });

    }

    private void deleteAccountFromServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.delete_account,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String code = jsonObject.getString("code");
                            if (code.equals("success")) {
                                Toast.makeText(getActivity(),"Account deleted successfully !", Toast.LENGTH_LONG).show();
                                deleteFirebaseAccount();
                                PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().apply();
                                Intent i = new Intent(getActivity(),LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                            } else {
                                Toast.makeText(getActivity(),"Error Occured! Try again Later !", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Toast.makeText(getActivity(), "Some error occurred ! Try again later", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new Hashtable<String, String>();
                params.put(Const.USER_ACCOUNT_DATA_USER_NAME, appPref.getUsername());
                params.put(Const.USER_ACCOUNT_DATA_PASSWORD, appPref.getPassword());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void deleteFirebaseAccount() {
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
                        user.delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //success
                                        }
                                    }
                                });

                    }
                });
    }

    private void changeFirebasePassword(final String newPass) {
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
                        user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(),"Password changed successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    public void performFirebaseLogout() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "successfully logged out from chat", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "failed logging out from chat", Toast.LENGTH_SHORT).show();
        }
    }

}
