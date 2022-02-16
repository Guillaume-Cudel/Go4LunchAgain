package com.guillaume.myapplication.viewModel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.guillaume.myapplication.api.UserHelper;
import com.guillaume.myapplication.model.Restaurant;
import com.guillaume.myapplication.model.firestore.UserFirebase;
import com.guillaume.myapplication.repository.FirestoreUserRepository;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FirestoreUserViewModelTest {

    @Mock
    FirestoreUserRepository repository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /*@Captor
    private ArgumentCaptor<UserHelper.GetUserCallback> mGetUserCallbackCaptor;*/

    private FirestoreUserViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new FirestoreUserViewModel(repository);
    }

    @Test
    public void createUser() {
        UserFirebase user = new UserFirebase("1", "Guillaume", null, null);

        viewModel = new FirestoreUserViewModel(repository);
        viewModel.createUser(user.getUid(), user.getUsername(), user.getUrlPicture(), user.getCurrentRadius());

        verify(repository).createUser(any(), any(), any(), any());
    }

    @Test
    public void createRestaurant() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.createRestaurant("2", "22",null, null, "La Boucherie", null, null, "3,9");

        verify(repository).createRestaurant(any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    public void getUsersList() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.getUsersList();

        verify(repository).getUsersList(any());
    }

    @Test
    public void getUser() {
        String uid = "1";

        viewModel = new FirestoreUserViewModel(repository);
        viewModel.getUser(uid);

        verify(repository).getUser(any(), any());
    }


    @Test
    public void getRestaurant() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.getRestaurant("23", "554");

        verify(repository).getRestaurant(any(), any(), any());
    }


    @Test
    public void updateRestaurantChoosed() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.updateRestaurantChoosed("22", "43");

        verify(repository).updateRestaurantChoosed(any(), any());
    }

    @Test
    public void updateFieldRestaurantName() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.updateFieldRestaurantName("s", null);

        verify(repository).updateFieldRestaurantName(any(), any());
    }

    @Test
    public void updateRadius() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.updateRadius("98", "2345");

        verify(repository).updateRadius(any(), any());
    }

    @Test
    public void deleteRestaurantChoosed() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.deleteRestaurantChoosed("22");

        verify(repository).deleteRestaurantChoosed(any());
    }

    @Test
    public void deleteRestaurantname() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.deleteRestaurantname("87");

        verify(repository).deleteRestaurantname(any());
    }

    @Test
    public void deleteRestaurant() {
        viewModel = new FirestoreUserViewModel(repository);
        viewModel.deleteRestaurant("32", "something");

        verify(repository).deleteRestaurant(any(), any());
    }
}