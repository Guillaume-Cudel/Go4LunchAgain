package com.guillaume.myapplication.model.firestore;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class MessageFirebase {

    private String message;
    private Date dateCreated;
    private UserFirebase userSender;
    private String urlImage;

    public MessageFirebase() { }

    public MessageFirebase(String message, UserFirebase userSender) {
        this.message = message;
        this.userSender = userSender;
    }

    public MessageFirebase(String message, String urlImage, UserFirebase userSender) {
        this.message = message;
        this.urlImage = urlImage;
        this.userSender = userSender;
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public UserFirebase getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(UserFirebase userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}
