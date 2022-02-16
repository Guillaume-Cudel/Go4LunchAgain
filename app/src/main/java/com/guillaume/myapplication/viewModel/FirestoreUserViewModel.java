package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.guillaume.myapplication.api.UserHelper;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.repository.FirestoreRestaurantRepository;
import com.guillaume.myapplication.repository.FirestoreUserRepository;

import java.util.List;

public class FirestoreUserViewModel extends ViewModel {

    private FirestoreUserRepository firestoreUserRepository;

    public FirestoreUserViewModel(FirestoreUserRepository firestoreUserRepository){
        this.firestoreUserRepository = firestoreUserRepository;
    }


    public void createUser(String uid, String username, String urlPicture, String radius) {
        firestoreUserRepository.createUser(uid, username, urlPicture, radius);
    }

    public void createRestaurant(String uid, String placeID, String photoData, String photoWidth, String name,
                                 String vicinity, String type, String rating) {
        firestoreUserRepository.createRestaurant(uid, placeID, photoData, photoWidth, name,
                vicinity, type, rating);
    }

    public LiveData<List<UserFirebase>> getUsersList() {
        MutableLiveData<List<UserFirebase>> usersListLiveData = new MutableLiveData<List<UserFirebase>>();
        firestoreUserRepository.getUsersList(new UserHelper.GetUsersListCallback() {
            @Override
            public void onSuccess(List<UserFirebase> list) {
                usersListLiveData.postValue(list);
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
        firestoreUserRepository.getUser(uid, new UserHelper.GetUserCallback() {
            @Override
            public void onSuccess(UserFirebase user) {
                //mUser = user;
                getUserLiveData.postValue(user);
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
        firestoreUserRepository.getRestaurant(uid, placeID, new UserHelper.GetRestaurantCallback() {
            @Override
            public void onSuccess(Restaurant restaurant) {
                getRestaurantLiveData.postValue(restaurant);
            }

            @Override
            public void onError(Exception exception) {
                getRestaurantLiveData.postValue(null);
            }
        });
        return getRestaurantLiveData;
    }

    public void updateRestaurantChoosed(String uid, String restaurantChoosed) {
        firestoreUserRepository.updateRestaurantChoosed(uid, restaurantChoosed);
    }

    public void updateFieldRestaurantName(String uid, String restaurantName) {
        firestoreUserRepository.updateFieldRestaurantName(uid, restaurantName);
    }

    public void updateRadius(String uid, String currentRadius) {
        firestoreUserRepository.updateRadius(uid, currentRadius);
    }

    public void deleteRestaurantChoosed(String uid) {
        firestoreUserRepository.deleteRestaurantChoosed(uid);
    }

    public void deleteRestaurantname(String uid) {
        firestoreUserRepository.deleteRestaurantname(uid);
    }

    public void deleteRestaurant(String uid, String placeID) {
        firestoreUserRepository.deleteRestaurant(uid, placeID);
    }
}
