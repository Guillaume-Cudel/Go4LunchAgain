package com.guillaume.myapplication.manager;

import com.google.android.gms.tasks.Task;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.repository.UserRepository;

public class UserManager {

    private static volatile UserManager instance;
    private UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public Task<UserFirebase> getUserData(){
        // Get the user from Firestore and cast it to a User model Object
        return userRepository.getUserData().continueWith(task -> task.getResult().toObject(UserFirebase.class)) ;
    }
}
