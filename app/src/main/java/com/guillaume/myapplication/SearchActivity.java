package com.guillaume.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import com.guillaume.myapplication.di.Injection;
import com.guillaume.myapplication.viewModel.RestaurantViewModel;

public class SearchActivity extends AppCompatActivity {

    private RestaurantViewModel restaurantViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handleIntent(getIntent());
        restaurantViewModel = Injection.provideRestaurantViewModel(this);
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        ...
        handleIntent(intent);
    }*/

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            //todo finish it
            restaurantViewModel.getSearchingRestaurant(query);
        }
    }
}