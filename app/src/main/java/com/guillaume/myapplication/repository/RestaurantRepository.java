package com.guillaume.myapplication.repository;

import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.Restaurant;

import java.util.List;

public interface RestaurantRepository {

    interface GetRestaurantsCallback{
        void onSuccess(List<Restaurant> restaurants);

        void onError(Exception exception);
    }

    void getRestaurants(String location, String radius, String type, String key, GetRestaurantsCallback callback);


    interface GetDetailsCallback{
        void onSuccess(Details details);

        void onError(Exception exception);
    }
    void getDetails(String placeID, String fields, String key, GetDetailsCallback callback);

}
