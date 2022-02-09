package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.guillaume.myapplication.api.RestaurantHelper;
import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.model.requests.Geometry;
import com.guillaume.myapplication.model.requests.OpeningHours;
import com.guillaume.myapplication.repository.FirestoreRestaurantRepository;

import java.util.List;

public class FirestoreRestaurantViewModel extends ViewModel {

    private MutableLiveData<UserFirebase> userLiveData;
    private MutableLiveData<List<UserFirebase>> participantsListLiveData;
    private MutableLiveData<List<Restaurant>> restaurantsListLiveData;
    private MutableLiveData<UserFirebase> userLikedLiveData;

    private UserFirebase mUser, mUserLiked;
    private List<UserFirebase> mParticipants;
    private List<Restaurant> mRestaurantsList;

    private final FirestoreRestaurantRepository firestoreRestaurantRepository;

    public FirestoreRestaurantViewModel(FirestoreRestaurantRepository firestoreRestaurantRepository){
        this.firestoreRestaurantRepository = firestoreRestaurantRepository;
    }

    public void createRestaurant(String placeID, String photoData, String photoWidth, String name,
                                 String vicinity, String type, String rating, Geometry geometry, Details detail, OpeningHours openingHours) {
        firestoreRestaurantRepository.createRestaurant(placeID, photoData, photoWidth, name, vicinity, type, rating, geometry, detail, openingHours);
    }

    public void createUserToRestaurant(String placeID, String uid, String username, String urlPicture) {
        firestoreRestaurantRepository.createUserToRestaurant(placeID, uid, username, urlPicture);
    }

    public void createUserRestaurantLiked(String placeID, String uid, String username, String urlPicture){
        firestoreRestaurantRepository.createUserRestaurantLiked(placeID, uid, username, urlPicture);
    }

    //todo to test
    public LiveData<UserFirebase> getUser(String placeID, String uid){
            userLiveData = new MutableLiveData<UserFirebase>();

            //RestaurantHelper.getTargetedUser(placeID, uid, new RestaurantHelper.GetUserTargetedCallback() {
        firestoreRestaurantRepository.getUser(placeID, uid, new FirestoreRestaurantRepository.GetUserTargetedCallback() {
                @Override
                public void onSuccess(UserFirebase user) {
                    mUser = user;
                    userLiveData.postValue(mUser);
                }

                @Override
                public void onError(Exception exception) {
                    userLiveData.postValue(null);
                }
            });
        return userLiveData;
    }

    // ok
    public LiveData<List<UserFirebase>> getNewParticipantsList(String placeID){
        if(participantsListLiveData == null){
            participantsListLiveData = new MutableLiveData<List<UserFirebase>>();
            firestoreRestaurantRepository.getParticipantsList(placeID, new FirestoreRestaurantRepository.GetAllUsersCallback() {
                @Override
                public void onSuccess(List<UserFirebase> list) {
                    mParticipants = list;
                    participantsListLiveData.postValue(mParticipants);
                }

                @Override
                public void onError(Exception exception) {
                    participantsListLiveData.postValue(null);
                }
            });
        }
        return participantsListLiveData;
    }

    // test it
    public LiveData<List<Restaurant>> getAllRestaurants(){
        if(restaurantsListLiveData == null){
            restaurantsListLiveData = new MutableLiveData<List<Restaurant>>();
            //RestaurantHelper.getAllRestaurants(new RestaurantHelper.GetAllRestaurantssCallback() {
            firestoreRestaurantRepository.getAllRestaurants(new FirestoreRestaurantRepository.GetAllRestaurantssCallback() {
                @Override
                public void onSuccess(List<Restaurant> list) {
                    mRestaurantsList = list;
                    restaurantsListLiveData.postValue(mRestaurantsList);
                }

                @Override
                public void onError(Exception exception) {
                    restaurantsListLiveData.postValue(null);
                }
            });
        }
        return restaurantsListLiveData;
    }

    //test it
    public LiveData<UserFirebase> getUserRestaurantLiked(String placeID, String uid){
        userLikedLiveData = new MutableLiveData<UserFirebase>();

        //RestaurantHelper.getUserRestaurantLiked(placeID, uid, new RestaurantHelper.GetUserRestaurantLikedCallback() {
        firestoreRestaurantRepository.getUserRestaurantLiked(placeID, uid, new FirestoreRestaurantRepository.GetUserRestaurantLikedCallback() {
            @Override
            public void onSuccess(UserFirebase user) {
                mUserLiked = user;
                userLikedLiveData.postValue(mUserLiked);
            }

            @Override
            public void onError(Exception exception) {
                userLikedLiveData.postValue(null);
            }
        });
        return userLikedLiveData;
    }

    public void  updateParticipantNumber(String placeID, boolean addParticipant){
        firestoreRestaurantRepository.updateParticipantNumber(placeID, addParticipant);
    }


    public void deleteParticipant(String placeID, String uid) {
        firestoreRestaurantRepository.deleteParticipant(placeID, uid);
    }

    public void deleteUserLiked(String placeID, String uid){
        firestoreRestaurantRepository.deleteUserLiked(placeID, uid);
    }
}
