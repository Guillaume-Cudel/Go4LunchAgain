package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.repository.RestaurantRepository;

import java.util.List;

public class RestaurantViewModel extends ViewModel {

    private static final String API_KEY = "AIzaSyCfIzqvYUkoerRn0a3nnUsLcpooxnZElxI";
    private static final String TYPE = "restaurant";
    private static final String FIELDS = "formatted_phone_number,opening_hours,website";
    private MutableLiveData<List<Restaurant>> restaurantListLiveData;
    private MutableLiveData<Details> detailsLiveData;
    private final MutableLiveData<List<UserFirebase>> _workmatesLiveData = new MutableLiveData<>();
    public LiveData<List<UserFirebase>> workmatesLiveData = _workmatesLiveData;

    private List<UserFirebase> workmatesParticipants;


    private final RestaurantRepository restaurantRepository;

    public RestaurantViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }


    public LiveData<List<Restaurant>> getRestaurants(String location, String radius) {
        if (restaurantListLiveData == null) {
            restaurantListLiveData = new MutableLiveData<List<Restaurant>>();
            loadRestaurants(location, radius);
        }
        return restaurantListLiveData;
    }


    private void loadRestaurants(String location, String radius) {
        restaurantRepository.getRestaurants(location, radius, TYPE, API_KEY, new RestaurantRepository.GetRestaurantsCallback() {
            @Override
            public void onSuccess(List<Restaurant> restaurants) {
                restaurantListLiveData.postValue(restaurants);
            }
            @Override
            public void onError(Exception exception) {
                restaurantListLiveData.postValue(null);
            }
        });
    }

    public LiveData<Details> getDetails(String placeID){
        if (detailsLiveData == null){
            detailsLiveData = new MutableLiveData<Details>();
            loadDetails(placeID);
        }
        return detailsLiveData;
    }

    private void loadDetails(String placeID){
        restaurantRepository.getDetails(placeID, FIELDS, API_KEY, new RestaurantRepository.GetDetailsCallback() {
            @Override
            public void onSuccess(Details details) {
                detailsLiveData.postValue(details);
            }

            @Override
            public void onError(Exception exception) {
                detailsLiveData.postValue(null);
            }
        });
    }

}

