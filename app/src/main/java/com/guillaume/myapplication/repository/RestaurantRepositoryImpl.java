package com.guillaume.myapplication.repository;

import com.guillaume.myapplication.model.Details;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.requests.GetDetailsResponse;
import com.guillaume.myapplication.model.requests.GetRestaurantsResponse;
import com.guillaume.myapplication.model.requests.GetSearchingResponse;
import com.guillaume.myapplication.network.ApiService;
import com.guillaume.myapplication.viewModel.RestaurantViewModel;

import java.security.Key;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepositoryImpl implements RestaurantRepository {

    private final ApiService apiService;
    private static final String FIELDS = "formatted_phone_number,opening_hours,website";
    private static final String API_KEY = "AIzaSyCfIzqvYUkoerRn0a3nnUsLcpooxnZElxI";

    public RestaurantRepositoryImpl(ApiService apiService){
        this.apiService = apiService;
    }

    @Override
    public void getRestaurants(String location, String radius, String type, String key, GetRestaurantsCallback callback) {
        Call<GetRestaurantsResponse> call = apiService.getAllRestaurants(location, radius, type, key);
        call.enqueue(new Callback<GetRestaurantsResponse>() {
            @Override
            public void onResponse(Call<GetRestaurantsResponse> call, Response<GetRestaurantsResponse> response) {

                List<Restaurant> listResponse = response.body().getResults();

                for( Restaurant r : listResponse){
                    getDetails( r.getPlace_id(), FIELDS, API_KEY, new GetDetailsCallback() {
                        @Override
                        public void onSuccess(Details details) {
                            r.setDetails(details);
                            callback.onSuccess(listResponse);
                        }
                        @Override
                        public void onError(Exception exception) {
                            r.setDetails(null);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<GetRestaurantsResponse> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    @Override
    public void getDetails(String placeID, String fields, String key, GetDetailsCallback callback){
        Call<GetDetailsResponse> call = apiService.getDetails(placeID, fields, key);
        call.enqueue(new Callback<GetDetailsResponse>() {
            @Override
            public void onResponse(Call<GetDetailsResponse> call, Response<GetDetailsResponse> response) {
                callback.onSuccess(response.body().getResult());
            }

            @Override
            public void onFailure(Call<GetDetailsResponse> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }

    @Override
    public void getSearchingRestaurant(String input, String key, GetSearchingCallback callback) {
        Call<GetSearchingResponse> call = apiService.getSearch(input, key);
        call.enqueue(new Callback<GetSearchingResponse>() {
            @Override
            public void onResponse(Call<GetSearchingResponse> call, Response<GetSearchingResponse> response) {
                callback.onSuccess(response.body().getSearchingResult());
            }

            @Override
            public void onFailure(Call<GetSearchingResponse> call, Throwable t) {
                callback.onError(new Exception(t));
            }
        });
    }


}
