package com.guillaume.myapplication.model.requests;

import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;

public class GetUserResponse {

    private UserFirebase result;

    public UserFirebase getSearchingResult(){
        return result;
    }
}
