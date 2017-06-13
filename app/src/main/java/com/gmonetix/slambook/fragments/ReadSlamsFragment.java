package com.gmonetix.slambook.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.adapter.ReadSlamAdapter;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.model.ReadSlam;
import com.gmonetix.slambook.utils.AppPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadSlamsFragment extends Fragment {

    private AppPref appPref;

    private View view;
    private RecyclerView recyclerView;

    private List<ReadSlam> readSlamList;
    private ReadSlamAdapter adapter;

    public ReadSlamsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_read_slams, container, false);
            appPref = App.getAppPref();

            recyclerView = (RecyclerView) view.findViewById(R.id.read_slams_recyclerView);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            readSlamList = new ArrayList<>();

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.read_slam_model,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            try {
                                JSONArray jsonArray = new JSONArray(s);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    ReadSlam readSlam = new ReadSlam();
                                    readSlam.setName(jsonObject.getString(Const.USER_ACCOUNT_DATA_NAME));
                                    readSlam.setFromUsername(jsonObject.getString("from_user_name"));
                                    readSlam.setImage(jsonObject.getString(Const.USER_ACCOUNT_DATA_IMAGE));
                                    readSlam.setUpdatedOn(jsonObject.getString("updated_on"));
                                    readSlam.setSentOn(jsonObject.getString("sent_on"));
                                    readSlamList.add(readSlam);
                                }
                                adapter = new ReadSlamAdapter(readSlamList, getActivity());
                                recyclerView.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "No slams found !", Toast.LENGTH_LONG).show();
                            }
                        }
                    },

                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(getActivity(), volleyError.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(Const.USER_ACCOUNT_DATA_USER_NAME, appPref.getUsername());
                    return params;
                }
            };
            requestQueue.add(stringRequest);

        }
        return view;
    }

}
