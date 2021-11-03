package com.guillaume.myapplication.di;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.guillaume.myapplication.repository.RestaurantRepository;
import com.guillaume.myapplication.network.ApiService;
import com.guillaume.myapplication.repository.RestaurantRepositoryImpl;
import com.guillaume.myapplication.viewModel.FirestoreRestaurantViewModel;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;
import com.guillaume.myapplication.viewModel.LocationViewModel;
import com.guillaume.myapplication.viewModel.RestaurantViewModel;
import com.guillaume.myapplication.viewModel.ViewModelFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Injection {

    public static Retrofit provideRetrofit(){

        return new retrofit2.Retrofit.Builder()
                .baseUrl(ApiService.PLACES_API_BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static ApiService provideApiService() {
        Retrofit retrofit = provideRetrofit();
        return retrofit.create(ApiService.class);
    }

    public static RestaurantRepository provideRestaurantRepository(){
        ApiService apiService = provideApiService();
        return new RestaurantRepositoryImpl(apiService);
    }

    public static ViewModelFactory provideViewModelFactory(){
        RestaurantRepository repository = provideRestaurantRepository();
        return new ViewModelFactory(repository);
    }
    public static RestaurantViewModel provideRestaurantViewModel(FragmentActivity activity){
        ViewModelFactory mViewModelFactory = provideViewModelFactory();
        return new ViewModelProvider(activity, mViewModelFactory).get(RestaurantViewModel.class);
    }

    public static LocationViewModel provideLocationViewModel(FragmentActivity activity){
        return new ViewModelProvider(activity).get(LocationViewModel.class);
    }

    public static FirestoreUserViewModel provideFirestoreUserViewModel(FragmentActivity activity){
        return new ViewModelProvider(activity).get(FirestoreUserViewModel.class);
    }

    public static FirestoreRestaurantViewModel provideFirestoreRestaurantViewModel(FragmentActivity activity){
        return new ViewModelProvider(activity).get(FirestoreRestaurantViewModel.class);
    }




}
