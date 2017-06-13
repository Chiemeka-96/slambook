package com.gmonetix.slambook.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gmonetix.slambook.R;
import com.gmonetix.slambook.core.chat.ChatContract;
import com.gmonetix.slambook.core.chat.ChatPresenter;
import com.gmonetix.slambook.events.PushNotificationEvent;
import com.gmonetix.slambook.model.Chat;
import com.gmonetix.slambook.adapter.ChatRecyclerAdapter;
import com.gmonetix.slambook.helper.Const;
import com.google.firebase.auth.FirebaseAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements ChatContract.View{
    private RecyclerView mRecyclerViewChat;
    private EditText mETxtMessage;
    private FloatingActionButton sendBtn;

    private ProgressDialog mProgressDialog;

    private ChatRecyclerAdapter mChatRecyclerAdapter;

    private ChatPresenter mChatPresenter;

    public static ChatFragment newInstance(String receiver, String receiverUid, String firebaseToken) {
        Bundle args = new Bundle();
        args.putString(Const.ARG_RECEIVER, receiver);
        args.putString(Const.ARG_RECEIVER_UID, receiverUid);
        args.putString(Const.ARG_FIREBASE_TOKEN, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(fragmentView);
        return fragmentView;
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mETxtMessage = (EditText) view.findViewById(R.id.edit_text_message);
        sendBtn = (FloatingActionButton) view.findViewById(R.id.send_message_btn);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mETxtMessage.getText().toString().trim();
                if (!message.equals("")) {
                    sendMessage(message + " " + getEmojiByUnicode(0x1F60A));
                }
            }
        });

        mChatPresenter = new ChatPresenter(this);
        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                getArguments().getString(Const.ARG_RECEIVER_UID));
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    private void sendMessage(String message) {
        String receiver = getArguments().getString(Const.ARG_RECEIVER);
        String receiverUid = getArguments().getString(Const.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Const.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender,
                receiver,
                senderUid,
                receiverUid,
                message,
                System.currentTimeMillis());
        mChatPresenter.sendMessage(getActivity().getApplicationContext(),
                chat,
                receiverFirebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
        mETxtMessage.setText("");
        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatRecyclerAdapter == null) {
            mChatRecyclerAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatRecyclerAdapter);
        }
        mChatRecyclerAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatRecyclerAdapter.getItemCount() - 1);
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void onPushNotificationEvent(PushNotificationEvent pushNotificationEvent) {
        if (mChatRecyclerAdapter == null || mChatRecyclerAdapter.getItemCount() == 0) {
            mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    pushNotificationEvent.getUid());
        }
    }
}
