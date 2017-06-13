package com.gmonetix.slambook.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmonetix.slambook.App;
import com.gmonetix.slambook.R;
import com.gmonetix.slambook.activity.Home;
import com.gmonetix.slambook.adapter.ChatListAdapter;
import com.gmonetix.slambook.model.Friend;
import com.gmonetix.slambook.utils.AppPref;

import java.util.List;

public class ChatsFragment extends Fragment {

    private AppPref appPref;
    private List<Friend> chatFriendList = Home.friendList;
    private ChatListAdapter adapter;

    private View view;
    private RecyclerView recyclerView;

    public ChatsFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chats, container, false);
        appPref = App.getAppPref();

        // intiliaze chatfriendlist TODO

        recyclerView = (RecyclerView) view.findViewById(R.id.chats_recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ChatListAdapter(chatFriendList,getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

}
