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
import com.gmonetix.slambook.adapter.SentSlamAdapter;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.model.SentSlam;
import com.gmonetix.slambook.utils.AppPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentSlamsFragment extends Fragment {

    private AppPref appPref;

    private View view;
    private RecyclerView recyclerView;

    private List<SentSlam> sentSlamList;
    private SentSlamAdapter adapter;

    public SentSlamsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_sent_slams, container, false);
            appPref = App.getAppPref();

            recyclerView = (RecyclerView) view.findViewById(R.id.sent_slams_recyclerView);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            sentSlamList = new ArrayList<>();

            StringRequest rqst = new StringRequest(Request.Method.POST, Const.slams_sent,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String data) {
                            try {
                                JSONArray jsonArray = new JSONArray(data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("code").equals("success")) {
                                        SentSlam sentSlam = new SentSlam();
                                        sentSlam.setUsername(jsonObject.getString("to_user_name"));
                                        sentSlam.setName(jsonObject.getString("name"));
                                        sentSlam.setImage(jsonObject.getString("image"));
                                        sentSlam.setSentOn(jsonObject.getString("sent_on"));
                                        sentSlam.setUpdatedOn(jsonObject.getString("updated_on"));
                                        sentSlamList.add(sentSlam);

                                        adapter = new SentSlamAdapter(sentSlamList, getActivity(), new SentSlamAdapter.OnSlamDeletedListener() {
                                            @Override
                                            public void onSlamDeleted(int pos) {
                                                sentSlamList.remove(pos);
                                                adapter.notifyDataSetChanged();
                                            }
                                        });
                                        recyclerView.setAdapter(adapter);
                                    } else if (jsonObject.getString("code").equals("failed")) {
                                        Toast.makeText(getActivity(), "Error occurred ! Please try again later !", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
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
            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(rqst);

        }
        return view;
    }

}
