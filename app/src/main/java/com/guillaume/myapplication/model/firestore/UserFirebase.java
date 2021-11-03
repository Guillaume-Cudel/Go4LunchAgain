package com.guillaume.myapplication.model.firestore;

import androidx.annotation.Nullable;

public class UserFirebase {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String restaurantChoosed;
    private String restaurantName;
    private String currentRadius;


    public UserFirebase() { }


    public UserFirebase(String uid, String username, @Nullable String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurantName = null;
        this.restaurantChoosed = null;
        this.currentRadius = null;
    }

    public UserFirebase(String uid, String username, @Nullable String urlPicture, String radius){
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurantName = null;
        this.restaurantChoosed = null;
        this.currentRadius = radius;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public String getRestaurantChoosed() { return restaurantChoosed; }
    public String getRestaurantName() { return restaurantName; }

    public String getCurrentRadius() {
        return currentRadius;
    }
}
