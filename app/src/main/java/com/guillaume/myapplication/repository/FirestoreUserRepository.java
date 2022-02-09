package com.guillaume.myapplication.repository;

import com.guillaume.myapplication.api.UserHelper;
import com.guillaume.myapplication.model.firestore.UserFirebase;

import java.util.List;

public interface FirestoreUserRepository {

    void createUser(String uid, String username, String urlPicture, String radius);

    void createRestaurant(String uid, String placeID, String photoData, String photoWidth, String name,
                          String vicinity, String type, String rating);


    void getUsersList(UserHelper.GetUsersListCallback callback);

    void getUser(String uid, UserHelper.GetUserCallback callback);

    void getRestaurant(String uid, String placeID, UserHelper.GetRestaurantCallback callback);

    void updateRestaurantChoosed(String uid, String restaurantChoosed);

    void updateFieldRestaurantName(String uid, String restaurantName);

    void updateRadius(String uid, String currentRadius);

    void deleteRestaurantChoosed(String uid);

    void deleteRestaurantname(String uid);

    void deleteRestaurant(String uid, String placeID);
}
