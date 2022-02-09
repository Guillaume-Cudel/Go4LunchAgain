package com.guillaume.myapplication.repository;

import com.guillaume.myapplication.api.UserHelper;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;

import java.util.List;

public class FirestoreUserRepositoryImpl implements FirestoreUserRepository {


    @Override
    public void createUser(String uid, String username, String urlPicture, String radius) {
        UserHelper.createUser(uid, username, urlPicture, radius);
    }

    @Override
    public void createRestaurant(String uid, String placeID, String photoData, String photoWidth, String name, String vicinity, String type, String rating) {
        UserHelper.createRestaurant(uid, placeID, photoData, photoWidth, name,
                vicinity, type, rating);
    }

    @Override
    public void getUsersList(UserHelper.GetUsersListCallback callback) {
        UserHelper.getAllUsers(new UserHelper.GetUsersListCallback() {
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

    @Override
    public void getUser(String uid, UserHelper.GetUserCallback callback) {
        UserHelper.getUser(uid, new UserHelper.GetUserCallback() {
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
    public void getRestaurant(String uid, String placeID, UserHelper.GetRestaurantCallback callback) {
        UserHelper.getRestaurant(uid, placeID, new UserHelper.GetRestaurantCallback() {
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
    public void updateRestaurantChoosed(String uid, String restaurantChoosed) {
        UserHelper.updateRestaurantChoosed(uid, restaurantChoosed);
    }

    @Override
    public void updateFieldRestaurantName(String uid, String restaurantName) {
        UserHelper.updateFieldRestaurantName(uid, restaurantName);
    }

    @Override
    public void updateRadius(String uid, String currentRadius) {
        UserHelper.updateRadius(uid, currentRadius);
    }

    @Override
    public void deleteRestaurantChoosed(String uid) {
        UserHelper.deleteRestaurantChoosed(uid);
    }

    @Override
    public void deleteRestaurantname(String uid) {
        UserHelper.deleteRestaurantname(uid);
    }

    @Override
    public void deleteRestaurant(String uid, String placeID) {
        UserHelper.deleteRestaurant(uid, placeID);
    }


}
