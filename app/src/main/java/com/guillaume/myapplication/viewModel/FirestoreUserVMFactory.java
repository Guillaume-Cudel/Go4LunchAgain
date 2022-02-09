package com.guillaume.myapplication.viewModel;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.guillaume.myapplication.repository.FirestoreRestaurantRepository;
import com.guillaume.myapplication.repository.FirestoreUserRepository;

public class FirestoreUserVMFactory implements ViewModelProvider.Factory {


    private final FirestoreUserRepository repository;


    public FirestoreUserVMFactory(FirestoreUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(FirestoreUserViewModel.class)) {
            return (T) new FirestoreUserViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
