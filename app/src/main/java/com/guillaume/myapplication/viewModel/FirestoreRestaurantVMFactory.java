package com.guillaume.myapplication.viewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.guillaume.myapplication.repository.FirestoreRestaurantRepository;
import com.guillaume.myapplication.repository.RestaurantRepository;

public class FirestoreRestaurantVMFactory implements ViewModelProvider.Factory {

    private final FirestoreRestaurantRepository repository;


    public FirestoreRestaurantVMFactory(FirestoreRestaurantRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FirestoreRestaurantViewModel.class)) {
            return (T) new FirestoreRestaurantViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
