package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.guillaume.myapplication.model.Restaurant;

import java.util.List;

public class LocationViewModel extends ViewModel {

    private final MutableLiveData<LatLng> _locationLiveData = new MutableLiveData<>();
    public LiveData<LatLng> locationLiveData = _locationLiveData;

    private final MutableLiveData<String> _refreshLiveData = new MutableLiveData<>();
    public LiveData<String> refreshLiveData = _refreshLiveData;

    private final MutableLiveData<FirebaseUser> _currentUserLiveData = new MutableLiveData<>();
    public LiveData<FirebaseUser> currentUserLiveData = _currentUserLiveData;

    private final MutableLiveData<List<Restaurant>> _currentRestaurantsDisplayedLiveData = new MutableLiveData<>();
    public LiveData<List<Restaurant>> currentRestaurantsDisplayedLiveData = _currentRestaurantsDisplayedLiveData;

    LatLng mLocation;
    FirebaseUser currentUser;
    List<Restaurant> currentRestaurants;

    public void setLocation(double latitude, double longitude) {
        this.mLocation = new LatLng(latitude, longitude);
        _locationLiveData.postValue(mLocation);
    }

    public void refreshMap(String refresh){
        _refreshLiveData.postValue(refresh);
    }

    public void setCurrentUser(FirebaseUser user){
        this.currentUser = user;
        _currentUserLiveData.postValue(currentUser);
    }

    public void setCurrentRestaurantsDisplayed(List<Restaurant> restaurants){
        this.currentRestaurants = restaurants;
        _currentRestaurantsDisplayedLiveData.postValue(currentRestaurants);
    }

}
