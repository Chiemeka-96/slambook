package com.gmonetix.slambook.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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
import com.gmonetix.slambook.activity.Home;
import com.gmonetix.slambook.adapter.FriendAdapter;
import com.gmonetix.slambook.adapter.FriendRequestAdapter;
import com.gmonetix.slambook.helper.Const;
import com.gmonetix.slambook.model.Friend;
import com.gmonetix.slambook.utils.AppPref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private AppPref appPref;
    private Context context;

    private TabLayout tabLayout;
    private RecyclerView recyclerView;
    private View view;

    private List<Friend> friendList = Home.friendList;
    private List<Friend> requestList = Home.requestList;

    private FriendRequestAdapter friendRequestAdapter;
    private FriendAdapter friendAdapter;

    public FriendsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_friends, container, false);
            appPref = App.getAppPref();
            context = getActivity();

            tabLayout = (TabLayout) view.findViewById(R.id.friends_tab_layout);
            recyclerView = (RecyclerView) view.findViewById(R.id.friends_recycler_view);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);

            tabLayout.addTab(tabLayout.newTab().setText("FRIENDS").setIcon(getResources().getDrawable(R.drawable.ic_friend_white)));
            tabLayout.addTab(tabLayout.newTab().setText("REQUESTS").setIcon(getResources().getDrawable(R.drawable.ic_friend_request_white)));

            friendAdapter = new FriendAdapter(friendList, getActivity());
            friendRequestAdapter = new FriendRequestAdapter(requestList, getActivity(), new FriendRequestAdapter.OnRequestAcceptedListeneer() {
                @Override
                public void onRequestAccepted(int pos) {
                    friendList.add(requestList.get(pos));
                    friendAdapter.notifyDataSetChanged();
                    requestList.remove(pos);
                    friendRequestAdapter.notifyDataSetChanged();
                }
            });
            recyclerView.setAdapter(friendAdapter);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        recyclerView.setAdapter(friendAdapter);
                    } else {
                        recyclerView.setAdapter(friendRequestAdapter);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

        }

        return view;
    }

}
