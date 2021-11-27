package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.guillaume.myapplication.api.UserHelper;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;

import java.util.List;

public class FirestoreUserViewModel extends ViewModel {

    private List<UserFirebase> workmates;
    private UserFirebase mUser;
    private Restaurant mRestaurant;


    public void createUser(String uid, String username, String urlPicture, String radius) {
        UserHelper.createUser(uid, username, urlPicture, radius);
    }

    public void createRestaurant(String uid, String placeID, String photoData, String photoWidth, String name,
                                 String vicinity, String type, String rating) {
        UserHelper.createRestaurant(uid, placeID, photoData, photoWidth, name,
                vicinity, type, rating);
    }

    public LiveData<List<UserFirebase>> getWorkmatesList() {
        MutableLiveData<List<UserFirebase>> usersListLiveData = new MutableLiveData<List<UserFirebase>>();
        UserHelper.getAllUsers(new UserHelper.GetUsersListCallback() {
            @Override
            public void onSuccess(List<UserFirebase> list) {
                workmates = list;
                usersListLiveData.postValue(workmates);
            }

            @Override
            public void onError(Exception exception) {
                usersListLiveData.postValue(null);
            }
        });

        return usersListLiveData;
    }


    public LiveData<UserFirebase> getUser(String uid) {

        MutableLiveData<UserFirebase> getUserLiveData = new MutableLiveData<UserFirebase>();
        UserHelper.getUser(uid, new UserHelper.GetUserCallback() {
            @Override
            public void onSuccess(UserFirebase user) {
                mUser = user;
                getUserLiveData.postValue(mUser);
            }

            @Override
            public void onError(Exception exception) {
                getUserLiveData.postValue(null);
            }
        });

        return getUserLiveData;
    }

    public LiveData<Restaurant> getRestaurant(String uid, String placeID) {

        MutableLiveData<Restaurant> getRestaurantLiveData = new MutableLiveData<Restaurant>();
        UserHelper.getRestaurant(uid, placeID, new UserHelper.GetRestaurantCallback() {
            @Override
            public void onSuccess(Restaurant restaurant) {
                mRestaurant = restaurant;
                getRestaurantLiveData.postValue(mRestaurant);
            }

            @Override
            public void onError(Exception exception) {
                getRestaurantLiveData.postValue(null);
            }
        });
        return getRestaurantLiveData;
    }

    public void updateRestaurantChoosed(String uid, String restaurantChoosed) {
        UserHelper.updateRestaurantChoosed(uid, restaurantChoosed);
    }

    public void updateFieldRestaurantName(String uid, String restaurantName) {
        //todo put a live data and set up it
        UserHelper.updateShieldRestaurantName(uid, restaurantName);
    }

    public void updateRadius(String uid, String currentRadius) {
        UserHelper.updateRadius(uid, currentRadius);
    }

    public void deleteRestaurantChoosed(String uid) {
        UserHelper.deleteRestaurantChoosed(uid);
    }

    public void deleteRestaurantname(String uid) {
        UserHelper.deleteRestaurantname(uid);
    }

    public void deleteRestaurant(String uid, String placeID) {
        UserHelper.deleteRestaurant(uid, placeID);
    }
}
