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
    public void getParticipantsList(String placeID, GetAllUsersCallback callback) {
        RestaurantHelper.getAllUsers(placeID, new RestaurantHelper.GetAllUsersCallback() {
            @Override
            public void onSuccess(List<UserFirebase> list) {
                callback.onSuccess(list);
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        });
    }

    //----------

    @Override
    public void getAllRestaurants(GetAllRestaurantssCallback callback) {
        RestaurantHelper.getAllRestaurants(new RestaurantHelper.GetAllRestaurantssCallback() {
            @Override
            public void onSuccess(List<Restaurant> list) {
                callback.onSuccess(list);
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void getUser(String placeID, String uid, GetUserTargetedCallback callback) {
        RestaurantHelper.getTargetedUser(placeID, uid, new RestaurantHelper.GetUserTargetedCallback() {
            @Override
            public void onSuccess(UserFirebase user) {
                callback.onSuccess(user);
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void getTargetedRestaurant(String placeId, GetRestaurantsTargetedCallback callback) {
        RestaurantHelper.getTargetedRestaurant(placeId, new RestaurantHelper.GetRestaurantsTargetedCallback() {
            @Override
            public void onSuccess(Restaurant restaurant) {
                callback.onSuccess(restaurant);
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        });
    }

    @Override
    public void getUserRestaurantLiked(String placeID, String uid, GetUserRestaurantLikedCallback callback) {
        RestaurantHelper.getUserRestaurantLiked(placeID, uid, new RestaurantHelper.GetUserRestaurantLikedCallback() {
            @Override
            public void onSuccess(UserFirebase user) {
                callback.onSuccess(user);
            }

            @Override
            public void onError(Exception exception) {
                callback.onError(exception);
            }
        });
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
