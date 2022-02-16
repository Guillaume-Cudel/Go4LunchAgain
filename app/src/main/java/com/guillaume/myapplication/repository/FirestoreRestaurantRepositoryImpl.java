package com.guillaume.myapplication.repository;

import com.guillaume.myapplication.api.RestaurantHelper;
import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.model.requests.Geometry;
import com.guillaume.myapplication.model.requests.GetUsersResponse;
import com.guillaume.myapplication.model.requests.OpeningHours;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirestoreRestaurantRepositoryImpl implements FirestoreRestaurantRepository{


    public void createRestaurant(String placeID, String photoData, String photoWidth, String name,
                                 String vicinity, String type, String rating, Geometry geometry, Details detail, OpeningHours openingHours) {
        RestaurantHelper.createRestaurant(placeID, photoData, photoWidth, name, vicinity, type, rating, geometry, detail, openingHours);
    }

    public void createUserToRestaurant(String placeID, String uid, String username, String urlPicture) {
        RestaurantHelper.createRestaurantUser(placeID, uid, username, urlPicture);
    }

    public void createUserRestaurantLiked(String placeID, String uid, String username, String urlPicture){
        RestaurantHelper.createRestaurantLikedUser(placeID, uid, username, urlPicture);
    }

    @Override
    public void getParticipantsList(String placeID, RestaurantHelper.GetAllUsersCallback callback) {
        RestaurantHelper.getAllUsers(placeID, callback);
    }

    //----------

    @Override
    public void getAllRestaurants(RestaurantHelper.GetAllRestaurantssCallback callback) {
        RestaurantHelper.getAllRestaurants(callback);
    }

    @Override
    public void getUser(String placeID, String uid, RestaurantHelper.GetUserTargetedCallback callback) {
        RestaurantHelper.getTargetedUser(placeID, uid, callback);
    }

    @Override
    public void getTargetedRestaurant(String placeId, RestaurantHelper.GetRestaurantsTargetedCallback callback) {
        RestaurantHelper.getTargetedRestaurant(placeId, callback);
    }

    @Override
    public void getUserRestaurantLiked(String placeID, String uid, RestaurantHelper.GetUserRestaurantLikedCallback callback) {
        RestaurantHelper.getUserRestaurantLiked(placeID, uid, callback);
    }

    public void  updateParticipantNumber(String placeID, boolean addParticipant){
        RestaurantHelper.updateParticipantNumber(placeID, addParticipant);
    }


    public void deleteParticipant(String placeID, String uid) {
        RestaurantHelper.deleteParticipant(placeID, uid);
    }

    public void deleteUserLiked(String placeID, String uid){
        RestaurantHelper.deleteUserRestaurantLiked(placeID, uid);
    }
}
