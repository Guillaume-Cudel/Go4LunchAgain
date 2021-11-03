package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.guillaume.myapplication.repository.RestaurantRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {


    private final RestaurantRepository repository;


    public ViewModelFactory(RestaurantRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RestaurantViewModel.class)) {
            return (T) new RestaurantViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}