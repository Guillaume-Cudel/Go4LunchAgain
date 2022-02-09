package com.guillaume.myapplication.model.requests;

import com.guillaume.myapplication.model.firestore.UserFirebase;

import java.util.List;

public class GetUsersResponse {

    private List<UserFirebase> result;

    public List<UserFirebase> getUsersResult(){
        return result;
    }
}
