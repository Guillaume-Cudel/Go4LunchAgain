package com.guillaume.myapplication.ui.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.guillaume.myapplication.R;
import com.guillaume.myapplication.model.firestore.MessageFirebase;
import com.guillaume.myapplication.manager.ChatManager;
import com.guillaume.myapplication.ui.BaseActivity;

public class ChatActivity extends BaseActivity implements ChatAdapter.Listener {

    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private TextView emptyRecyclerView;
    private Button sendButton;
    private EditText chatEditText;

    private static final String CHAT_NAME = "Workmates chat";

    //private UserManager userManager = UserManager.getInstance();
    private ChatManager chatManager = ChatManager.getInstance();

    /*@Override
    protected ActivityChatBinding getViewBinding() {
        return ActivityMentorChatBinding.inflate(getLayoutInflater());
    }*/

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_chat;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chat);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF9C1A")));
            actionBar.setDisplayHomeAsUpEnabled(true);}

        chatRecyclerView = findViewById(R.id.activity_chat_recycler_view);
        emptyRecyclerView = findViewById(R.id.activity_chat_text_view_recycler_view_empty);
        sendButton = findViewById(R.id.activity_chat_send_button);
        chatEditText = findViewById(R.id.activity_chat_message_edit_text);

        configureRecyclerView();
        setupListeners();
    }



    private void setupListeners(){

        // Chat buttons
        /*binding.androidChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_ANDROID); });
        binding.firebaseChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_FIREBASE); });
        binding.bugChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_BUG); });*/

        sendButton.setOnClickListener(view -> {sendMessage();});

    }

    // Configure RecyclerView
    private void configureRecyclerView(){
        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(
                generateOptionsForAdapter(chatManager.getAllMessageForChat(ChatActivity.CHAT_NAME)),
                Glide.with(this), this);

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                //binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
                chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount());
            }
        });

        //binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //binding.chatRecyclerView.setAdapter(this.chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(this.chatAdapter);
    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<MessageFirebase> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<MessageFirebase>()
                .setQuery(query, MessageFirebase.class)
                .setLifecycleOwner(this)
                .build();
    }

    public void onDataChanged() {
        // Show TextView in case RecyclerView is empty
        //binding.emptyRecyclerView.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        emptyRecyclerView.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    private void sendMessage(){
        // Check if user can send a message (Text not null + user logged)
        //boolean canSendMessage = !TextUtils.isEmpty(binding.chatEditText.getText()) && userManager.isCurrentUserLogged();
        boolean canSendMessage = !TextUtils.isEmpty(chatEditText.getText());


        if (canSendMessage){
            // Create a new message for the chat
            String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            //chatManager.createMessageForChat(binding.chatEditText.getText().toString(), this.currentChatName);
            chatManager.createMessageForChat(chatEditText.getText().toString(), ChatActivity.CHAT_NAME);

            // Reset text field
            //binding.chatEditText.setText("");
            chatEditText.setText("");
        }
    }
}