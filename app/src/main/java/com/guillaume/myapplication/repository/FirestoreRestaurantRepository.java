package com.guillaume.myapplication.repository;

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


    interface GetAllUsersCallback {
        void onSuccess(List<UserFirebase> list);

        void onError(Exception exception);
    }
    void getParticipantsList(String placeID, GetAllUsersCallback callback);


    interface GetAllRestaurantssCallback {
        void onSuccess(List<Restaurant> list);

        void onError(Exception exception);
    }
    void getAllRestaurants(GetAllRestaurantssCallback callback);


    interface GetUserTargetedCallback {
        void onSuccess(UserFirebase user);

        void onError(Exception exception);
    }
    void getUser(String placeID, String uid, GetUserTargetedCallback callback);


    interface GetRestaurantsTargetedCallback {
        void onSuccess(Restaurant restaurant);

        void onError(Exception exception);
    }
    void getTargetedRestaurant(String placeId, GetRestaurantsTargetedCallback callback);


    interface GetUserRestaurantLikedCallback {
        void onSuccess(UserFirebase user);

        void onError(Exception exception);
    }
    void getUserRestaurantLiked(String placeID, String uid, GetUserRestaurantLikedCallback callback);

    void  updateParticipantNumber(String placeID, boolean addParticipant);

    void deleteParticipant(String placeID, String uid);

    void deleteUserLiked(String placeID, String uid);

}
