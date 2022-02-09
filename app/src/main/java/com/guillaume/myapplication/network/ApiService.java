package com.guillaume.myapplication.network;

import com.guillaume.myapplication.model.requests.GetDetailsResponse;
import com.guillaume.myapplication.model.requests.GetRestaurantsResponse;
import com.guillaume.myapplication.model.requests.GetRestaurantResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/";

    @GET("nearbysearch/json?")
    Call<GetRestaurantsResponse> getAllRestaurants(@Query("location") String location, @Query("radius") String radius,
                                                   @Query("type") String type, @Query("key") String key);


    // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=43.6536576,1.4419125&radius=1000&type=restaurant&key=AIzaSyBpPAJjNZ2X4q0xz3p_zK_uW3MdZCpD704


    @GET("details/json?")
    Call<GetDetailsResponse> getDetails(@Query("place_id") String placeID, @Query("fields") String fields, @Query("key") String key);

    //https://maps.googleapis.com/maps/api/place/details/json?place_id=ChIJJVG1yI-krhIRHg0g-oZVtcU&fields=formatted_phone_number,opening_hours,website&key=AIzaSyBpPAJjNZ2X4q0xz3p_zK_uW3MdZCpD704

    @GET("autocomplete/json?")
    Call<GetRestaurantResponse> getSearch(@Query("input") String input, @Query("key") String key);


}
