package com.guillaume.myapplication.repository;

import com.guillaume.myapplication.api.RestaurantHelper;
import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.model.requests.Geometry;
import com.guillaume.myapplication.model.requests.OpeningHours;

import java.util.List;

public interface FirestoreRestaurantRepository {

    void createRestaurant(String placeID, String photoData, String photoWidth, String name,
                          String vicinity, String type, String rating, Geometry geometry, Details detail, OpeningHours openingHours);

    void createUserToRestaurant(String placeID, String uid, String username, String urlPicture);

    void createUserRestaurantLiked(String placeID, String uid, String username, String urlPicture);

    void getParticipantsList(String placeID, RestaurantHelper.GetAllUsersCallback callback);


    void getAllRestaurants(RestaurantHelper.GetAllRestaurantssCallback callback);


    void getUser(String placeID, String uid, RestaurantHelper.GetUserTargetedCallback callback);


    void getTargetedRestaurant(String placeId, RestaurantHelper.GetRestaurantsTargetedCallback callback);


    void getUserRestaurantLiked(String placeID, String uid, RestaurantHelper.GetUserRestaurantLikedCallback callback);

    void  updateParticipantNumber(String placeID, boolean addParticipant);

    void deleteParticipant(String placeID, String uid);

    void deleteUserLiked(String placeID, String uid);

}
