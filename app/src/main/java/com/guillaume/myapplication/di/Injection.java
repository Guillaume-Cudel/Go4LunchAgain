package com.guillaume.myapplication.di;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.guillaume.myapplication.repository.FirestoreRestaurantRepository;
import com.guillaume.myapplication.repository.FirestoreRestaurantRepositoryImpl;
import com.guillaume.myapplication.repository.FirestoreUserRepository;
import com.guillaume.myapplication.repository.FirestoreUserRepositoryImpl;
import com.guillaume.myapplication.repository.RestaurantRepository;
import com.guillaume.myapplication.network.ApiService;
import com.guillaume.myapplication.repository.RestaurantRepositoryImpl;
import com.guillaume.myapplication.viewModel.FirestoreRestaurantVMFactory;
import com.guillaume.myapplication.viewModel.FirestoreRestaurantViewModel;
import com.guillaume.myapplication.viewModel.FirestoreUserVMFactory;
import com.guillaume.myapplication.viewModel.FirestoreUserViewModel;
import com.guillaume.myapplication.viewModel.UtilsViewModel;
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

    public static FirestoreRestaurantRepository provideFirestoreRestaurantRepository(){
        return new FirestoreRestaurantRepositoryImpl();
    }
    /*public static FirestoreRestaurantViewModel provideFirestoreRestaurantViewModel(FragmentActivity activity){
        return new ViewModelProvider(activity).get(FirestoreRestaurantViewModel.class);
    }*/
    public static FirestoreRestaurantVMFactory provideFirestoreRestaurantVMFactory(){
        FirestoreRestaurantRepository repository = provideFirestoreRestaurantRepository();
        return new FirestoreRestaurantVMFactory(repository);
    }

    public static FirestoreRestaurantViewModel provideFirestoreRestaurantViewModel(FragmentActivity activity){
        FirestoreRestaurantVMFactory factory = provideFirestoreRestaurantVMFactory();
        return new ViewModelProvider(activity, factory).get(FirestoreRestaurantViewModel.class);
    }

    public static FirestoreUserRepository provideFirestoreUserRepository(){
        return new FirestoreUserRepositoryImpl();
    }

    public static FirestoreUserVMFactory provideFirestoreUserVMFactory(){
        FirestoreUserRepository repository = provideFirestoreUserRepository();
        return new FirestoreUserVMFactory(repository);
    }
    public static FirestoreUserViewModel provideFirestoreUserViewModel(FragmentActivity activity){
        FirestoreUserVMFactory factory = provideFirestoreUserVMFactory();
        return new ViewModelProvider(activity, factory).get(FirestoreUserViewModel.class);
    }

    //----------------

    public static UtilsViewModel provideUtilsViewModel(FragmentActivity activity){
        return new ViewModelProvider(activity).get(UtilsViewModel.class);
    }

    /*public static FirestoreUserViewModel provideFirestoreUserViewModel(FragmentActivity activity){
        return new ViewModelProvider(activity).get(FirestoreUserViewModel.class);
    }*/






}
