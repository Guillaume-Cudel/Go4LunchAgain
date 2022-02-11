package com.guillaume.myapplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ApplicationProvider;

import com.guillaume.myapplication.repository.FirestoreRestaurantRepository;
import com.guillaume.myapplication.viewModel.FirestoreRestaurantViewModel;

import junit.framework.TestCase;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(JUnit4.class)
public class RestaurantViewModelTest extends TestCase {

    private FirestoreRestaurantViewModel viewModel;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        //viewModel = FirestoreRestaurantViewModel(FirestoreRestaurantRepository());
        //Context context = ApplicationProvider.getApplicationContext();
    }

    /*@Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }*/

}