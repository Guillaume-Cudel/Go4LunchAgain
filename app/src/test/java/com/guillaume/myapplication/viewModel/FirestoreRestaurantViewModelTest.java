package com.guillaume.myapplication.viewModel;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.repository.FirestoreRestaurantRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FirestoreRestaurantViewModelTest {

    @Mock
    FirestoreRestaurantRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FirestoreRestaurantViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new FirestoreRestaurantViewModel(repository);

    }

    @Test
    public void createRestaurant() {
        //when(viewModel.createRestaurant("", "", "", "", "", "", "", geometry, details, openingHours))

        /*doNothing().when(viewModel).createRestaurant("", "", "", "", "",
                "", "", geometry, details, openingHours);*/
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.createRestaurant("id", null, null, "Pizza", null, null, null, null,
                null, null);

        verify(repository).createRestaurant(any(), any(), any(), any(), any(), any(), any(), any(), any(), any());


    }

    @Test
    public void createUserToRestaurant() {
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.createUserToRestaurant("id", "uid", "Guillaume", null);

        verify(repository).createUserToRestaurant(any(), any(), any(), any());
    }

    @Test
    public void createUserRestaurantLiked() {
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.createUserRestaurantLiked("id", "uid", "Guillaume", null);

        verify(repository).createUserRestaurantLiked(any(), any(), any(), any());
    }

    @Test
    public void getUser() {
        /*UserFirebase user = new UserFirebase();
        MutableLiveData<UserFirebase> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(user);

        when(viewModel.getUser("placeId", "uid")).thenReturn(mutableLiveData);*/
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.getUser("id", "uid");

        verify(repository).getUser(any(), any(), any());
    }

    @Test
    public void getNewParticipantsList() {
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.getNewParticipantsList("id");

        verify(repository).getParticipantsList(any(), any());
    }

    @Test
    public void getAllRestaurants() {
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.getAllRestaurants();

        verify(repository).getAllRestaurants(any());
    }

    @Test
    public void getUserRestaurantLiked() {
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.getUserRestaurantLiked("id", "uid");

        verify(repository).getUserRestaurantLiked(any(), any(), any());
    }

    @Test
    public void updateParticipantNumber() {
        boolean isOk = true;
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.updateParticipantNumber("id", isOk);

        verify(repository).updateParticipantNumber(any(), eq(isOk));
    }

    @Test
    public void deleteParticipant() {
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.deleteParticipant("id", "uid");

        verify(repository).deleteParticipant(any(), any());
    }

    @Test
    public void deleteUserLiked() {
        viewModel = new FirestoreRestaurantViewModel(repository);
        viewModel.deleteUserLiked("id", "uid");

        verify(repository).deleteUserLiked(any(), any());
    }
}