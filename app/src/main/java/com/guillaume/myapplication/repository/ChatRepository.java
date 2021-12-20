package com.guillaume.myapplication.repository;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.guillaume.myapplication.manager.UserManager;
import com.guillaume.myapplication.model.firestore.MessageFirebase;

public final class ChatRepository {
    private static final String CHAT_COLLECTION = "chat";
    private static final String MESSAGE_COLLECTION = "messages";
    private static volatile ChatRepository instance;

    private UserManager userManager;

    private ChatRepository(){
        this.userManager = UserManager.getInstance();
    }

    public static ChatRepository getInstance() {
        ChatRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatRepository.class) {
            if (instance == null) {
                instance = new ChatRepository();
            }
            return instance;
        }
    }

    public CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    public Query getAllMessage(String chat){
        return this.getChatCollection()
                .document(chat)
                .collection(MESSAGE_COLLECTION)
                .orderBy("dateCreated")
                .limit(50);
    }
    /*public Query getAllMessage(){
        return this.getChatCollection()
                .orderBy("dateCreated")
                .limit(50);
    }*/

    public void createMessageForChat(String textMessage, String chat){
        userManager.getUserData().addOnSuccessListener(user -> {
            // Create the Message object
            MessageFirebase message = new MessageFirebase(textMessage, user);

            // Store Message to Firestore
            this.getChatCollection()
                    .document(chat)
                    .collection(MESSAGE_COLLECTION)
                    .add(message);
        });
    }
    /*public void createMessageForChat(String textMessage){
        //todo check the problem
        userManager.getUserData().addOnSuccessListener(user -> {
            // Create the Message object
            MessageFirebase message = new MessageFirebase(textMessage, user);

            // Store Message to Firestore
            this.getChatCollection()
                    //.document(chat)
                    //.collection(MESSAGE_COLLECTION)
                    .add(message);
        });
    }*/
}
