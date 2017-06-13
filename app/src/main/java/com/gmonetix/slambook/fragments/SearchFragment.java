package com.gmonetix.slambook.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.gmonetix.slambook.adapter.SearchAdapter;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.model.Friend;
import com.gmonetix.slambook.utils.AppPref;
import com.gmonetix.slambook.utils.Funcs;
import com.gmonetix.slambook.utils.UtilsUI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment {

    private View view;
    private EditText etBox;
    private ImageView btnSearch;
    private RecyclerView recyclerView;

    private AppPref appPref;
    private List<Friend> searchedFriends;
    private SearchAdapter adapter;
    private String name;

    public SearchFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
            appPref = App.getAppPref();
            init();

            searchedFriends = new ArrayList<>();
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            btnSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = etBox.getText().toString().trim();
                    if (searchedFriends.size() > 0) {
                        searchedFriends.clear();
                        adapter.notifyDataSetChanged();
                    }
                    if (name.equals("") || name == null) {
                        Toast.makeText(getActivity(), "Enter name to search a friend", Toast.LENGTH_SHORT).show();
                    } else {
                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, Const.url_search,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        try {
                                            JSONArray jsonArray = new JSONArray(s);
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                Friend friend = new Friend();
                                                friend.setName(jsonObject.getString(Const.USER_ACCOUNT_DATA_NAME));
                                                friend.setEmail(jsonObject.getString(Const.USER_ACCOUNT_DATA_EMAIL));
                                                friend.setImage(jsonObject.getString(Const.USER_ACCOUNT_DATA_IMAGE));
                                                friend.setDescription(jsonObject.getString(Const.USER_ACCOUNT_DATA_DESCRIPTION));
                                                friend.setUsername(jsonObject.getString(Const.USER_ACCOUNT_DATA_USER_NAME));
                                                friend.setDob(jsonObject.getString(Const.USER_ACCOUNT_DATA_DOB));
                                                friend.setRegisteredAt(jsonObject.getString("registered_at"));
                                                friend.setNumber(jsonObject.getString(Const.USER_ACCOUNT_DATA_PHONE_NUMBER));
                                                friend.setPhoneVisibility(jsonObject.getString("phone_visibility"));
                                                searchedFriends.add(friend);

                                                adapter = new SearchAdapter(searchedFriends, getActivity());
                                                recyclerView.setAdapter(adapter);

                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
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
                                params.put(Const.USER_ACCOUNT_DATA_NAME, name);
                                return params;
                            }
                        };
                        requestQueue.add(stringRequest);
                    }
                }
            });

        }
        return view;
    }

    private void init() {
        UtilsUI.getUilInstance(getActivity());
        etBox = (EditText) view.findViewById(R.id.etSearch);
        btnSearch = (ImageView) view.findViewById(R.id.btn_search);
        recyclerView = (RecyclerView) view.findViewById(R.id.search_recyclerView);
    }

}
